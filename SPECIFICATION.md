# Restaurant Rush – Source Specification

Specification derived from the Java sources under `src/main/java`. The program is a turn-based, console (CLI) restaurant management game, organised in an MVC-style layout:

| Package | Role |
|---|---|
| `src.main.java` | Entry point (`Main`) |
| `controller` | Game flow and user-choice orchestration |
| `model` | Domain entities, rules, configuration |
| `view` | Console rendering and input |
| `utils` | Small helpers (padding, grammar, error logging) |

---

## 1. Game Overview

- The player manages a restaurant for a fixed number of **turns** (default 18).
- Each turn: customers arrive, are seated, order, eat, pay; waiters, chefs and cashiers act; unsatisfied customers lose satisfaction and may leave.
- At the end the game shows a **win/lose** screen based on the victory condition, then asks whether to play again.

### Default configuration (`GameConfig()`)

| Parameter | Value |
|---|---|
| Max turns | 18 |
| Tables | 2 |
| Staff | 1 Waiter, 1 Chef, 1 Cashier |
| Arrivals | One `RegularCustomer` on every turn where `turnIndex % 3 == 0` (turns 1, 4, 7, 10, 13, 16 in 1-based display) |
| Menu | Salad – $9, prep 1 unit; Burger – $15, prep 1 unit; Pasta – $18, prep 2 units |
| Victory | `numPaid >= 4` **and** `revenue >= $45` **and** `totalSatisfaction >= 60 * numPaid` |

A second constructor `GameConfig(int maxTurns, int tables, ArrayList<Staff> staff)` allows custom turns/tables/staff. Menu, arrivals and victory rules remain hard-coded (overridable by subclassing).

---

## 2. Entry Point

### `Main`
`main(String[])` creates a `GameController` and calls `startGame()`.

---

## 3. Controller Package

### 3.1 `GameController`

Owns `GameCLI cli`, `GameConfig gameConfig`, `GameState state`, `Scoreboard scoreboard`.

| Method | Description |
|---|---|
| `GameController()` | Builds CLI, default config, fresh state and scoreboard. |
| `startGame()` | Shows title/home screens, then the exit screen. |
| `showTitleAndHomeScreens()` (private) | Shows title; repeatedly prompts home menu (`New Game` / `Exit`) until valid; choice `0` starts a game. |
| `createNewGame()` (private) | Resets state/scoreboard, runs the turn loop, shows verdict, asks to continue (if yes, returns to the home screen). |
| `processArrivals()` | `state.addCustomersToQueues()`; logs new arrivals and queue size. |
| `processPayment()` | For each `Cashier`: takes the first `READY_TO_PAY` customer, logs payment, records on scoreboard, frees the table, marks customer and order paid. |
| `processEating()` | Every `SERVED` customer is logged as finished eating and moved to `READY_TO_PAY`; the turn is stored as `readyToPayTurn`. |
| `showInitStats()` | Shows preliminary stats, waiting queue, table availability, customer satisfactions, order status, available tasks. |
| `processWaiterActions()` | For each `Waiter`: lists valid actions, asks the player (skipped if only one), runs SEAT / SERVE / TAKE_ORDER / WAIT. |
| `processWaiterSeatAction`, `processWaiterServeAction`, `processWaiterTakeOrderAction` | Present the sub-choice (table / ready order / seated customer; auto-selected if only one option) and perform the action. SERVE also calls `scoreboard.recordServed()`. |
| `processChefActions()` / `processChefPrepareAction(chef)` | For each `Chef`: PREPARE (choose a `PREPARING` order) or WAIT. |
| `processSatisfactionLossAndAbandonment()` | Every customer who `canLoseSatisfaction()` loses satisfaction; those who leave free their table and are recorded as departures; departed customers at the front of the queue are removed. |
| `showStats()`, `showVerdict()`, `askContinue()` | End-of-turn scoreboard, end-of-game result, yes/no replay prompt. |

**Turn sequence** (in `createNewGame`):

1. Banner "Turn n/N begins!"
2. Arrivals → Payment → Eating (log output)
3. Initial stats (multiple screens)
4. Waiter actions
5. Chef actions
6. Satisfaction loss & abandonment
7. Scoreboard for the turn
8. "Turn complete" banner, `state.advanceTurn()`

Most steps are separated by a "Press [ENTER] to continue" prompt.

### 3.2 `TryUntilSucceedBlock<T>`

Generic retry helper. Constructed with a `Supplier<T>` (try block) and `Consumer<Exception>` (error handler). `execute()` loops until the supplier returns without throwing, calling the handler for each exception. Used to re-prompt after invalid input.

---

## 4. Model Package

### 4.1 Enumerations

| Enum | Values |
|---|---|
| `CustomerState` | `WAITING`, `SEATED`, `ORDERED`, `SERVED`, `READY_TO_PAY`, `PAID`, `LEFT` |
| `OrderState` | `PLACED`, `PREPARING`, `READY`, `SERVED`, `PAID`, `CANCELLED` |
| `WaiterAction` | `SEAT`, `TAKE_ORDER`, `SERVE`, `WAIT` |
| `ChefAction` | `PREPARE`, `WAIT` |

### 4.2 State machines

**Customer**

```
WAITING → SEATED → ORDERED → SERVED → READY_TO_PAY → PAID
   \         \         \
    └─────────┴─────────┴──► LEFT   (satisfaction reaches 0)
```

**Order**

```
PLACED → PREPARING → READY → SERVED → PAID
(any state except PAID) ──► CANCELLED
```

Invalid customer transitions throw `UncheckedIOException` wrapping an `IOException` with a descriptive message.

### 4.3 `Customer` (abstract)

| Field | Notes |
|---|---|
| `static int firstUnallocatedId` | Global counter; ids are 0-based, displayed as `id + 1`. **Not reset between games.** |
| `id`, `arrivalTurn`, `satisfaction` (starts 100), `state` (starts `WAITING`), `order`, `servedTurn` (-1), `readyToPayTurn` (-1), `customerType` | |

Abstract: `int lossPerWaitingTurn()`, `MenuItem chooseItem(Menu)`.

Key methods:
- `seatCustomer()`, `setOrdered()`, `setServed()`, `setReadyToPay()`, `setPaid()` – guarded state transitions.
- `lowerSatisfaction()` – subtracts `lossPerWaitingTurn()` (floored at 0); at 0 calls `leave()`.
- `leave()` – from `WAITING`/`SEATED`/`ORDERED` sets `LEFT` and cancels any order.
- `canLoseSatisfaction()` – true in `WAITING`, `SEATED`, `ORDERED`.
- `setServedTurn(int)` only sets once; getters for all fields; `hasLeft()`.

### 4.4 `RegularCustomer extends Customer`
- Type string `"Regular Customer"`.
- Loses **8** satisfaction per turn.
- `chooseItem` picks `menu.getItem(id % numItems)` (deterministic rotation).

### 4.5 `Staff` (abstract) and subclasses

`Staff(String staffType)`, `getStaffType()`.

| Class | Type name | Behaviour |
|---|---|---|
| `Waiter` | "Waiter" | `seat(customer, table)` – table must be free and customer `WAITING`. `takeOrder(customer, state)` – customer must be `SEATED`; sets `ORDERED`, picks item from the state's menu, creates an `Order`, registers it and sends it to the kitchen (`PREPARING`). `serve(order, turn)` – order must be `READY` and customer `ORDERED`; customer → `SERVED`, records served turn, order → `SERVED`. |
| `Chef` | "Chef" | `prepare(order)` – only valid for `PREPARING` orders (advances one prep unit); any other state throws. |
| `Cashier` | "Cashier" | `collectPayment(list)` – picks the `READY_TO_PAY` customer with the earliest `readyToPayTurn`, sets customer and order to paid, returns that customer (or `null`). |

Waiter and Chef also offer a constructor taking a custom type name.

### 4.6 `Order`
- Fields: `customer`, `item`, `unitsLeft` (= item preparation units), `state` (`PLACED`).
- `sendToKitchen()` PLACED→PREPARING; `advanceOrderPreparation()` decrements units, becoming `READY` at 0; `markServed()` READY→SERVED; `markPaid()` SERVED→PAID; `markCancelled()` any non-PAID→CANCELLED.
- `getBillCents()` returns the item price.

### 4.7 `Menu` / `MenuItem`
- `MenuItem(name, priceCents, preparationUnits)` with getters.
- `Menu(ArrayList<MenuItem>)`: `getNumItems()`, `getItem(int)`, `findByName(String)` (returns `null` if absent).

### 4.8 `Price`
Package-private utility: `dollars(int)` → cents (×100), `cents(int)` → identity.

### 4.9 `Table`
`id`, `servedCustomer`. `isFree()`, `setCustomer()`, `free()`, `getId()`, `getServedCustomer()`.

### 4.10 `Scoreboard`
Counters: `revenueCents`, `numPaid`, `numServed`, `numDepartures`, `totSatisfaction`.
`recordPayment(cents, satisfaction)`, `recordServed()`, `recordDeparture()` and matching getters.

### 4.11 `GameState`
Holds config, tables, waiting queue (`ArrayDeque`), customer list, order list and `turnIndex` (starts 0). Accessors return defensive copies.

| Method | Description |
|---|---|
| `getTables()`, `getFreeTables()`, `freeTable(customer)` | Table queries; `freeTable` frees the table holding that customer (returns it or `null`). |
| `getMenu()` | `config.getMenu(turnIndex)`. |
| `getStaffOfClassType(Class<T>)` | Typed filter of configured staff. |
| `getWaitingQueue()`, `getCustomerList()` | Copies. |
| `addCustomersToQueues()` | Asks config for arrivals at the current turn; adds to queue and list. |
| `getFirstCustomerFromWaitingQueue()` | Head or `null`. |
| `removeCustomerFromWaitingQueue()` | Pops head; throws if empty. |
| `getFirstCustomerWithState(state)`, `getAllCustomersWithState(state)` | Search by state. |
| `addOrder`, `getOrders`, `getOrdersWithState` | Order tracking. |
| `advanceTurn()`, `getTurnIndex()` | Turn counter. |
| `getValidWaiterActions()` | `SEAT` (queue non-empty and a free table), `TAKE_ORDER` (a `SEATED` customer exists), `SERVE` (a `READY` order exists), `WAIT` (always). |
| `getValidChefActions()` | `PREPARE` (a `PREPARING` order exists), `WAIT` (always). |
| `checkVictory(scoreboard)` | Delegates to `GameConfig.checkVictory`. |

### 4.12 `GameConfig`
See section 1. Public methods: `getNewArrivals(turnIndex)`, `getMaxTurns()`, `getNumTables()`, `getStaff()`, `getMenu(turnIndex)`, `checkVictory(state, scoreboard)`.

---

## 5. View Package

### 5.1 Interfaces

| Interface | Methods |
|---|---|
| `IRendererCLI` | `ArrayList<String> render()`; default `display()` prints each line. |
| `IInputCLI` | `String takeInput(Scanner)` |

### 5.2 Renderers

| Class | Purpose |
|---|---|
| `ContentRendererCLI` | Wraps a list of lines; `getWidth()` returns the longest line. |
| `BoxRendererCLI` | Draws a `+---+` / `\|` border around a child renderer with configurable padding (default 1 vertical, 2 horizontal), optional top border (`activateTop`/`deactivateTop`) and minimum width. |
| `OrderedListRendererCLI` | Renders items as `(1) item`, `(2) item`, … |
| `TableViewCLI` | Renders a column-major data set with headers as an ASCII table; optional index column. Row count is the smallest column length (capped at 1000). |
| `ChoiceRendererCLI` | Optional `[[title]]` plus ordered list; `takeInput` parses a 1-based integer and returns the 0-based index as a string. Throws `UncheckedIOException` on non-integer or out-of-range input. |
| `ChoiceScreenCLI` | Prints a choice list, prompts `>> Input an action:`, delegates to `ChoiceRendererCLI`. |
| `ScoreboardRendererCLI` | Boxed stats: average satisfaction (or `N/A`), total revenue, paid, served and unhappy (departed) customers. |
| `TitleCLI`, `ExitScreenCLI`, `ResultScreenCLI` | ASCII-art title, exit ("Exiting...") and WIN/LOSE screens (the latter followed by the scoreboard). |

### 5.3 `GameCLI` (façade)

Owns the `Scanner(System.in)` and all screens.

- Input: `showHomeScreen()`, `showChoicesScreen(choices, title)` (returns 0-based index), `yesNoInput(msg)` (accepts y/n/yes/no, case-insensitive; throws otherwise), `confirmationInput()` (waits for ENTER), generic `prompt(Supplier, message)`.
- Logging: `log(type, msg)`, `logEvent` (`[Event]: …`), `logGeneric` (`[Log]: …`), `showBoxedMessage`.
- Tables: `showPreliminaryStats` (turn, revenue, average satisfaction), `showWaitingQueue`, `showTableAvailability`, `showCustomerSatisfactions`, `showOrderStatus`, `showAvailableTasks` (Tables 1–6).
- Screens: `showTitleScreen`, `showScoreboardScreen(turn, max, scoreboard)`, `showResultScreen(victory, scoreboard)`, `showExitScreen`.

### 5.4 `PriceFormatter`
`formatPrice(cents)` → `"D.CC"`; `formatPriceWithDollars(cents)` → `"$D.CC"`.

---

## 6. Utils Package

| Class | Methods |
|---|---|
| `Callbacks` | `errorLogger(Throwable)` – walks to the root cause and prints `Error: <message>`. |
| `GrammarPoints` | `nounPluralityQualifier(n)` → `""` if 1 else `"s"`; `linkingVerb(n)` → `"is"`/`"are"`. |
| `StringPadder` | `padIntToLen(num, len)` zero-pads left; `padIntEndToLen(num, len)` pads right with `0`s; `padEndToLen(str, len[, pad])` pads right (spaces by default). |

---

## 7. Input / Error Handling

- All player choices go through `TryUntilSucceedBlock` with `Callbacks::errorLogger`; invalid input prints `Error: …` and re-prompts.
- Menus with a single option are auto-selected without prompting.
- Domain-rule violations raise `UncheckedIOException(IOException(msg))`.

## 8. Notes / Observations

- `Customer.firstUnallocatedId` is static and never reset, so customer ids continue increasing across replayed games (and `RegularCustomer.chooseItem` depends on the id).
- `PriceFormatter.formatPrice` pads the cents on the right (`padIntEndToLen`), so e.g. 905 cents renders as `9.50` rather than `9.05`; current prices are whole dollars so this is not visible yet.
- `processPayment` handles one customer per cashier per turn.
- `Chef.prepare` throws for non-`PREPARING` orders; the controller only offers `PREPARING` orders, so this is a defensive check.

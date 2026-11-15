# sc2002-project
A project for Nanyang Technological University's (NTU) SC2002 course on Object-Oriented Design and Programming

Repository URL: https://github.com/GabeeDeVera/sc2002-project


## Build Instructions
Run `python compile.py` from the root directory to compile the project.

Then, run `python run.py` to run the project. Note that python creates a subprocess that runs the `Main.java` file.

If you run into issues with stale `.class` build artifacts, run `python make_clean.py` to remove all `.class` files in the `src\main\java` directory.

## Stage History

Each stage is preserved as a runnable version using the tags
`stage-1`, `stage-2`, and `stage-3`.

### Stage 1 → Stage 2: VIP and Critic Customers

#### New files

- `src/main/java/model/VIPCustomer.java`
  - Adds a customer type that loses 5 satisfaction points per waiting
    turn and receives a 10% discount.
  - Implements a deterministic menu-choice rule.

- `src/main/java/model/CriticCustomer.java`
  - Adds a customer type that loses 12 satisfaction points per waiting
    turn.
  - Chooses the menu item with the highest preparation-unit requirement.

#### Modified files

- `src/main/java/model/Customer.java`
  - Adds `getPriceMultiplierPercent()` with a default value of 100.
  - Allows subclasses to customise pricing through polymorphism.

- `src/main/java/model/Waiter.java`
  - Calculates the bill using the customer's price multiplier when
    taking an order and passes the calculated amount to `Order`.

- `src/main/java/model/Order.java`
  - Adds a final `billCents` field and a constructor accepting the
    calculated bill.
  - Returns the stored bill through `getBillCents()` so the price is
    fixed when the order is taken.

- `src/main/java/model/GameConfig.java`
  - Updates the arrival schedule to include one VIP and one Critic
    among the six arrivals.

- `README.md`
  - Documents the Stage 2 features and changes.

#### Deleted files

None of the Stage 1 application source files were deleted.

#### Comparison

[Stage 1 → Stage 2](https://github.com/GabeeDeVera/sc2002-project/compare/stage-1...stage-2)

### Stage 2 → Stage 3: Host, Combo Meals and Happy Hour

#### New files

- `src/main/java/model/Host.java`
  - Adds a staff role that automatically seats at most one queued
    customer per turn when a table is available.

- `src/main/java/model/ComboMeal.java`
  - Extends `MenuItem` to represent two existing menu items as one meal.
  - Uses the sum of their preparation units and 90% of their combined
    price, rounded to cents.

#### Modified files

- `src/main/java/model/Customer.java`
  - Adds `getHappyHourRecovery()` and `recoverSatisfaction(int)`.
  - Supports satisfaction recovery capped at 100.

- `src/main/java/model/GameState.java`
  - Tracks the remaining Happy Hour turns.
  - Calculates order prices by applying the customer multiplier first,
    followed by the Happy Hour discount when active.
  - Restores satisfaction to eligible waiting customers during Happy Hour.

- `src/main/java/model/Waiter.java`
  - Delegates price calculation to `GameState.getOrderPriceCents()`
    so customer discounts and Happy Hour use the same pricing calculation.
  - Continues to store the calculated bill in the new order.

- `src/main/java/model/GameConfig.java`
  - Adds a Host to the staff configuration.
  - Adds combo meals to the menu.

- `src/main/java/controller/GameController.java`
  - Adds the manager's Happy Hour activation choice.
  - Tracks whether Happy Hour has already been used during the game.
  - Processes Host actions and triggers Happy Hour satisfaction recovery.

- `src/main/java/view/GameCLI.java`
  - Displays the number of Happy Hour turns remaining.

- `README.md`
  - Documents the Stage 3 features and changes.

#### Deleted files

None of the Stage 2 application source files were deleted.

#### Comparison

[Stage 2 → Stage 3](https://github.com/GabeeDeVera/sc2002-project/compare/stage-2...stage-3)

## Video Timestamps

00.00 [De Vera] Game Introduction

01.27 [Jitsue] Stage 2 change analysis

03.03 [Chaisrisawatsuk] Stage 3 change analysis

05.05 [Dickson] Game Runthrough (One victory & One defeat)

08.22 [Juthasuwansiri] Sequence Diagram

09.31 [De Vera] SOLID principle analysis

12.40 Individual Reflections



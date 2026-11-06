package src.main.java.controller;

import java.util.ArrayList;
import java.util.stream.Collectors;

import src.main.java.model.Cashier;
import src.main.java.model.Chef;
import src.main.java.model.ChefAction;
import src.main.java.model.Customer;
import src.main.java.model.CustomerState;
import src.main.java.model.GameConfig;
import src.main.java.model.GameState;
import src.main.java.model.Order;
import src.main.java.model.OrderState;
import src.main.java.model.Scoreboard;
import src.main.java.model.Table;
import src.main.java.model.Waiter;
import src.main.java.model.WaiterAction;
import src.main.java.utils.Callbacks;
import src.main.java.utils.GrammarPoints;
import src.main.java.view.GameCLI;
import src.main.java.view.PriceFormatter;

public class GameController {  
  private GameCLI cli;
  private GameConfig gameConfig;
  private GameState state;
  private Scoreboard scoreboard;

  public GameController() {
    cli = new GameCLI();
    gameConfig = new GameConfig();
    state = new GameState(
      gameConfig
    );
    scoreboard = new Scoreboard();
  }

  public void startGame() {
    showTitleAndHomeScreens();

    cli.showExitScreen();
  }

  private void showTitleAndHomeScreens() {
    cli.showTitleScreen();

    TryUntilSucceedBlock<Integer> homeScreenInputLoop = new TryUntilSucceedBlock<Integer>(
      cli::showHomeScreen,
      Callbacks::errorLogger
    );

    int homeScreenChoice = homeScreenInputLoop.execute();

    if(homeScreenChoice == 0) {
      createNewGame();
    }
  }

  private void createNewGame() {
    cli.logEvent("Starting a new game");

    // Game Setup
    gameSetup();
    
    // Game Loop
    for(int turnIndex = 0; turnIndex < gameConfig.getMaxTurns(); turnIndex++) {
      cli.showBoxedMessage(
        String.format("Turn %d/%d begins!", state.getTurnIndex() + 1, gameConfig.getMaxTurns())
      );
      
      cli.showBoxedMessage("Arrivals, Payment, and Eating");
      processArrivals();
      processPayment();
      processEating();
      cli.logGeneric("An empty log means that nothing happened.");
      cli.confirmationInput();
      showInitStats();
      cli.confirmationInput();
      processWaiterActions();
      cli.confirmationInput();
      processChefActions();
      cli.confirmationInput();
      processSatisfactionLossAndAbandonment();
      cli.confirmationInput();
      showStats();
      cli.confirmationInput();

      cli.showBoxedMessage(
        String.format("Turn %d/%d complete!", state.getTurnIndex() + 1, gameConfig.getMaxTurns())
      );

      state.advanceTurn();
    }

    // Game Conclusion
    showVerdict();
    
    boolean continueGame = askContinue();
    if(continueGame) {
      showTitleAndHomeScreens();
    }
  }

  private void gameSetup() {
    state = new GameState(gameConfig);
    scoreboard = new Scoreboard();
  }

  private void processArrivals() {
    ArrayList<Customer> newCustomers = state.addCustomersToQueues();
    int numCustomersInQueue = state.getWaitingQueue().size();

    if(newCustomers.size() > 0) {
      cli.logEvent(
        String.format("%d new customer%s joined the queue (there %s now %d customer%s in the queue)", newCustomers.size(), GrammarPoints.nounPluralityQualifier(newCustomers.size()), GrammarPoints.linkingVerb(numCustomersInQueue), numCustomersInQueue, GrammarPoints.nounPluralityQualifier(numCustomersInQueue))
      );
    }
  }

  private void processPayment() {
    for(Cashier cashier : state.getStaffOfClassType(Cashier.class)) {
      Customer customerToPay = state.getFirstCustomerWithState(CustomerState.READY_TO_PAY);
      
      if(customerToPay != null) {
        cli.logEvent(
          String.format(
            "%s %d has paid (+%s revenue, +%d total satisfaction)",
            customerToPay.getCustomerType(),
            customerToPay.getId() + 1,
            PriceFormatter.formatPriceWithDollars(customerToPay.getOrder().getBillCents()),
            customerToPay.getSatisfaction())
        );
        
        scoreboard.recordPayment(customerToPay.getOrder().getBillCents(), customerToPay.getSatisfaction());
        
        // Free the table
        state.freeTable(customerToPay);

        // Set customer and order to paid
        cashier.collectPayment(new ArrayList<Customer>() {
          {
            add(customerToPay);
          }
        });

      }

    }
  }

  private void processEating() {
    for(Customer eatingCustomer : state.getAllCustomersWithState(CustomerState.SERVED)) {
      cli.logGeneric(
        String.format("%s %d has finished eating", eatingCustomer.getCustomerType(), eatingCustomer.getId() + 1)
      );
      eatingCustomer.setReadyToPay();
      eatingCustomer.setReadyToPayTurn(state.getTurnIndex());
    }
  }

  private void showInitStats() {
    // At the start of every turn, show:
    // the turn number, revenue, average satisfaction of paid customers,
    // the waiting queue, occupied tables, each customer’s satisfaction,
    // order status, and the staff tasks available.
    cli.showBoxedMessage("Initial Stats");

    // Show the turn number, revenue, average satisfaction of paid customers
    cli.showPreliminaryStats(state, scoreboard, gameConfig);

    // Show the waiting queue
    cli.showWaitingQueue(state);
    cli.confirmationInput();
    
    // Show the tables
    cli.showTableAvailability(state);
    
    // Show each customer’s satisfaction
    cli.showCustomerSatisfactions(state);
    cli.confirmationInput();

    // Show order statuses
    cli.showOrderStatus(state);

    // Show available staff tasks
    cli.showAvailableTasks(state);
  }

  private void processWaiterActions() {
    cli.showBoxedMessage("Waiter Actions");
    
    int waiterInd = 0;
    for(Waiter waiter : state.getStaffOfClassType(Waiter.class)) {
      // Taking stock of valid actions
      ArrayList<WaiterAction> validActions = state.getValidWaiterActions();
      ArrayList<String> choiceDescriptions = new ArrayList<>();

      for(WaiterAction action : validActions) {
        switch(action) {
          case SEAT:
            choiceDescriptions.add("Seat a customer");
            break;
          case SERVE:
            choiceDescriptions.add("Serve a ready order");
            break;
          case TAKE_ORDER:
            choiceDescriptions.add("Take a customer's order");
            break;
          default:
          case WAIT:
            choiceDescriptions.add("Wait (do nothing)");
            break;
        }
      }
      
      // Taking input
      String screenTitle = String.format("Choose an action for %s %d", waiter.getStaffType(), waiterInd + 1);

      TryUntilSucceedBlock<Integer> waiterChoiceBlock = new TryUntilSucceedBlock<Integer>(
        () -> {
          return cli.showChoicesScreen(
            choiceDescriptions, 
            screenTitle
          );
        },
        Callbacks::errorLogger
      );

      int waiterChoice = 0;

      // Skip asking for input if the waiter only has one action
      if(validActions.size() > 1) {
        waiterChoice = waiterChoiceBlock.execute();
      }
      WaiterAction waiterAction = validActions.get(waiterChoice);
      
      switch(waiterAction) {
        case SEAT:
          cli.logGeneric(
            String.format("%s %d is seating a customer", waiter.getStaffType(), waiterInd + 1)
          );
          processWaiterSeatAction(waiter);
          break;
        case SERVE:
          cli.logGeneric(
            String.format("%s %d is serving a customer", waiter.getStaffType(), waiterInd + 1)
          );
          processWaiterServeAction(waiter);
          break;
        case TAKE_ORDER:
          cli.logGeneric(
            String.format("%s %d is taking an order", waiter.getStaffType(), waiterInd + 1)
          );
          processWaiterTakeOrderAction(waiter);
          break;
        case WAIT:
        default:
          cli.logGeneric(
            String.format("%s %d is resting", waiter.getStaffType(), waiterInd + 1)
          );
          break;
      }

      waiterInd++;
    }
  }

  private void processWaiterSeatAction(Waiter waiter) {
    ArrayList<Table> availableTables = state.getFreeTables();
    ArrayList<String> choiceDescriptions = availableTables.stream()
      .map((Table table) -> {
        return String.format("Table %d", table.getId() + 1);
      })
      .collect(Collectors.toCollection(ArrayList::new));

    Customer nextCustomer = state.getFirstCustomerFromWaitingQueue();
    String screenTitle = String.format("Choose a seat for %s %d", nextCustomer.getCustomerType(), nextCustomer.getId() + 1);

    TryUntilSucceedBlock<Integer> waiterChoiceBlock = new TryUntilSucceedBlock<Integer>(
      () -> {
        return cli.showChoicesScreen(
          choiceDescriptions, 
          screenTitle
        );
      },
      Callbacks::errorLogger
    );

    int waiterChoice = 0;

    // Skip asking for input if the waiter only has one action
    if(availableTables.size() > 1) {
      waiterChoice = waiterChoiceBlock.execute();
    }

    Table chosenTable = availableTables.get(waiterChoice);

    cli.logGeneric(
      String.format(
        "%s %d has been seated at table %d (current satisfaction: %d)",
        nextCustomer.getCustomerType(),
        nextCustomer.getId() + 1,
        chosenTable.getId() + 1,
        nextCustomer.getSatisfaction()
      )
    );
    waiter.seat(nextCustomer, chosenTable);
    state.removeCustomerFromWaitingQueue();
  }

  private void processWaiterServeAction(Waiter waiter) {
    ArrayList<Order> readyOrders = state.getOrdersWithState(OrderState.READY);
    ArrayList<String> choiceDescriptions = readyOrders.stream()
      .map((Order order) -> {
        Customer customer = order.getCustomer();
        return String.format(
          "%s %d's order (type: %s, price: %s, current satisfaction: %d)",
          customer.getCustomerType(),
          customer.getId() + 1,
          order.getItem().getName(),
          PriceFormatter.formatPriceWithDollars(order.getBillCents()),
          customer.getSatisfaction()
        );
      })
      .collect(Collectors.toCollection(ArrayList::new));

    String screenTitle = String.format("Choose an order to serve");

    TryUntilSucceedBlock<Integer> waiterChoiceBlock = new TryUntilSucceedBlock<Integer>(
      () -> {
        return cli.showChoicesScreen(
          choiceDescriptions, 
          screenTitle
        );
      },
      Callbacks::errorLogger
    );

    int waiterChoice = 0;

    // Skip asking for input if the waiter only has one action
    if(readyOrders.size() > 1) {
      waiterChoice = waiterChoiceBlock.execute();
    }

    Order chosenOrder = readyOrders.get(waiterChoice);

    cli.logGeneric(
      String.format(
        "%s %d has been served (current satisfaction: %d)",
        chosenOrder.getCustomer().getCustomerType(),
        chosenOrder.getCustomer().getId() + 1,
        chosenOrder.getCustomer().getSatisfaction()
      )
    );

    scoreboard.recordServed();
    waiter.serve(chosenOrder, state.getTurnIndex());
  }

  private void processWaiterTakeOrderAction(Waiter waiter) {
    ArrayList<Customer> seatedCustomers = state.getAllCustomersWithState(CustomerState.SEATED);
    ArrayList<String> choiceDescriptions = seatedCustomers.stream()
      .map((Customer customer) -> {
        return String.format("%s %d (current satisfaction: %d)", customer.getCustomerType(), customer.getId() + 1, customer.getSatisfaction());
      })
      .collect(Collectors.toCollection(ArrayList::new));

    String screenTitle = String.format("Choose a customer to take an order from");

    TryUntilSucceedBlock<Integer> waiterChoiceBlock = new TryUntilSucceedBlock<Integer>(
      () -> {
        return cli.showChoicesScreen(
          choiceDescriptions, 
          screenTitle
        );
      },
      Callbacks::errorLogger
    );

    int waiterChoice = 0;

    // Skip asking for input if the waiter only has one action
    if(seatedCustomers.size() > 1) {
      waiterChoice = waiterChoiceBlock.execute();
    }

    Customer chosenCustomer = seatedCustomers.get(waiterChoice);

    waiter.takeOrder(chosenCustomer, state);
    cli.logGeneric(
      String.format(
        "%s %d's order has been taken (current satisfaction: %d)",
        chosenCustomer.getCustomerType(),
        chosenCustomer.getId() + 1,
        chosenCustomer.getSatisfaction()
      )
    );

    cli.logGeneric(
      String.format(
        "%s %d ordered \"%s\" (price: %s)",
        chosenCustomer.getCustomerType(),
        chosenCustomer.getId() + 1,
        chosenCustomer.getOrder().getItem().getName(),
        PriceFormatter.formatPriceWithDollars(chosenCustomer.getOrder().getBillCents())
      )
    );
  }

  private void processChefActions() {
    cli.showBoxedMessage("Chef Actions");
    int curChefId = 0;
    for(Chef chef : state.getStaffOfClassType(Chef.class)) {
      ArrayList<ChefAction> validActions = state.getValidChefActions();
      ArrayList<String> choiceDescriptions = new ArrayList<>();

      for(ChefAction action : validActions) {
        switch(action) {
          case PREPARE:
            choiceDescriptions.add("Prepare a dish");
            break;
          default:
          case WAIT:
            choiceDescriptions.add("Wait (do nothing)");
            break;
        }
      }

      // Taking input
      String screenTitle = String.format("Choose an action for %s %d", chef.getStaffType(), curChefId + 1);

      TryUntilSucceedBlock<Integer> chefChoiceBlock = new TryUntilSucceedBlock<Integer>(
        () -> {
          return cli.showChoicesScreen(
            choiceDescriptions, 
            screenTitle
          );
        },
        Callbacks::errorLogger
      );

      int chefChoice = 0;

      // Skip asking for input if the chef only has one action
      if(validActions.size() > 1) {
        chefChoice = chefChoiceBlock.execute();
      }
      ChefAction chefAction = validActions.get(chefChoice);

      switch(chefAction) {
        case PREPARE:
          cli.logGeneric(
            String.format("%s %d is preparing a dish", chef.getStaffType(), curChefId + 1)
          );
          processChefPrepareAction(chef);
          break;
        case WAIT:
        default:
          cli.logGeneric(
            String.format("%s %d is resting", chef.getStaffType(), curChefId + 1)
          );
          break;
      }

      curChefId++;
    }
  }

  private void processChefPrepareAction(Chef chef) {
    ArrayList<Order> preparingOrders = state.getOrdersWithState(OrderState.PREPARING);
    ArrayList<String> choiceDescriptions = preparingOrders.stream()
      .map((Order order) -> {
        Customer customer = order.getCustomer();
        return String.format(
          "%s %d's order (turns until prepared: %d, type: %s, price: %s, current satisfaction: %d)",
          customer.getCustomerType(),
          customer.getId() + 1,
          order.getUnitsLeft(),
          order.getItem().getName(),
          PriceFormatter.formatPriceWithDollars(order.getBillCents()),
          customer.getSatisfaction()
        );
      })
      .collect(Collectors.toCollection(ArrayList::new));

    String screenTitle = String.format("Choose an order to prepare");

    TryUntilSucceedBlock<Integer> chefChoiceBlock = new TryUntilSucceedBlock<Integer>(
      () -> {
        return cli.showChoicesScreen(
          choiceDescriptions, 
          screenTitle
        );
      },
      Callbacks::errorLogger
    );

    int chefChoice = 0;

    // Skip asking for input if the chef only has one action
    if(preparingOrders.size() > 1) {
      chefChoice = chefChoiceBlock.execute();
    }

    Order chosenOrder = preparingOrders.get(chefChoice);

    cli.logGeneric(
      String.format(
        "%s %d's order is being prepared (turns until prepared: %d, current satisfaction: %d)",
        chosenOrder.getCustomer().getCustomerType(),
        chosenOrder.getCustomer().getId() + 1,
        chosenOrder.getUnitsLeft(),
        chosenOrder.getCustomer().getSatisfaction()
      )
    );

    chef.prepare(chosenOrder);
  }

  private void processSatisfactionLossAndAbandonment() {
    cli.showBoxedMessage("Unsatisfied Customers may Leave...");

    int numLeft = 0;
    for(Customer customer : state.getCustomerList()
      .stream()
      .filter((Customer customerToFilter) -> {
        return customerToFilter.canLoseSatisfaction();
      })
      .collect(Collectors.toCollection(ArrayList::new))) {
      customer.lowerSatisfaction();

      if(customer.getState() == CustomerState.LEFT) {
        numLeft++;
        state.freeTable(customer);

        scoreboard.recordDeparture();
      }
    }

    if(numLeft > 0) {
      cli.logEvent(
        String.format("%d unsatisfied customer%s left", numLeft, GrammarPoints.nounPluralityQualifier(numLeft))
      );
    } else {
      cli.logGeneric("No customers left the restaurant");
    }

    // Remove customers who have left from the front of the queue
    while(state.getFirstCustomerFromWaitingQueue() != null && state.getFirstCustomerFromWaitingQueue().getState() == CustomerState.LEFT) {
      state.removeCustomerFromWaitingQueue();
    }
  }

  private void showStats() {
    cli.showScoreboardScreen(state.getTurnIndex(), gameConfig.getMaxTurns(), scoreboard);
  }

  private void showVerdict() {
    cli.showResultScreen(state.checkVictory(scoreboard), scoreboard);
  }

  private boolean askContinue() {
    TryUntilSucceedBlock<Boolean> askContinueInputLoop = new TryUntilSucceedBlock<Boolean>(
      () -> {
        return cli.yesNoInput("Do you want to continue?");
      },
      Callbacks::errorLogger
    );
    return askContinueInputLoop.execute();
  }
}

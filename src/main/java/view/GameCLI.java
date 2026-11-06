package src.main.java.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Supplier;

import src.main.java.model.ChefAction;
import src.main.java.model.Customer;
import src.main.java.model.GameConfig;
import src.main.java.model.GameState;
import src.main.java.model.Order;
import src.main.java.model.OrderState;
import src.main.java.model.Scoreboard;
import src.main.java.model.Table;
import src.main.java.model.WaiterAction;

public class GameCLI {
  private Scanner sc;
  private TitleCLI titleScreen;
  private ChoiceScreenCLI choiceScreen;
  private ScoreboardRendererCLI scoreboardScreen;
  private ResultScreenCLI resultScreen;
  private ExitScreenCLI exitScreen;
  private TableViewCLI tableScreen;

  public GameCLI() {
    sc = new Scanner(System.in);
    titleScreen = new TitleCLI();
    choiceScreen = new ChoiceScreenCLI();
    exitScreen = new ExitScreenCLI();
    resultScreen = new ResultScreenCLI();
    scoreboardScreen = new ScoreboardRendererCLI();
    tableScreen = new TableViewCLI(false, null, null);
  }

  /*
  Note to groupmates: When GameCLI needs to output something,
  prefer to inject a dependency on the required state
  instead of bloating GameCLI with state.
  */

  // Parameterizing the prompt function by return type T
  // to allow for a generic input source
  // and to reduce code duplication
  public <T> T prompt(Supplier<T> inputFn, String message) {
    if(message.equals("")) {
      return inputFn.get();
    } else {
      System.out.printf(">> %s: ", message);
      return inputFn.get();
    }
  }

  // Title screen
  public void showTitleScreen() {
    titleScreen.display();
  }

  // Home screen
  public int showHomeScreen() {
    return showChoicesScreen(
      new ArrayList<>() {
        {
          add("New Game");
          add("Exit");
        }
      },
      "Choose an action"
    );
  }

  // Choices screen
  public int showChoicesScreen(ArrayList<String> choices, String title) {
    choiceScreen.setChoices(choices);
    choiceScreen.setTitle(title);
    return Integer.parseInt(choiceScreen.takeInput(sc));
  }

  // Log screen
  public void log(String logType, String message) {
    System.out.println(
      String.format("[%s]: %s", logType, message)
    );
  }

  // Event screen
  public void logEvent(String event) {
    log("Event", event);
  }

  // Generic log screen
  public void logGeneric(String message) {
    log("Log", message);
  }

  // Boxed message
  public void showBoxedMessage(String message) {
    BoxRendererCLI boxRenderer = new BoxRendererCLI(
      new ContentRendererCLI(
        new ArrayList<String>() {
          {
            add(message);
          }
        }
      )
    );

    boxRenderer.display();
  }

  // Table view
  public void showTableView(ArrayList<ArrayList<String>> data, ArrayList<String> headers) {
    tableScreen.setData(data);
    tableScreen.setHeaders(headers);

    tableScreen.display();
  }

  // Yes or no input
  public boolean yesNoInput(String message) {
    System.out.printf(">> %s (Y/N/Yes/No): ", message);

    String response = sc.nextLine().toLowerCase();

    if(!(response.equals("y") || response.equals("n") || response.equals("yes") || response.equals("no"))) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Unexpected Input '%s'. Please output Y, N, Yes, or No.", response)
        )
      );
    }

    return response.equals("yes") || response.equals("y");
  }

  // Confirmation input
  public void confirmationInput() {
    System.out.print(">> Press [ENTER] to continue: ");
    sc.nextLine();
  }
  
  // Show the turn number, revenue, and average satisfaction of paid customers
  public void showPreliminaryStats(GameState state, Scoreboard scoreboard, GameConfig config) {
    ArrayList<String> dataLabels = new ArrayList<>() {
      {
        add("Turn Number");
        add("Revenue");
        add("Average Satisfaction of Paid Customers");
      }
    };
    
    ArrayList<String> data = new ArrayList<>() {
      {
        add(
          String.format(
            "%d/%d",
            state.getTurnIndex() + 1,
            config.getMaxTurns()
          )
        );
        add(
          PriceFormatter.formatPriceWithDollars(
            scoreboard.getRevenueCents()
          )
        );
        add(
          (scoreboard.getNumPaid() > 0 ? 
          String.format("%1$,.2f", (double)(scoreboard.getTotSatisfaction()) / (double)(scoreboard.getNumPaid()))
          : "N/A"
        )
      );
    }
  };
  
  ArrayList<ArrayList<String>> tableData = new ArrayList<>();
  
  tableData.add(dataLabels);
  tableData.add(data);
  
  ArrayList<String> header = new ArrayList<>() {
    {
      add("Statistic");
      add("Value");
    }
  };
  
    System.out.println("Table 1. Preliminary Statistics");
    showTableView(tableData, header);
    System.out.println();
  }
  
  public void showWaitingQueue(GameState state) {
    ArrayList<ArrayList<String>> data = new ArrayList<>() {
      {
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
      }
    };
    
    ArrayList<String> header = new ArrayList<>() {
      {
        add("Id");
        add("Type");
        add("Satisfaction");
      }
    };
    
    for(Customer customer : state.getWaitingQueue()) {
      data.get(0).add(
        String.valueOf(customer.getId() + 1)
      );
      data.get(1).add(
        customer.getCustomerType()
      );
      data.get(2).add(
        String.valueOf(customer.getSatisfaction())
      );
    }

    System.out.println("Table 2. Waiting Queue");
    showTableView(data, header);
    System.out.println();

  }

  public void showTableAvailability(GameState state) {
    ArrayList<ArrayList<String>> data = new ArrayList<>() {
      {
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
      }
    };
    
    ArrayList<String> header = new ArrayList<>() {
      {
        add("Id");
        add("Available");
        add("Seated Customer");
      }
    };
    
    for(Table table : state.getTables()) {
      data.get(0).add(
        String.valueOf(table.getId() + 1)
      );
      data.get(1).add(
        table.isFree() ? "Yes" : "No"
      );
      data.get(2).add(
        table.isFree()
        ? "FREE"
        : String.format(
          "%d (%s)",
          table.getServedCustomer().getId() + 1,
          table.getServedCustomer().getCustomerType()
        )
      );
    }

    System.out.println("Table 3. Table Availability");
    showTableView(data, header);
    System.out.println();
  }

  public void showCustomerSatisfactions(GameState state) {
    ArrayList<ArrayList<String>> data = new ArrayList<>() {
      {
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
      }
    };
    
    ArrayList<String> header = new ArrayList<>() {
      {
        add("Id");
        add("Type");
        add("Status");
        add("Satisfaction");
      }
    };
    
    for(Customer customer : state.getCustomerList()) {
      data.get(0).add(
        String.valueOf(customer.getId() + 1)
      );
      data.get(1).add(
        customer.getCustomerType()
      );
      data.get(2).add(
        customer.getState().name()
      );
      data.get(3).add(
        String.valueOf(customer.getSatisfaction())
      );
    }

    System.out.println("Table 4. Customer Status and Satisfaction");
    showTableView(data, header);
    System.out.println();
  }

  public void showOrderStatus(GameState state) {
    ArrayList<ArrayList<String>> data = new ArrayList<>() {
      {
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
      }
    };
    
    ArrayList<String> header = new ArrayList<>() {
      {
        add("Status");
        add("Meal");
        add("Prep Units Left");
        add("Customer");
      }
    };
    
    for(Order order : state.getOrders()) {
      data.get(0).add(
        order.getState().name()
      );
      data.get(1).add(
        String.valueOf(order.getItem().getName())
      );
      data.get(2).add(
        String.valueOf(order.getUnitsLeft())
      );
      data.get(3).add(
        String.format(
          "%d (%s)",
          order.getCustomer().getId() + 1,
          order.getCustomer().getCustomerType()
        )
      );
    }

    System.out.println("Table 5. Order Status");
    showTableView(data, header);
    System.out.println();
  }

  public void showAvailableTasks(GameState state) {
    ArrayList<ArrayList<String>> data = new ArrayList<>() {
      {
        add(new ArrayList<>());
        add(new ArrayList<>());
        add(new ArrayList<>());
      }
    };
    
    ArrayList<String> header = new ArrayList<>() {
      {
        add("Action");
        add("Role");
        add("Description");
      }
    };
    
    for(WaiterAction action : state.getValidWaiterActions()) {
      data.get(0).add(
        action.name()
      );
      data.get(1).add(
        "Waiter"
      );
      data.get(2).add(
        switch(action) {
          case SEAT:
            yield "Seat a customer in the waiting queue";
          case SERVE:
            yield "Serve a customer's food";
          case TAKE_ORDER:
            yield "Take a customer's order";
          case WAIT:
          default:
            yield "Do nothing";
        }
      );
    }

    for(ChefAction action : state.getValidChefActions()) {
      data.get(0).add(
        action.name()
      );
      data.get(1).add(
        "Chef"
      );
      data.get(2).add(
        switch(action) {
          case PREPARE:
            yield "Begin/continue preparing an order";
          case WAIT:
          default:
            yield "Do nothing";
        }
      );
    }

    System.out.println("Table 6. Available Tasks");
    showTableView(data, header);
    System.out.println();
  }

  // Show results at the end of a turn
  public void showScoreboardScreen(int turnIndex, int maxTurns, Scoreboard scoreboard) {
    scoreboardScreen.deactivateTop();
    scoreboardScreen.setScoreboard(scoreboard);
    
    ContentRendererCLI turnCounterContentCLI = new ContentRendererCLI(
      new ArrayList<String>() {
        {
          add(
            String.format("Turn %d/%d Results",
              turnIndex + 1,
              maxTurns
            )
          );
        }
      }
    );

    BoxRendererCLI turnCounterCLI = new BoxRendererCLI(
      turnCounterContentCLI
    );

    turnCounterCLI.setMinWidth(scoreboardScreen.getWidth());
    
    turnCounterCLI.display();
    scoreboardScreen.display();
  }

  // Show results at the end of the game
  public void showResultScreen(boolean victory, Scoreboard scoreboard) {
    resultScreen.setVictory(victory);
    resultScreen.setScoreboard(scoreboard);
    resultScreen.display();
  }

  // Exit screen
  public void showExitScreen() {
    exitScreen.display();
  }
}

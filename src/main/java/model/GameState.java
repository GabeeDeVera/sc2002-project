package src.main.java.model;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameState {
  private GameConfig config;
  private ArrayList<Table> tables;
  private ArrayDeque<Customer> customerWaitingQueue;
  private ArrayList<Customer> customerList;
  private ArrayList<Order> orders;
  private int turnIndex;

  public GameState(GameConfig theConfig) {
    config = theConfig;
    int numTables = config.getNumTables();
    tables = new ArrayList<Table>();

    for(int i = 0; i < numTables; i++) {
      tables.add(new Table(i));
    }

    customerWaitingQueue = new ArrayDeque<>();
    customerList = new ArrayList<>();
    orders = new ArrayList<>();

    turnIndex = 0;
  }

  public ArrayList<Table> getTables() {
    // Returning a shallow copy to protect internal data
    return new ArrayList<>(tables);
  }

  public ArrayList<Table> getFreeTables() {
    return tables.stream()
      .filter(
        (Table table) -> {
          return table.isFree();
        }
      )
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public Table freeTable(Customer customer) {
    for(Table table : tables) {
      if(customer.equals(table.getServedCustomer())) {
        table.free();
        return table;
      }
    }
    return null;
  }

  public Menu getMenu() {
    return config.getMenu(turnIndex);
  }

  // Parameterizing by T to remove the need for separate getter methods for each staff type
  public <T extends Staff> ArrayList<T> getStaffOfClassType(Class<T> staffType) {
    // Performing an instanceof check and an
    // explicit downcast through the streams API
    return config.getStaff()
      .stream()
      .filter(staffType::isInstance)
      .map(staffType::cast)
      .collect(Collectors.toCollection(ArrayList::new));
  }

  public ArrayList<Customer> getWaitingQueue() {
    return new ArrayList<>(customerWaitingQueue);
  }

  public ArrayList<Customer> getCustomerList() {
    return new ArrayList<>(customerList);
  }

  // Adds new customers based on the arrival schedule defined in the
  // GameConfig and the current turnIndex
  public ArrayList<Customer> addCustomersToQueues() {
    ArrayList<Customer> newCustomers = config.getNewArrivals(turnIndex);

    customerWaitingQueue.addAll(newCustomers);
    customerList.addAll(newCustomers);

    return newCustomers;
  }

  public Customer getFirstCustomerFromWaitingQueue() {
    return (customerWaitingQueue.size() == 0 ? null : customerWaitingQueue.getFirst());
  }

  public Customer removeCustomerFromWaitingQueue() {
    if(customerWaitingQueue.size() == 0) {
      throw new UncheckedIOException(new IOException("Cannot seat a customer when no customers are waiting to be seated"));
    }

    Customer customerServed = customerWaitingQueue.getFirst();
    customerWaitingQueue.removeFirst();

    return customerServed;
  }

  public Customer getFirstCustomerWithState(CustomerState state) {
    for(Customer customer : customerList) {
      if(customer.getState() == state) {
        return customer;
      }
    }

    return null;
  }

  public ArrayList<Customer> getAllCustomersWithState(CustomerState state) {
    return customerList.stream()
      .filter((Customer customer) -> {return customer.getState() == state;})
      .collect(
        Collectors.toCollection(ArrayList::new)
      );
  }

  public void addOrder(Order order) {
    orders.add(order);
  }

  public ArrayList<Order> getOrders() {
    return new ArrayList<>(orders);
  }

  public ArrayList<Order> getOrdersWithState(OrderState state) {
    return orders.stream()
      .filter((Order order) -> {return order.getState() == state;})
      .collect(
        Collectors.toCollection(ArrayList::new)
      );
  }

  public void advanceTurn() {
    turnIndex++;
  }

  public int getTurnIndex() {
    return turnIndex;
  }

  public ArrayList<WaiterAction> getValidWaiterActions() {
    // Taking stock of valid actions
    ArrayList<WaiterAction> validActions = new ArrayList<>();
    
    /*
    Action: Seat a customer
    Validity conditions:
    1. Customer queue is nonempty
    2. There is at least one empty seat
    */
    if(getFirstCustomerFromWaitingQueue() != null && getFreeTables().size() > 0) {
      validActions.add(WaiterAction.SEAT);
    }

    /*
    Action: Take a customer order
    Validity conditions:
    1. There is at least one customer who is SEATED
    */
    if(getAllCustomersWithState(CustomerState.SEATED).size() > 0) {
      validActions.add(WaiterAction.TAKE_ORDER);
    }

    /*
    Action: Serve one ready order
    Validity conditions:
    1. There is at least one ready order
    */
    if(getOrdersWithState(OrderState.READY).size() > 0) {
      validActions.add(WaiterAction.SERVE);
    }

    /*
    Action: Wait
    Validity conditions: None
    */
    validActions.add(WaiterAction.WAIT);

    return validActions;
  }

  public ArrayList<ChefAction> getValidChefActions() {
    ArrayList<ChefAction> validActions = new ArrayList<>();

    /*
    Action: Prepare an order
    Validity conditions:
    1. At least one order is in the PREPARING state
    */
    if(getOrdersWithState(OrderState.PREPARING).size() > 0) {
      validActions.add(ChefAction.PREPARE);
    }
    
    /*
    Action: Wait
    Validity conditions: None
    */
    validActions.add(ChefAction.WAIT);

    return validActions;
  }

  // Delegating the checking of victory onto the GameConfig object.
  // GameState's sole responsibility is to expose
  // the relevant endpoints to track changing game state.
  // GameConfig is responsible for game configuration and objectives
  public boolean checkVictory(Scoreboard scoreboard) {
    return config.checkVictory(this, scoreboard);
  }
}

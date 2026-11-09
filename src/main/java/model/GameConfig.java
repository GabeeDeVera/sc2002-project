package src.main.java.model;

import java.util.ArrayList;

public class GameConfig {
  private final int maxTurns;
  private final int tables;
  private final ArrayList<Staff> staff;

  public GameConfig(int theMaxTurns, int theTables, ArrayList<Staff> theStaff) {
    maxTurns = theMaxTurns;
    tables = theTables;
    staff = theStaff;
  }

  // Default constructor uses the default values in the project document
  public GameConfig() {
    maxTurns = 18;
    tables = 2;

    staff = new ArrayList<Staff>();
    staff.add(new Waiter());
    staff.add(new Chef());
    staff.add(new Cashier());
  }

  public ArrayList<Customer> getNewArrivals(int turnIndex) {
    ArrayList<Customer> newCustomers = new ArrayList<>();

    if(turnIndex % 3 == 0) {
      int arrivalNumber = turnIndex / 3;
      if(arrivalNumber == 1) {
        newCustomers.add(new VIPCustomer(turnIndex));
      } else if(arrivalNumber == 2) {
        newCustomers.add(new CriticCustomer(turnIndex));
      } else {
        newCustomers.add(new RegularCustomer(turnIndex));
      }
    }

    return newCustomers;
  }

  public int getMaxTurns() {
    return maxTurns;
  }

  public int getNumTables() {
    return tables;
  }

  public ArrayList<Staff> getStaff() {
    return staff;
  }

  // Parameterizing the getMenu getter by the turnIndex to
  // allow for a dynamic menu
  // (eg., the restaurant may have separate breakfast and regular menus)
  public Menu getMenu(int turnIndex) {
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    menuItems.add(new MenuItem("Salad", Price.dollars(9), 1));
    menuItems.add(new MenuItem("Burger", Price.dollars(15), 1));
    menuItems.add(new MenuItem("Pasta", Price.dollars(18), 2));

    return new Menu(menuItems);
  }

  // Checks whether a certain GameState object is in the victory state
  // Override in a subclass to change the victory condition
  // I pass in the GameState as checking for victory is dependent on it (dependency injection)
  public boolean checkVictory(GameState state, Scoreboard scoreboard) {
    // Note: I rearrange the final inequality to avoid precision issues
    // involved with floating-point arithmetic
    return scoreboard.getNumPaid() >= 4
      && scoreboard.getRevenueCents() >= Price.dollars(45)
      && scoreboard.getTotSatisfaction() >= 60 * scoreboard.getNumPaid();
  }
}

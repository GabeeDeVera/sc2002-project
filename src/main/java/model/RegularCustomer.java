package src.main.java.model;

public class RegularCustomer extends Customer {
  public RegularCustomer(int theArrivalTurn) {
    super(theArrivalTurn, "Regular Customer");
  }

  @Override
  public int lossPerWaitingTurn() {
    return 8;
  }

  @Override
  public MenuItem chooseItem(Menu menu) {
    return menu.getItem(getId() % menu.getNumItems());
  }
}

package src.main.java.model;

public class VIPCustomer extends Customer {
  public VIPCustomer(int theArrivalTurn) {
    super(theArrivalTurn, "VIP Customer");
  }

  @Override
  public int lossPerWaitingTurn() {
    return 5;
  }

  @Override
  public MenuItem chooseItem(Menu menu) {
    return menu.getItem((getId() + 1) % menu.getNumItems());
  }

  @Override
  public int getPriceMultiplierPercent() {
    return 90;
  }
}

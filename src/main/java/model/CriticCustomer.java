package src.main.java.model;

public class CriticCustomer extends Customer {
  public CriticCustomer(int theArrivalTurn) {
    super(theArrivalTurn, "Critic Customer");
  }

  @Override
  public int lossPerWaitingTurn() {
    return 12;
  }

  @Override
  public MenuItem chooseItem(Menu menu) {
    MenuItem preferred = menu.getItem(0);
    for(int i = 1; i < menu.getNumItems(); i++) {
      MenuItem candidate = menu.getItem(i);
      if(candidate.getPreparationUnits() > preferred.getPreparationUnits()) {
        preferred = candidate;
      }
    }
    return preferred;
  }
}

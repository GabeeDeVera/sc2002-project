package src.main.java.model;

public class MenuItem {
  private final String name;
  private final int priceCents;
  private final int preparationUnits;

  public MenuItem(String theName, int thePriceCents, int thePreparationUnits) {
    name = theName;
    priceCents = thePriceCents;
    preparationUnits = thePreparationUnits;
  }

  public String getName() {
    return name;
  }

  public int getPriceCents() {
    return priceCents;
  }

  public int getPreparationUnits() {
    return preparationUnits;
  }
}

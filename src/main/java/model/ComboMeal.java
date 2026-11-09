package src.main.java.model;

public class ComboMeal extends MenuItem {
  private final MenuItem firstItem;
  private final MenuItem secondItem;

  public ComboMeal(MenuItem theFirstItem, MenuItem theSecondItem) {
    super(
      theFirstItem.getName() + " + " + theSecondItem.getName() + " Combo",
      (int)Math.round((theFirstItem.getPriceCents() + theSecondItem.getPriceCents()) * 0.90),
      theFirstItem.getPreparationUnits() + theSecondItem.getPreparationUnits()
    );
    firstItem = theFirstItem;
    secondItem = theSecondItem;
  }

  public MenuItem getFirstItem() { return firstItem; }
  public MenuItem getSecondItem() { return secondItem; }
}

package src.main.java.model;

public class Order {
  private Customer customer;
  private MenuItem item;
  private int unitsLeft;
  private OrderState state;

  public Order(Customer theCustomer, MenuItem theItem) {
    customer = theCustomer;
    item = theItem;
    unitsLeft = theItem.getPreparationUnits();
    state = OrderState.PLACED;
  }

  public void sendToKitchen() {
    if(state == OrderState.PLACED) {
      state = OrderState.PREPARING;
    }
  }

  public void advanceOrderPreparation() {
    if(state == OrderState.PREPARING) {
      unitsLeft = Math.max(unitsLeft - 1, 0);

      if(unitsLeft == 0) {
        state = OrderState.READY;
      }
    }
  }

  public void markServed() {
    if(state == OrderState.READY) {
      state = OrderState.SERVED;
    }
  }

  public void markPaid() {
    if(state == OrderState.SERVED) {
      state = OrderState.PAID;
    }
  }

  public void markCancelled() {
    if(state != OrderState.PAID) {
      state = OrderState.CANCELLED;
    }
  }

  public int getBillCents() {
    return item.getPriceCents();
  }

  public OrderState getState() {
    return state;
  }

  public MenuItem getItem() {
    return item;
  }

  public Customer getCustomer() {
    return customer;
  }

  public int getUnitsLeft() {
    return unitsLeft;
  }
}

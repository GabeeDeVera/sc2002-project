package src.main.java.model;

import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class Customer {
  private static int firstUnallocatedId;
  private int id;
  private int arrivalTurn;
  private int satisfaction;
  private CustomerState state;
  private Order order;
  private int servedTurn;
  private int readyToPayTurn;
  private final String customerType;

  protected Customer(int theArrivalTurn, String theCustomerType) {
    id = firstUnallocatedId++;
    arrivalTurn = theArrivalTurn;
    satisfaction = 100;
    state = CustomerState.WAITING;
    order = null;
    servedTurn = readyToPayTurn = -1;
    customerType = theCustomerType;
  }

  public abstract int lossPerWaitingTurn();

  public abstract MenuItem chooseItem(Menu menu);

  public int getPriceMultiplierPercent() {
    return 100;
  }

  public int getHappyHourRecovery() {
    return 15;
  }

  public void leave() {
    if(state == CustomerState.WAITING || state == CustomerState.SEATED || state == CustomerState.ORDERED) {
      state = CustomerState.LEFT;
      if(order != null) order.markCancelled();
    }
  }

  public void lowerSatisfaction() {
    satisfaction = Math.max(0, satisfaction - lossPerWaitingTurn());

    if(satisfaction <= 0) {
      leave();
    }
  }

  public void recoverSatisfaction(int amount) {
    satisfaction = Math.min(100, satisfaction + amount);
  }

  public boolean canLoseSatisfaction() {
    return state == CustomerState.WAITING || state == CustomerState.SEATED || state == CustomerState.ORDERED;
  }

  public int getSatisfaction() {
    return satisfaction;
  }

  public CustomerState getState() {
    return state;
  }

  public void seatCustomer() {
    if(state == CustomerState.WAITING) {
      state = CustomerState.SEATED;
    } else {
      throw new UncheckedIOException(new IOException("Only WAITING customers may be seated"));
    }
  }

  public void setOrdered() {
    if(state == CustomerState.SEATED) {
      state = CustomerState.ORDERED;
    } else {
      throw new UncheckedIOException(new IOException("Only SEATED customers may take orders"));
    }
  }

  public void setServed() {
    if(state == CustomerState.ORDERED) {
      state = CustomerState.SERVED;
    } else {
      throw new UncheckedIOException(new IOException("Only customers who have ORDERED may be served"));
    }
  }

  public void setReadyToPay() {
    if(state == CustomerState.SERVED) {
      state = CustomerState.READY_TO_PAY;
    } else {
      throw new UncheckedIOException(new IOException("Only customers who have been SERVED may become ready to pay"));
    }
  }

  public void setPaid() {
    if(state == CustomerState.READY_TO_PAY) {
      state = CustomerState.PAID;
    } else {
      throw new UncheckedIOException(new IOException("Only customers who are READY TO PAY may pay"));
    }
  }

  public boolean hasLeft() {
    return state == CustomerState.LEFT;
  }

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order newOrder) {
    order = newOrder;
  }

  public int getArrivalTurn() {
    return arrivalTurn;
  }

  public int getServedTurn() {
    return servedTurn;
  }

  public void setServedTurn(int newServedTurn) {
    if(servedTurn == -1) {
      servedTurn = newServedTurn;
    }
  }

  public int getReadyToPayTurn() {
    return readyToPayTurn;
  }

  public void setReadyToPayTurn(int newReadyToPayTurn) {
    readyToPayTurn = newReadyToPayTurn;
  }

  public int getId() {
    return id;
  }

  public String getCustomerType() {
    return customerType;
  }
}

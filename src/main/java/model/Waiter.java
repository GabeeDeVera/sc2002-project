package src.main.java.model;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Waiter extends Staff {
  public Waiter() {
    super("Waiter");
  }

  public Waiter(String theStaffType) {
    super(theStaffType);
  }

  public void seat(Customer customer, Table table) {
    if(!table.isFree()) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Table %d is already occupied", table.getId())
        )
      );
    }

    if(customer.getState() != CustomerState.WAITING) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Customer %d is not waiting to be seated", customer.getId())
        )
      );
    }
    
    table.setCustomer(customer);
    customer.seatCustomer();
  }

  public void takeOrder(Customer customer, GameState gameState) {
    if(customer.getState() != CustomerState.SEATED) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Customer %d is not currently seated", customer.getId())
        )
      );
    }

    // Change customer state first before doing anything
    // In case we need to fail early
    customer.setOrdered();

    MenuItem orderedItem = customer.chooseItem(gameState.getMenu());

    Order newOrder = new Order(customer, orderedItem);

    gameState.addOrder(newOrder);
    customer.setOrder(newOrder);
    newOrder.sendToKitchen();
  }

  public void serve(Order order, int currentTurn) {
    if(order.getState() != OrderState.READY) {
      throw new UncheckedIOException(
        new IOException("The order is not ready yet")
      );
    }

    Customer customer = order.getCustomer();

    if(customer.getState() != CustomerState.ORDERED) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Cannot serve customer %d who has not been served yet", customer.getId())
        )
      );
    }

    customer.setServed();
    customer.setServedTurn(currentTurn);
    order.markServed();
  }
}

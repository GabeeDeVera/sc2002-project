package src.main.java.model;

public class Host extends Staff {
  public Host() {
    super("Host");
  }

  public boolean seatNextCustomer(GameState state) {
    Customer customer = state.getFirstCustomerFromWaitingQueue();
    if(customer == null || state.getFreeTables().size() == 0) {
      return false;
    }
    Table table = state.getFreeTables().get(0);
    table.setCustomer(customer);
    customer.seatCustomer();
    state.removeCustomerFromWaitingQueue();
    return true;
  }
}

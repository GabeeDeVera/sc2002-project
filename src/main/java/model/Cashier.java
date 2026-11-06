package src.main.java.model;

import java.util.ArrayList;

public class Cashier extends Staff {
  public Cashier() {
    super("Cashier");
  }

  public Customer collectPayment(ArrayList<Customer> customers) {
    int numCustomers = customers.size();
    
    Customer earliestCustomer = null;
    
    for(int i = 0; i < numCustomers; i++) {
      if(customers.get(i).getState() == CustomerState.READY_TO_PAY) {
        if(earliestCustomer == null || customers.get(i).getReadyToPayTurn() < earliestCustomer.getReadyToPayTurn()) {
          earliestCustomer = customers.get(i);
        }
      }
    }

    if(earliestCustomer != null) {
      earliestCustomer.setPaid();
      earliestCustomer.getOrder().markPaid();
    }

    return earliestCustomer;
  }
}

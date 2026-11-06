package src.main.java.model;

public class Table {
  private int id;
  private Customer servedCustomer;

  public Table(int theId) {
    id = theId;
    servedCustomer = null;
  }

  public Customer getServedCustomer() {
    return servedCustomer;
  }

  public void setCustomer(Customer newCustomer) {
    servedCustomer = newCustomer;
  }
  
  public void free() {
    servedCustomer = null;
  }

  public int getId() {
    return id;
  }

  public boolean isFree() {
    return servedCustomer == null;
  }
}

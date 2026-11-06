package src.main.java.model;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Chef extends Staff {
  public Chef() {
    super("Chef");
  }

  public Chef(String theStaffType) {
    super(theStaffType);
  }

  public void prepare(Order order) {
    
    switch(order.getState()) {
      case PREPARING:
        {
          order.advanceOrderPreparation();  
        }  
        break;
      case READY:
        {
          throw new UncheckedIOException(new IOException("A chef cannot advance an order that has already been prepared"));
        }  
      case PAID:
        {
          throw new UncheckedIOException(new IOException("A chef cannot advance an order that has already been paid for"));
        }  
      case SERVED:
        {
          throw new UncheckedIOException(new IOException("A chef cannot advance an order that has already been served"));
        }
      case CANCELLED:
        {
          throw new UncheckedIOException(new IOException("A chef cannot advance an order that has been cancelled"));
        }
      default:
      case PLACED:
        {
          throw new UncheckedIOException(new IOException("A chef cannot advance an order that is not in the kitchen"));
        }        
    }
  }
}

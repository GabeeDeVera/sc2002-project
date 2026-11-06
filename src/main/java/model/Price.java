package src.main.java.model;

// This class exists to make prices more readable and enhance pricing semantics
public final class Price {
  private static final int CENTS_PER_DOLLAR = 100; 

  static int dollars(int amountDollars) {
    return amountDollars * CENTS_PER_DOLLAR;
  }

  static int cents(int amountCents) {
    return amountCents;
  }
}

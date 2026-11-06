package src.main.java.model;

public class Scoreboard {
  private int revenueCents;
  private int numPaid;
  private int numServed;
  private int numDepartures;
  private int totSatisfaction;

  public Scoreboard() {
    revenueCents = numPaid = numServed = numDepartures = totSatisfaction = 0;
  }

  public void recordPayment(int paymentCents, int satisfaction) {
    numPaid++;
    revenueCents += paymentCents;
    totSatisfaction += satisfaction;
  }

  public void recordServed() {
    numServed++;
  }

  public void recordDeparture() {
    numDepartures++;
  }

  public int getRevenueCents() {
    return revenueCents;
  }

  public int getNumPaid() {
    return numPaid;
  }

  public int getNumServed() {
    return numServed;
  }

  public int getNumDepartures() {
    return numDepartures;
  }

  public int getTotSatisfaction() {
    return totSatisfaction;
  }
}

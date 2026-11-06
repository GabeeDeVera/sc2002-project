package src.main.java.model;

public abstract class Staff {
  private String staffType;

  public Staff(String theStaffType) {
    staffType = theStaffType;
  }

  public String getStaffType() {
    return staffType;
  }
}

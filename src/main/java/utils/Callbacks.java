package src.main.java.utils;

import java.util.function.Consumer;

public class Callbacks {
  public static void errorLogger(Throwable e) {
    Throwable curThrowable = e;
    while(curThrowable.getCause() != null) {
      curThrowable = curThrowable.getCause();
    }
    System.out.printf("Error: %s%n", curThrowable.getMessage());
  }
}

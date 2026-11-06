package src.main.java.utils;

public class StringPadder {
  public static String padIntToLen(int num, int len) {
    return String.format(
      String.format("%%0%dd", len),
      num);
  }

  public static String padIntEndToLen(int num, int len) {
    return StringPadder.padEndToLen(String.valueOf(num), len, "0");
  }

  public static String padEndToLen(String val, int len) {
    return val + " ".repeat(Math.max(0, len - val.length()));
  }

  public static String padEndToLen(String val, int len, String padString) {
    return val + padString.repeat(Math.max(0, len - val.length()));
  }
}

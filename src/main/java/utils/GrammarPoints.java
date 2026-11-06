package src.main.java.utils;

public class GrammarPoints {
  public static String nounPluralityQualifier(int count) {
    return (count == 1 ? "" : "s");
  }

  public static String linkingVerb(int count) {
    return (count == 1 ? "is" : "are");
  }
}

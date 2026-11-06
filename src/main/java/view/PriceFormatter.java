package src.main.java.view;

import src.main.java.utils.StringPadder;

public class PriceFormatter {
  public static String formatPrice(int priceCents) {
    return String.format("%d.%s", priceCents / 100, StringPadder.padIntEndToLen(priceCents % 100, 2));
  }

  public static String formatPriceWithDollars(int priceCents) {
    return "$" + PriceFormatter.formatPrice(priceCents);
  }
}

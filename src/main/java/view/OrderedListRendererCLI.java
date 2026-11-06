package src.main.java.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderedListRendererCLI implements IRendererCLI {
  private ArrayList<String> items;

  public OrderedListRendererCLI(ArrayList<String> theItems) {
    items = theItems;
  };

  @Override
  public ArrayList<String> render() {
    return IntStream
      .range(0, items.size())
      .mapToObj((int i) -> {
        return String.format("(%d) %s", i + 1, items.get(i));
      })
      .collect(Collectors.toCollection(ArrayList::new));
  }
  
  public void setItems(ArrayList<String> newItems) {
    items = newItems;
  }
}

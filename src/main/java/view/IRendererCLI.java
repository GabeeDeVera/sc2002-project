package src.main.java.view;

import java.util.ArrayList;

public interface IRendererCLI {
  public ArrayList<String> render();

  public default void display() {
    ArrayList<String> displayData = render();

    for(String str : displayData) {
      System.out.println(str);
    }
  }
}

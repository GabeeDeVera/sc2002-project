package src.main.java.model;

import java.util.ArrayList;

public class Menu {
  private final ArrayList<MenuItem> items;

  public Menu(ArrayList<MenuItem> theItems) {
    items = theItems;
  }

  public int getNumItems() {
    return items.size();
  }

  public MenuItem getItem(int itemIndex) {
    return items.get(itemIndex);
  }

  public MenuItem findByName(String name) {
    for(MenuItem item : items) {
      if(item.getName().equals(name)) {
        return item;
      }
    }

    return null;
  }
}

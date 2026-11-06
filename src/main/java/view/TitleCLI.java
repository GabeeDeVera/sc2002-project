package src.main.java.view;

import java.util.ArrayList;

public class TitleCLI implements IRendererCLI {
  private static final ArrayList<String> startScreenTitle = new ArrayList<>() {
    {
      add(" ___        _                          _   ");
      add("| _ \\___ __| |_ __ _ _  _ _ _ __ _ _ _| |_ ");
      add("|   / -_|_-<  _/ _` | || | '_/ _` | ' \\  _|");
      add("|_|_\\___/__/\\__\\__,_|\\_,_|_| \\__,_|_||_\\__|");
      add("| _ \\_  _ __| |_                           ");
      add("|   / || (_-< ' \\                          ");
      add("|_|_\\\\_,_/__/_||_|         By the Lab Gang ");
      add("                                           ");
    }
  };
  private BoxRendererCLI titleBox;

  public TitleCLI() {
    titleBox = new BoxRendererCLI(
      new ContentRendererCLI(startScreenTitle)
    );
  }

  @Override
  public ArrayList<String> render() {
    return titleBox.render();
  }
}

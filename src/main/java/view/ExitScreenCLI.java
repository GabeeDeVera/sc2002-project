package src.main.java.view;

import java.util.ArrayList;

public class ExitScreenCLI implements IRendererCLI {
  private static final ArrayList<String> exitScreenTitle = new ArrayList<>() {
    {
      add(" _____ _              _          __         ");
      add("|_   _| |_  __ _ _ _ | |__ ___  / _|___ _ _ ");
      add("  | | | ' \\/ _` | ' \\| / /(_-< |  _/ _ \\ '_|");
      add(" _|_|_|_||_\\__,_|_||_|_\\_\\/__/ |_|_\\___/_|  ");
      add("| _ \\ |__ _ _  _(_)_ _  __ _   __\\ \\        ");
      add("|  _/ / _` | || | | ' \\/ _` | |___| |       ");
      add("|_| |_\\__,_|\\_, |_|_||_\\__, | |___| |       ");
      add("            |__/       |___/     /_/        ");
      add("                                            ");
      add("                                 Exiting... ");
    }
  };
  private BoxRendererCLI titleBox;

  public ExitScreenCLI() {
    titleBox = new BoxRendererCLI(
      new ContentRendererCLI(exitScreenTitle)
    );
  }

  @Override
  public ArrayList<String> render() {
    return titleBox.render();
  }
}

package src.main.java.view;

import java.util.ArrayList;

import src.main.java.model.Scoreboard;

public class ResultScreenCLI implements IRendererCLI {
  private static final ArrayList<String> winScreenTitle = new ArrayList<>() {
    {
      add("$$\\      $$\\ $$\\           $$\\ ");
      add("$$ | $\\  $$ |\\__|          $$ |");
      add("$$ |$$$\\ $$ |$$\\ $$$$$$$\\  $$ |");
      add("$$ $$ $$\\$$ |$$ |$$  __$$\\ $$ |");
      add("$$$$  _$$$$ |$$ |$$ |  $$ |\\__|");
      add("$$$  / \\$$$ |$$ |$$ |  $$ |    ");
      add("$$  /   \\$$ |$$ |$$ |  $$ |$$\\ ");
      add("\\__/     \\__|\\__|\\__|  \\__|\\__|");
    }
  };

  private static final ArrayList<String> loseScreenTitle = new ArrayList<>() {
    {
      add("                         .-')      ('-.            ");
      add("                        ( OO ).  _(  OO)           ");
      add(" ,--.      .-'),-----. (_)---\\_)(,------.          ");
      add(" |  |.-') ( OO'  .-.  '/    _ |  |  .---'          ");
      add(" |  | OO )/   |  | |  |\\  :` `.  |  |              ");
      add(" |  |`-' |\\_) |  |\\|  | '..`''.)(|  '--.           ");
      add("(|  '---.'  \\ |  | |  |.-._)   \\ |  .--'           ");
      add(" |      |    `'  '-'  '\\       / |  `---..-..-..-. ");
      add(" `------'      `-----'  `-----'  `------'`-'`-'`-' ");
    }
  };

  private BoxRendererCLI winBox;
  private BoxRendererCLI loseBox;
  private ScoreboardRendererCLI statsBox;

  private boolean victory;
  private Scoreboard scoreboard;

  public ResultScreenCLI() {
    winBox = new BoxRendererCLI(
      new ContentRendererCLI(winScreenTitle)
    );

    loseBox = new BoxRendererCLI(
      new ContentRendererCLI(loseScreenTitle)
    );

    statsBox = new ScoreboardRendererCLI();
    statsBox.deactivateTop();

    victory = false;
    scoreboard = new Scoreboard();
  }

  @Override
  public ArrayList<String> render() {
    ArrayList<String> renderResult = new ArrayList<>();
    renderResult.addAll((victory ? winBox.render() : loseBox.render()));
    renderResult.addAll(statsBox.render());

    return renderResult;
  }

  public void setVictory(boolean newVictory) {
    victory = newVictory;
    statsBox.setMinWidth((victory ? winScreenTitle : loseScreenTitle).get(0).length());
  }

  public void setScoreboard(Scoreboard newScoreboard) {
    scoreboard = newScoreboard;
    statsBox.setScoreboard(scoreboard);
  }
}

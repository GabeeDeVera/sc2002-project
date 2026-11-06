package src.main.java.view;

import java.util.ArrayList;

import src.main.java.model.Scoreboard;

public class ScoreboardRendererCLI implements IRendererCLI {
  private Scoreboard scoreboard;
  private BoxRendererCLI boxRenderer;
  private ContentRendererCLI contentRenderer;

  public ScoreboardRendererCLI() {
    scoreboard = new Scoreboard();
    contentRenderer = new ContentRendererCLI(new ArrayList<String>() {
      {
        add(" ");
      }
    });
    boxRenderer = new BoxRendererCLI(contentRenderer);
  }

  @Override
  public ArrayList<String> render() {
    ArrayList<String> stats = new ArrayList<>();

    
    
    if(scoreboard.getNumPaid() > 0) {
      stats.add(
        String.format("Average Satisfaction: %1$,.2f", (double)(scoreboard.getTotSatisfaction()) / (double)(scoreboard.getNumPaid()))
      );
    } else {
      stats.add("Average Satisfaction: N/A");
    }

    stats.add(
      String.format("Total Revenue: %s", PriceFormatter.formatPriceWithDollars(scoreboard.getRevenueCents()))
    );

    stats.add(
      String.format("Paid Customers: %d", scoreboard.getNumPaid())
    );

    stats.add(
      String.format("Served Customers: %d", scoreboard.getNumServed())
    );

    stats.add(
      String.format("Unhappy Customers: %d", scoreboard.getNumDepartures())
    );

    contentRenderer.setContent(stats);
    return boxRenderer.render();
  }

  public int getWidth() {
    render(); // Force rerender to ensure width is updated
    return contentRenderer.getWidth();
  }

  public void setScoreboard(Scoreboard newScoreboard) {
    scoreboard = newScoreboard;
  }

  public void activateTop() {
    boxRenderer.activateTop();
  }

  public void deactivateTop() {
    boxRenderer.deactivateTop();
  }

  public boolean isTopActivated() {
    return boxRenderer.isTopActivated();
  }

  public void setMinWidth(int minWidth) {
    boxRenderer.setMinWidth(minWidth);
  }
}

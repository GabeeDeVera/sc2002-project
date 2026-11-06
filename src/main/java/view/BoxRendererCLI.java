package src.main.java.view;

import java.util.ArrayList;

import src.main.java.utils.StringPadder;

public class BoxRendererCLI implements IRendererCLI {
  private static final String HORIZONTAL_DIVIDER = "-"; 
  private static final String VERTICAL_DIVIDER = "|"; 
  private static final String TABLE_JUNCTION = "+";

  private int topPadding;
  private int bottomPadding;
  private int leftPadding;
  private int rightPadding;
  private IRendererCLI renderableChild;
  private boolean renderTop;
  private int minWidth;

  public BoxRendererCLI(IRendererCLI theRenderableChild, int theTopPadding, int theBottomPadding, int theLeftPadding, int theRightPadding) {
    topPadding = theTopPadding;
    bottomPadding = theBottomPadding;
    leftPadding = theLeftPadding;
    rightPadding = theRightPadding;
    renderableChild = theRenderableChild;
    renderTop = true;
    minWidth = 0;
  }
  
  public BoxRendererCLI(IRendererCLI theRenderableChild) {
    topPadding = bottomPadding = 1;
    leftPadding = rightPadding = 2;
    renderableChild = theRenderableChild;
    renderTop = true;
    minWidth = 0;
  }

  public ArrayList<String> render() {
    ArrayList<String> childRender = renderableChild.render();
    int width = childRender.stream()
      .map((String str) -> {return str.length();})
      .reduce(1, Math::max);
    width = Math.max(width, minWidth);

    String horizontalBorder = BoxRendererCLI.TABLE_JUNCTION + BoxRendererCLI.HORIZONTAL_DIVIDER.repeat(width + leftPadding + rightPadding) + BoxRendererCLI.TABLE_JUNCTION;
    String emptyRow = BoxRendererCLI.VERTICAL_DIVIDER + " ".repeat(width + leftPadding + rightPadding) + BoxRendererCLI.VERTICAL_DIVIDER;

    ArrayList<String> result = new ArrayList<>();

    if(renderTop) {
      result.add(new String(horizontalBorder));
    }
    for(int i = 0; i < topPadding; i++) {
      result.add(new String(emptyRow));
    }

    for(String childRenderRow : childRender) {
      result.add(BoxRendererCLI.VERTICAL_DIVIDER + " ".repeat(leftPadding) + StringPadder.padEndToLen(childRenderRow, width) + " ".repeat(rightPadding) + BoxRendererCLI.VERTICAL_DIVIDER);
    }

    for(int i = 0; i < bottomPadding; i++) {
      result.add(new String(emptyRow));
    }

    result.add(new String(horizontalBorder));

    return result;
  }

  public void activateTop() {
    renderTop = true;
  }

  public void deactivateTop() {
    renderTop = false;
  }

  public boolean isTopActivated() {
    return renderTop;
  }

  public void setMinWidth(int newMinWidth) {
    minWidth = newMinWidth;
  }

  public int getMinWidth() {
    return minWidth;
  }

  public void setRenderableChild(IRendererCLI newRenderableChild) {
    renderableChild = newRenderableChild;
  }
}

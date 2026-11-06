package src.main.java.view;

import java.util.ArrayList;

public class ContentRendererCLI implements IRendererCLI {
  private ArrayList<String> content;
  public ContentRendererCLI(ArrayList<String> theContent) {
    content = theContent;
  }

  @Override
  public ArrayList<String> render() {
    return content;
  }

  public void setContent(ArrayList<String> newContent) {
    content = newContent;
  }

  public ArrayList<String> getContent() {
    return new ArrayList<String>(content);
  }

  public int getWidth() {
    return content
      .stream()
      .map((String s) -> {return s.length();})
      .reduce(0, Math::max);
  }
}

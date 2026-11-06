package src.main.java.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ChoiceRendererCLI implements IRendererCLI, IInputCLI {
  private ArrayList<String> choices;
  private OrderedListRendererCLI orderedListRenderer;
  private String title;

  public ChoiceRendererCLI() {
    choices = new ArrayList<String>();
    orderedListRenderer = new OrderedListRendererCLI(choices);
    title = "";
  };

  @Override
  public String takeInput(Scanner sc) {
    int choiceInd = 0;
    try {
      choiceInd = Integer.parseInt(sc.nextLine());
    } catch(NumberFormatException e) {
      throw new UncheckedIOException(
        new IOException(
          "Please input an integer"
        )
      );
    }

    choiceInd--;
    if(choiceInd < 0 || choiceInd >= choices.size()) {
      throw new UncheckedIOException(
        new IOException(
          String.format("Please input a choice from 1 to %d inclusive", choices.size())
        )
      );
    }

    return String.valueOf(choiceInd);
  }

  @Override
  public ArrayList<String> render() {
    ArrayList<String> renderResult = new ArrayList<>();

    if(!title.equals("")) {
      renderResult.add("[[" + title + "]]");
    }

    renderResult.addAll(orderedListRenderer.render());
    
    return renderResult;
  }
  
  public void setChoices(ArrayList<String> newChoices) {
    choices = newChoices;
    orderedListRenderer.setItems(newChoices);
  }

  public void setTitle(String newTitle) {
    title = newTitle;
  }
}

package src.main.java.view;

import java.util.ArrayList;
import java.util.Scanner;

public class ChoiceScreenCLI implements IRendererCLI, IInputCLI {
  private ChoiceRendererCLI choicesRenderer;
  
  public ChoiceScreenCLI() {
    choicesRenderer = new ChoiceRendererCLI();
  }

  @Override
  public String takeInput(Scanner sc) {
    System.out.println();
    choicesRenderer.display();
    System.out.println();
    System.out.print(">> Input an action: ");
    return choicesRenderer.takeInput(sc);
  }

  @Override
  public ArrayList<String> render() {
    return choicesRenderer.render();
  }

  public void setChoices(ArrayList<String> newChoices) {
    choicesRenderer.setChoices(newChoices);
  }

  public void setTitle(String newTitle) {
    choicesRenderer.setTitle(newTitle);
  }
}

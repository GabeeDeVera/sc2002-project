package src.main.java.view;

import java.util.ArrayList;

import src.main.java.utils.StringPadder;

public class TableViewCLI implements IRendererCLI {
  private static final int MAXROWS = 1000;
  private static final String HORIZONTAL_DIVIDER = "-"; 
  private static final String VERTICAL_DIVIDER = "|"; 
  private static final String TABLE_JUNCTION = "+";
  private static final int HORIZONTAL_PADDING = 1;

  private boolean showIndex;
  private ArrayList<ArrayList<String>> data;
  private ArrayList<String> headers;

  public TableViewCLI(boolean theShowIndex, ArrayList<ArrayList<String>> theData, ArrayList<String> theHeaders) {
    showIndex = theShowIndex;
    data = theData;
    headers = theHeaders;
  }

  private String generateTableHorizontalDivider(ArrayList<Integer> lengths) {
    ArrayList<String> dividerSections = new ArrayList<>();

    dividerSections.add(TableViewCLI.TABLE_JUNCTION);

    for(int length : lengths) {
      dividerSections.add(TableViewCLI.HORIZONTAL_DIVIDER.repeat(length + (TableViewCLI.HORIZONTAL_PADDING << 1)));
      dividerSections.add(TableViewCLI.TABLE_JUNCTION);
    }

    return String.join("", dividerSections);
  }

  @Override
  public ArrayList<String> render() {
    int numRows = data.stream()
    .map((ArrayList<String> dataColumn) -> {return dataColumn.size();})
    .reduce(MAXROWS, Math::min);
    
    if(showIndex) {
      int numLen = String.valueOf(numRows).length();
      ArrayList<String> indexColumnData = new ArrayList<>();
      
      for(int i = 1; i <= numRows; i++) {
        indexColumnData.add(StringPadder.padIntToLen(i, numLen));
      }
      
      // Adding index column to table
      data.add(0, indexColumnData);
      headers.add(0, "i");
    }
    
    // Getting column lengths
    int numColumns = headers.size();
    ArrayList<Integer> columnLengths = new ArrayList<>();

    for(int colInd = 0; colInd < numColumns; colInd++) {
      int curColumnLength = headers.get(colInd).length();

      for(int rowInd = 0; rowInd < numRows; rowInd++) {
        curColumnLength = Math.max(curColumnLength, data.get(colInd).get(rowInd).length());
      }

      columnLengths.add(curColumnLength);
    }

    // Setting up print results
    ArrayList<String> renderResult = new ArrayList<>();

    // Printing the header
    renderResult.add(generateTableHorizontalDivider(columnLengths));

    StringBuilder headerBuilder = new StringBuilder(); // I use StringBuilder to speed up string building

    headerBuilder.append(TableViewCLI.VERTICAL_DIVIDER);
    for(int colInd = 0; colInd < numColumns; colInd++) {
      headerBuilder.append(" ".repeat(TableViewCLI.HORIZONTAL_PADDING));
      headerBuilder.append(StringPadder.padEndToLen(headers.get(colInd), columnLengths.get(colInd)));
      headerBuilder.append(" ".repeat(TableViewCLI.HORIZONTAL_PADDING) + TableViewCLI.VERTICAL_DIVIDER);
    }
    renderResult.add(headerBuilder.toString());
    renderResult.add(generateTableHorizontalDivider(columnLengths));
    
    // Printing the table
    for(int rowInd = 0; rowInd < numRows; rowInd++) {
      StringBuilder rowBuilder = new StringBuilder();
      rowBuilder.append(TableViewCLI.VERTICAL_DIVIDER);
      for(int colInd = 0; colInd < numColumns; colInd++) {
        rowBuilder.append(" ".repeat(TableViewCLI.HORIZONTAL_PADDING));
        rowBuilder.append(StringPadder.padEndToLen(data.get(colInd).get(rowInd), columnLengths.get(colInd)));
        rowBuilder.append(" ".repeat(TableViewCLI.HORIZONTAL_PADDING) + TableViewCLI.VERTICAL_DIVIDER);
      }
      renderResult.add(rowBuilder.toString());
    }
    renderResult.add(generateTableHorizontalDivider(columnLengths));

    // Pop the first column if we decided to print indices
    if(showIndex) {
      data.remove(0);
      headers.remove(0);
    }

    return renderResult;
  }

  public void activateIndexColumn() {
    showIndex = true;
  }
  public void deactivateIndexColumn() {
    showIndex = false;
  }

  public void setData(ArrayList<ArrayList<String>> newData) {
    data = newData;
  }

  public void setHeaders(ArrayList<String> newHeaders) {
    headers = newHeaders;
  }
}

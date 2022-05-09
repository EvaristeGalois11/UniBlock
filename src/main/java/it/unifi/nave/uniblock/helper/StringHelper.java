package it.unifi.nave.uniblock.helper;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.stream.Collectors;

public class StringHelper {
  private static final int LINE_LENGTH = 100;
  private static final String PADDING = "-";
  private static final int MARGIN = 5;
  private static final int FIELD_LENGTH = 20;

  public static String emptyLine() {
    return PADDING.repeat(LINE_LENGTH);
  }

  public static String formatCenter(String string) {
    var normalized = (string.length() % 2 == 0) ? string : string + PADDING;
    var toFill = LINE_LENGTH - string.length();
    return PADDING.repeat(toFill / 2) + normalized + PADDING.repeat(toFill / 2);
  }

  public static String formatTitle(String title) {
    return emptyLine() + "\n" + formatCenter(title.toUpperCase()) + "\n" + emptyLine();
  }

  public static String formatLeft(Object object, String label) {
    return formatLeft(object.toString(), label);
  }

  public static String formatLeft(String string, String label) {
    var margin = PADDING.repeat(MARGIN);
    var fieldName =
        margin + Strings.padEnd(" " + label + " ", FIELD_LENGTH, PADDING.charAt(0)) + " = ";
    var leftMargin = PADDING.repeat(fieldName.length() - 1) + " ";
    var realLine = LINE_LENGTH - fieldName.length() - MARGIN - 1;
    var rightMargin =
        " "
            + ((string.length() % realLine != 0)
                ? PADDING.repeat(realLine - string.length() % realLine)
                : "")
            + margin;
    return Splitter.fixedLength(realLine)
        .splitToStream(string)
        .collect(Collectors.joining(" " + margin + "\n" + leftMargin, fieldName, rightMargin));
  }
}

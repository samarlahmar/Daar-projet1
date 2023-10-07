package regEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Matching {

  // The `makeLps` method is used to create the Longest Proper Prefix which is
  // also a Suffix (LPS) array for a given pattern. The LPS array is used in the
  // Knuth-Morris-Pratt (KMP) algorithm for pattern matching.
  private static int[] makeLps(String pattern) {
    int n = pattern.length();
    int[] lps = new int[n + 1];
    lps[0] = -1;

    String[] PrefixList = new String[n - 1];
    PrefixList[0] = pattern.substring(0, 1);
    for (int i = 1; i < n - 1; i++) {
      PrefixList[i] = PrefixList[i - 1] + pattern.substring(i, i + 1);
    }

    String dejaVu = "";
    int i = 0;
    String prefix = pattern.substring(i, i + 1);

    while (i < n) {
      String c = pattern.substring(i, i + 1);
      if (dejaVu.contains(c) && (Arrays.asList(PrefixList).contains(prefix))) {
        lps[i + 1] = prefix.length();
        try {
          prefix += pattern.substring(i + 1, i + 2);
          i++;
        } catch (Exception e) {
          i++;
        }

      } else {
        try {
          prefix = pattern.substring(i + 1, i + 2);
          lps[i + 1] = 0;
          dejaVu += pattern.charAt(i);
          i++;

        } catch (Exception e) {
          lps[i + 1] = 0;
          dejaVu += pattern.charAt(i);
          i++;
        }
      }
    }

    return optimise(lps, pattern);
  }

  /**
   * The function optimizes the Longest Proper Prefix and Suffix (LPS) array for
   * a given pattern.
   *
   * @param lps The "lps" parameter is an array of integers representing the
   *     Longest Proper Prefix
   * which is also a Suffix (LPS) for each index in the pattern string.
   * @param pattern The "pattern" parameter is a String representing the pattern
   * for which we want to calculate the Longest Proper Prefix which is also a
   * Suffix (LPS) array. The LPS array is used in string matching algorithms
   * like the Knuth-Morris-Pratt (KMP) algorithm to optimize
   * @return The method is returning an array of integers, specifically the
   *     modified lps array.
   */
  private static int[] optimise(int[] lps, String pattern) {
    for (int i = 0; i < lps.length; i++) {
      try {
        if (pattern.charAt(i) == pattern.charAt(lps[i])) {
          if (lps[lps[i]] == -1) {
            lps[i] = -1;
          } else {
            lps[i] = lps[lps[i]];
          }
        }

      }

      catch (Exception e) {
      }
    }
    return lps;
  }

  /**
   * The function `matchingPattern` takes a pattern and a text as input and
   * returns a list of starting positions in the text where the pattern is
   * found.
   *
   * @param pattern The pattern parameter is a string that represents the
   *     pattern we are searching for
   * in the text.
   * @param text The "text" parameter is the string in which we want to search
   *     for the pattern.
   * @return The method is returning a List of integers, which represents the
   *     positions in the text
   * where the pattern is found.
   */
  public static List<Integer> matchingPattern(String pattern, String text) {
    List<Integer> positions = new ArrayList<>();
    int[] carryOver = makeLps(pattern);
    int textLength = text.length();
    int patternLength = pattern.length();
    int j = 0;
    for (int i = 0; i < textLength; i++) {
      while (j >= 0 && text.charAt(i) != pattern.charAt(j)) {
        // Update j using the carryOver array, ensuring it doesn't go below zero
        j = carryOver[j];
      }

      j++; // Increment j if the characters match

      if (j == patternLength) {
        // Pattern found, add the starting index to the result
        positions.add(i - j + 1);
        // Update j using the carryOver array, ensuring it doesn't go below zero
        j = carryOver[j];
      }
    }

    return positions;
  }

  /**
   * The function `matchingLines` takes a pattern and a text as input, and
   * returns a list of lines from the text that match the pattern.
   *
   * @param pattern The "pattern" parameter is a string that represents the
   *     pattern you want to match
   * in the "text" parameter.
   * @param text The "text" parameter is a string that represents the text in
   *     which we want to search
   * for lines that match a given pattern.
   * @return The method is returning a List of Strings.
   */
  public static List<String> matchingLines(String pattern, String text) {
    List<String> lines = new LinkedList<>();
    List<Integer> positions = Matching.matchingPattern(pattern, text);
    Integer last = Integer.MIN_VALUE;
    String lastLine = "";
    for (Integer integer : positions) {
      if (integer >= last && integer < last + lastLine.length())
        continue;

      int start = integer;
      while (start > 0 && text.charAt(start) != '\n')
        start--;
      int end = integer;
      while (end < text.length() && text.charAt(end) != '\n')
        end++;
      lastLine = text.substring(start, end);
      lines.add(lastLine);
    }
    return lines;
  }
}
package regEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matching {

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

  public static List<Integer> matchingRegex(Automate automate, String text) {
    int textLength = text.length();
    for (int i = 0; i < textLength; i++) {
    }
    return null;
  }
}
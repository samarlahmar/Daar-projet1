package regEx;

import java.io.File;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.Scanner;
import regEx.Helpers.Pair;
import regEx.Helpers.Tester;

public class RegEx {
  // MACROS
  static final int CONCAT = 0xC04CA7;
  static final int ETOILE = 0xE7011E;
  static final int PLUS = 0x2B;
  static final int ALTERN = 0xA17E54;

  static final int PROTECTION = 0xBADDAD;

  static final int PARENTHESEOUVRANT = 0x16641664;
  static final int PARENTHESEFERMANT = 0x51515151;
  static final int DOT = 0xD07;

  public static final int EPSILON = -12345678;

  // REGEX
  private static String regEx = null;

  private static boolean isFlag(String arg) { return arg.charAt(0) == '-'; }
  private static boolean isRegExp(String arg) {
    return !isFlag(arg) && arg.startsWith("\"") && arg.endsWith("\"");
  }

  public static void main(String[] args) throws Exception {
    final String regExp = args[0];
    final String filepath = args[1];
    System.out.println(new Tester(regExp, filepath).toString());
  }

  public static void _main(String[] args) throws Exception {
    boolean displayAutomate = false;
    boolean displayMatchingLinesNumber = false;
    boolean displayMatchingIndex = false;
    Automate result = null;
    String regExp = null;
    Scanner sc = null;
    for (int i = 0; i < args.length; i++) {
      String arg = args[i];
      if (isFlag(arg)) {
        switch (arg) {
        case "-l":
          displayMatchingLinesNumber = true;
          break;
        case "-dm":
          displayMatchingIndex = true;
        case "-d":
          displayAutomate = true;
          break;
        default:
          throw new IllegalArgumentException("Invalid Flag: n°" + arg);
        }
      }
      if (isRegExp(arg))
        regExp = arg.substring(1, arg.length() - 1);
      else if (!isFlag(arg))
        sc = new Scanner(new File(arg));
    }

    if (regExp == null)
      throw new Exception("No regex given");

    if (displayAutomate)
      result = Automate.buildFromRegexAndDisplayDot(regExp);
    else
      result = Automate.buildFromRegex(regExp);

    if (sc == null) {
      System.out.println("No file given");
      return;
    }

    int matchingLinesNumber = 0;
    while (sc.hasNextLine()) {
      String line = sc.nextLine();
      Pair<Integer, Integer> start_end = result.getFirstMatchWithIndex(line);
      if (start_end != null) {
        if (displayMatchingLinesNumber) {
          System.out.print("Line " + matchingLinesNumber);
          matchingLinesNumber++;
        }
        if (displayMatchingIndex)
          System.out.print(start_end + " :");
        System.out.println(line);
      }
    }
    sc.close();
  }

  // FROM REGEX TO SYNTAX TREE
  public static RegExTree parse(String input) throws Exception {
    if (input.length() < 1)
      throw new Exception();

    regEx = input;
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    for (int i = 0; i < regEx.length(); i++) {
      if (regEx.charAt(i) != '\"')
        result.add(new RegExTree(charToRoot(regEx.charAt(i)),
                                 new ArrayList<RegExTree>()));
    }

    return parse(result);
  }

  private static int charToRoot(char c) {
    if (c == '.')
      return DOT;
    if (c == '*')
      return ETOILE;
    if (c == '|')
      return ALTERN;
    if (c == '(')
      return PARENTHESEOUVRANT;
    if (c == ')')
      return PARENTHESEFERMANT;
    if (c == '+')
      return PLUS;
    return (int)c;
  }

  private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
    while (containParenthese(result))
      result = processParenthese(result);
    while (containEtoile(result))
      result = processEtoile(result);
    while (containPlus(result))
      result = processPlus(result);
    while (containConcat(result))
      result = processConcat(result);
    while (containAltern(result))
      result = processAltern(result);
    if (result.size() > 1)
      throw new Exception();

    return removeProtection(result.get(0));
  }

  private static boolean containPlus(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == PLUS && t.subTrees.isEmpty())
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processPlus(ArrayList<RegExTree> trees)
      throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t : trees) {
      if (!found && t.root == PLUS && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size() - 1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(PLUS, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static boolean containParenthese(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == PARENTHESEFERMANT || t.root == PARENTHESEOUVRANT)
        return true;
    return false;
  }

  private static ArrayList<RegExTree>
  processParenthese(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t : trees) {
      if (!found && t.root == PARENTHESEFERMANT) {
        boolean done = false;
        ArrayList<RegExTree> content = new ArrayList<RegExTree>();
        while (!done && !result.isEmpty())
          if (result.get(result.size() - 1).root == PARENTHESEOUVRANT) {
            done = true;
            result.remove(result.size() - 1);
          } else
            content.add(0, result.remove(result.size() - 1));
        if (!done)
          throw new Exception();
        found = true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(parse(content));
        result.add(new RegExTree(PROTECTION, subTrees));
      } else {
        result.add(t);
      }
    }
    if (!found)
      throw new Exception();
    return result;
  }

  private static boolean containEtoile(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == ETOILE && t.subTrees.isEmpty())
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees)
      throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t : trees) {
      if (!found && t.root == ETOILE && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size() - 1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(ETOILE, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static boolean containConcat(ArrayList<RegExTree> trees) {
    boolean firstFound = false;
    for (RegExTree t : trees) {
      if (!firstFound && t.root != ALTERN) {
        firstFound = true;
        continue;
      }
      if (firstFound)
        if (t.root != ALTERN)
          return true;
        else
          firstFound = false;
    }
    return false;
  }

  private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees)
      throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    boolean firstFound = false;
    for (RegExTree t : trees) {
      if (!found && !firstFound && t.root != ALTERN) {
        firstFound = true;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root == ALTERN) {
        firstFound = false;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root != ALTERN) {
        found = true;
        RegExTree last = result.remove(result.size() - 1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        subTrees.add(t);
        result.add(new RegExTree(CONCAT, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static boolean containAltern(ArrayList<RegExTree> trees) {
    for (RegExTree t : trees)
      if (t.root == ALTERN && t.subTrees.isEmpty())
        return true;
    return false;
  }

  private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees)
      throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    RegExTree gauche = null;
    boolean done = false;
    for (RegExTree t : trees) {
      if (!found && t.root == ALTERN && t.subTrees.isEmpty()) {
        if (result.isEmpty())
          throw new Exception();
        found = true;
        gauche = result.remove(result.size() - 1);
        continue;
      }
      if (found && !done) {
        if (gauche == null)
          throw new Exception();
        done = true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(gauche);
        subTrees.add(t);
        result.add(new RegExTree(ALTERN, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }

  private static RegExTree removeProtection(RegExTree tree) throws Exception {
    if (tree.root == PROTECTION && tree.subTrees.size() != 1)
      throw new Exception();
    if (tree.subTrees.isEmpty())
      return tree;
    if (tree.root == PROTECTION)
      return removeProtection(tree.subTrees.get(0));

    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    for (RegExTree t : tree.subTrees)
      subTrees.add(removeProtection(t));
    return new RegExTree(tree.root, subTrees);
  }
}

// UTILITARY CLASS

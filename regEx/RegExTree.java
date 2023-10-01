package regEx;

import java.util.ArrayList;

public class RegExTree {
  protected int root;
  protected ArrayList<RegExTree> subTrees;

  public RegExTree(int root, ArrayList<RegExTree> subTrees) {
    this.root = root;
    this.subTrees = subTrees;
  }

  // FROM TREE TO PARENTHESIS
  public String toString() {
    if (subTrees.isEmpty())
      return rootToString();
    String result = rootToString() + "(" + subTrees.get(0).toString();
    for (int i = 1; i < subTrees.size(); i++)
      result += "," + subTrees.get(i).toString();
    return result + ")";
  }

  private String rootToString() {
    switch (root) {
    case RegEx.CONCAT:
      return ".";
    case RegEx.ETOILE:
      return "*";
    case RegEx.ALTERN:
      return "|";
    case RegEx.DOT:
      return ".";
    case RegEx.PLUS:
      return "+";
    case RegEx.EPSILON:
      return "ε";
    default:
      return Character.toString((char)root);
    }
  }

  public boolean isRootOperator() {
    return root == RegEx.CONCAT || root == RegEx.ETOILE ||
        root == RegEx.ALTERN || root == RegEx.DOT || root == RegEx.PLUS;
  }

  public int getRoot() { return root; }
}

package regEx.Helpers;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import regEx.Automate;
import regEx.Matching;
import regEx.NdfaToDfa;
import regEx.RegEx;
import regEx.RegExTree;
import regEx.TreeToNdfa;

public class Tester {
  int parsing;
  int ndfa;
  int dfa;
  int minimization;
  int matchFileTime;
  int egrepTime;
  int kmpTime = -1;
  String regExp;
  String book;

  private class Timer {
    int start = 0;

    public Timer() { start = (int)System.currentTimeMillis(); }
    public int getElapsedTime() {
      return (int)System.currentTimeMillis() - start;
    }

    public int getElapsedTimeAndReset() {
      int elapsed = getElapsedTime();
      reset();
      return elapsed;
    }
    public void reset() { start = (int)System.currentTimeMillis(); }
  }

  public Tester(String regExp, String filepath) throws Exception {

    this.regExp = regExp;
    this.book = filepath.substring(filepath.lastIndexOf('/') + 1);

    Timer timer = new Timer();
    RegExTree tree = RegEx.parse(regExp);
    parsing = timer.getElapsedTimeAndReset();
    Automate automaton = TreeToNdfa.makeNDFA(tree);
    ndfa = timer.getElapsedTimeAndReset();
    NdfaToDfa.DFA_To_NDFA(automaton);
    dfa = timer.getElapsedTimeAndReset();
    NdfaToDfa.minimize(automaton);
    minimization = timer.getElapsedTimeAndReset();

    Scanner scanner = new Scanner(new File(filepath));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (automaton.match(line))
        System.out.println(line);
    }
    scanner.close();
    matchFileTime = timer.getElapsedTime();

    final String cmd = "egrep \"" + regExp + "\" " + filepath;
    ProcessBuilder builder = new ProcessBuilder();
    builder.inheritIO();
    builder.command("sh", "-c", cmd);
    timer.reset();
    builder.start().waitFor();
    egrepTime = timer.getElapsedTimeAndReset();

    timer.reset();
    String clearRegExp = RegExTree.getConcatenation(tree);
    if (clearRegExp == null)
      return;
    String text = new String(Files.readAllBytes(Paths.get(filepath)),
                             StandardCharsets.UTF_8);
    List<Integer> positions = Matching.matchingPattern(clearRegExp, text);
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
      System.out.print(lastLine);
    }
    scanner.close();
    System.out.println();
    kmpTime = timer.getElapsedTime();
  }

  public String toString() {
    return regExp + ";" + book + ";" + parsing + ";" + ndfa + ";" + dfa + ";" +
        minimization + ";" + matchFileTime + ";" + egrepTime + ";" + kmpTime;
  }
}

package regEx.Helpers;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import regEx.Automate;
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
  String regExp;
  String book;

  static final String progOutPath = "Tests/PROG_output.txt";
  static final String egrepOutPath = "Tests/EGREP_output.txt";

  public Tester(String regExp, String filepath) throws Exception {
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
    FileWriter writer = new FileWriter(progOutPath, false);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if (automaton.match(line)) {
        writer.write(line);
        writer.write("\n");
      }
    }
    writer.close();
    matchFileTime = timer.getElapsedTime();

    final String cmd =
        "rm " + egrepOutPath + " ; touch " + egrepOutPath + " ; egrep "
        + "\"" + regExp + "\""
        + " " + filepath + " > " + egrepOutPath + " 2> /dev/null";
    ProcessBuilder builder = new ProcessBuilder();
    builder.inheritIO();
    timer.reset();
    builder.command("sh", "-c", cmd);
    builder.start().waitFor();
    egrepTime = timer.getElapsedTime();

    this.regExp = regExp;
    this.book = filepath.substring(filepath.lastIndexOf('/') + 1);
  }

  public String toString() {
    return regExp + ";" + book + ";" + parsing + ";" + ndfa + ";" + dfa + ";" +
        minimization + ";" + matchFileTime + ";" + egrepTime;
  }
}

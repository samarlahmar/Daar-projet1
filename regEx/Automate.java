package regEx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import regEx.Helpers.Pair;

public class Automate {
  public Integer startingStateId, tmpNDFAFinalId;
  final public Map<Integer, State> states;
  final private AtomicInteger stateIdGen;

  public Automate(State starting_state, State ndfaEnd,
                  final AtomicInteger stateIdGen) {
    this.stateIdGen = stateIdGen;
    this.states = new HashMap<Integer, State>();
    setStartingState(starting_state);
    tmpNDFAFinalId = addState(ndfaEnd);
  }

  /**
   * The function takes a regular expression tree as input, converts it into a
   * non-deterministic finite automaton (NDFA), converts the NDFA into a
   * deterministic finite automaton (DFA), and returns the resulting DFA.
   *
   * @param reg The parameter "reg" is of type RegExTree, which represents a
   *     regular expression tree.
   * @return The method is returning an Automate object.
   */
  public static Automate buildFromRegex(RegExTree reg) throws Exception {
    Automate ndfa = TreeToNdfa.makeNDFA(reg);
    NdfaToDfa.convert(ndfa);
    return ndfa;
  }

  /**
   * The function takes a regular expression tree, builds an automaton from it,
   * and displays the automaton in DOT format using Graphviz.
   *
   * @param reg The parameter "reg" is of type RegExTree, which represents a
   *     regular expression tree.
   * @return The method is returning an Automate object.
   */
  public static Automate buildFromRegexAndDisplayDot(RegExTree reg)
      throws Exception {

    Automate automaton = TreeToNdfa.makeNDFA(reg);
    System.out.println("Graphviz file generated in Visual/*.dot");
    automaton.writeToDotFile("ndfa");
    NdfaToDfa.NDFA_To_DFA(automaton);
    automaton.writeToDotFile("dfa");
    NdfaToDfa.minimize(automaton);
    automaton.writeToDotFile("dfa-minimized");
    return automaton;
  }

  /**
   * The function `getFirstMatchWithIndex` searches for the first occurrence of
   * a match in a given string and returns the start and end indices of the
   * match.
   *
   * @param toTest A string that needs to be tested for a match.
   * @param start The `start` parameter is the index in the `toTest` string from
   *     where the search for a
   * match should begin.
   * @return The method is returning a Pair<Integer, Integer> object.
   */
  private Pair<Integer, Integer> getFirstMatchWithIndex(final String toTest,
                                                        final int start) {
    State current = getStartingState();
    for (int i = start; i < toTest.length(); i++) {
      if (current.isAccepting)
        return new Pair<Integer, Integer>(start, i);

      final Integer symbol = (int)toTest.charAt(i);
      current = current.dfaTransition.get(symbol);
      if (current == null)
        return getFirstMatchWithIndex(toTest, start + 1);
    }
    if (current.isAccepting)
      return new Pair<Integer, Integer>(start, toTest.length());
    return null;
  }

  public Pair<Integer, Integer> getFirstMatchWithIndex(final String toTest) {
    return getFirstMatchWithIndex(toTest, 0);
  }

  /**
   * The function checks if a given string matches a specific pattern using a
   * deterministic finite automaton (DFA).
   *
   * @param toTest The parameter "toTest" is a String that represents the input
   *     string that we want to
   * test for a match.
   * @param start The parameter "start" represents the starting index in the
   *     string "toTest" from where
   * the matching process should begin.
   * @return The method is returning a boolean value.
   */
  private boolean match(final String toTest, final int start) {
    State current = getStartingState();
    for (int i = start; i < toTest.length(); i++) {
      if (current.isAccepting)
        return true;

      final Integer symbol = (int)toTest.charAt(i);
      current = current.dfaTransition.get(symbol);
      if (current == null)
        return match(toTest, start + 1);
    }
    return current.isAccepting;
  }

  public boolean match(final String toTest) { return match(toTest, 0); }

  /**
   * The addState function adds a new state object to a map and returns its
   * assigned ID.
   *
   * @param newS The parameter "newS" is of type "State". It represents the new
   *     state object that needs
   * to be added to the collection of states.
   * @return The method is returning an integer value, which is the ID of the
   *     newly added state.
   */
  public int addState(State newS) {
    final int id = stateIdGen.getAndIncrement();
    states.put(id, newS);
    return id;
  }

  public State getState(int id) { return this.states.get(id); }
  public void deleteState(int id) { this.states.remove(id); }

  /**
   * The mergeAutomaton function merges the states of another Automate object
   * into the current object.
   *
   * @param other The "other" parameter is an instance of the Automate class.
   */
  public void mergeAutomaton(Automate other) {
    this.states.putAll(other.states);
  }

  public State getStartingState() { return this.states.get(startingStateId); }
  public State getTmpNDFAFinalState() {
    return this.states.get(tmpNDFAFinalId);
  }

  /**
   * The function sets the starting state of an object by assigning it a state
   * ID.
   *
   * @param a The parameter "a" is of type State.
   */
  public void setStartingState(State a) { this.startingStateId = addState(a); }

  /**
   * The `toDotString()` function generates a DOT representation of a finite
   * state machine.
   *
   * @return The method is returning a string representation of a finite state
   *     machine in the DOT
   * language format.
   */
  public String toDotString() {
    final String dotFilePrefix = "digraph finite_state_machine {\n"
                                 + "graph [ dpi = 400 ];\n"
                                 + "rankdir=LR;\n"
                                 + "size=\"8,5\"\n"
                                 + "node [shape = circle];\n";
    final StringBuilder sb = new StringBuilder(dotFilePrefix);

    for (Entry<Integer, State> entry : states.entrySet()) {
      int id = entry.getKey();
      entry.getValue().toDotString(sb, id);
      if (id == startingStateId) {
        sb.append(id);
        sb.append(" [color=\"blue\"]\n");
      }
    }
    sb.append('}');
    return sb.toString();
  }

  /**
   * The function writes a graph in DOT format to a file, converts it to a JPG
   * image using the Graphviz "dot" command, and saves it with the specified
   * filename.
   *
   * @param filename The filename parameter is the name of the file that will be
   *     created.
   */
  public void writeToDotFile(String filename)
      throws IOException, InterruptedException {
    final String path = "Visual/" + filename;
    final String dotExt = path + ".dot";
    final String jpgExt = path + ".jpg";

    final String cmd = String.format("dot -Tjpeg %s > %s ", dotExt, jpgExt);

    FileWriter fw = new FileWriter(dotExt, false);
    fw.write(toDotString());
    fw.close();

    ProcessBuilder builder = new ProcessBuilder();
    builder.inheritIO();
    builder.command("sh", "-c", cmd);

    builder.start().waitFor();
  }
}

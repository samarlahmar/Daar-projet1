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

  public static Automate buildFromRegex(String regxp) throws Exception {
    RegExTree reg = RegEx.parse(regxp);
    Automate ndfa = TreeToNdfa.makeNDFA(reg);
    NdfaToDfa.convert(ndfa);
    return ndfa;
  }

  public static Automate buildFromRegexAndDisplayDot(String regxp)
      throws Exception {
    RegExTree reg = RegEx.parse(regxp);
    Automate automaton = TreeToNdfa.makeNDFA(reg);
    System.out.println("Graphviz file generated in Visual/*.dot");
    automaton.writeToDotFile("ndfa");
    NdfaToDfa.DFA_To_NDFA(automaton);
    automaton.writeToDotFile("dfa");
    NdfaToDfa.minimize(automaton);
    automaton.writeToDotFile("dfa-minimized");
    return automaton;
  }

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

  

  public int addState(State newS) {
    final int id = stateIdGen.getAndIncrement();
    states.put(id, newS);
    return id;
  }

  public State getState(int id) { return this.states.get(id); }
  public void deleteState(int id) { this.states.remove(id); }

  public void mergeAutomaton(Automate other) {
    this.states.putAll(other.states);
  }

  public State getStartingState() { return this.states.get(startingStateId); }
  public State getTmpNDFAFinalState() {
    return this.states.get(tmpNDFAFinalId);
  }

  public void setStartingState(State a) { this.startingStateId = addState(a); }

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

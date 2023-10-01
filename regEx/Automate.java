package regEx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

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
    final boolean isWindows =
        System.getProperty("os.name").toLowerCase().startsWith("windows");

    FileWriter fw = new FileWriter(dotExt, false);
    fw.write(toDotString());
    fw.close();

    ProcessBuilder builder = new ProcessBuilder();
    builder.inheritIO();
    if (isWindows)
      builder.command("cmd.exe", "/c", cmd);
    else
      builder.command("sh", "-c", cmd);

    builder.start().waitFor();
  }
}

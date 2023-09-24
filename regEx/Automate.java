package regEx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Automate {

  private Integer _starting_state, _final_state;
  private Map<Integer, State> _states;

  public Automate(State starting_state, State final_state) {
    this._states = new HashMap<Integer, State>();
    this._starting_state = starting_state.getStateID();
    this._final_state = final_state.getStateID();
    putState(final_state);
    putState(starting_state);
  }

  public Automate(Integer starting_state, Collection<State> tates) {
    this._states = new HashMap<Integer, State>();
    this._starting_state = starting_state;
    for (State state : tates)
      putState(state);
  }

  public Map<Integer, State> getStates() { return this._states; }

  public void putState(State newState) {
    this._states.put(newState.getStateID(), newState);
  }

  public void putAllStates(Map<Integer, State> s) {
    for (State state : s.values())
      putState(state);
  }

  public State getState(Integer id) { return this._states.get(id); }

  public void setStartingState(State newstarting_state) {
    this._starting_state = newstarting_state.getStateID();
    putState(newstarting_state);
  }

  public void setFinalState(State newfinal_state) {
    this._final_state = newfinal_state.getStateID();
    putState(newfinal_state);
  }

  public Integer getStartingStateID() { return this._starting_state; }
  public State getStartingState() { return getState(getStartingStateID()); }
  public Integer getFinalStateID() { return this._final_state; }
  public State getFinalState() { return getState(getFinalStateID()); }
  public int getNumberOfStates() { return this._states.size(); }

  public String toDotString() {
    String s = "digraph finite_state_machine {\n"
               + "graph [ dpi = 400 ];\n"
               + "rankdir=LR;\n"
               + "size=\"8,5\"\n"
               + "node [shape = circle];\n";

    for (State state : getStates().values())
      s += state.toDotString(state.getStateID() == getStartingStateID());
    s += "}";
    return s;
  }

  public void writeToDotFile(String filename)
      throws IOException, InterruptedException {
    String path = "Visual/" + filename;
    String dotExt = path + ".dot";
    String jpgExt = path + ".jpg";

    FileWriter fw = new FileWriter(dotExt, false);
    fw.write(toDotString());
    fw.close();
    boolean isWindows =
        System.getProperty("os.name").toLowerCase().startsWith("windows");

    ProcessBuilder builder = new ProcessBuilder();
    builder.inheritIO();
    String cmd = String.format("dot -Tjpeg %s > %s ", dotExt, jpgExt);
    if (isWindows) {
      builder.command("cmd.exe", "/c", cmd);
    } else {
      builder.command("sh", "-c", cmd);
    }
    Process process = builder.start();
    process.waitFor();
  }
}

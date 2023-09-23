package regEx;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Automate {

  private State starting_state;
  private ArrayList<State> states;
  private State final_state;

  public Automate(State starting_state, State final_state) {
    this.starting_state = starting_state;
    this.final_state = final_state;
    this.states = new ArrayList<State>();
  }

  public ArrayList<State> getStates() { return this.states; }

  public void setStates(ArrayList<State> states) { this.states = states; }

  public void setStartingState(State starting_state) {
    this.starting_state = starting_state;
  }

  public State getStartingState() { return this.starting_state; }

  public void setFinalState(State final_state) {
    this.final_state = final_state;
  }

  public State getFinalState() { return this.final_state; }

  public void addState(State s) { this.states.add(s); }

  public void addAllStates(ArrayList<State> states) {
    for (State s : states) {
      this.states.add(s);
    }
  }

  public int getNumberOfStates() { return states.size() + 2; }

  public String toDotString() {
    String s = "digraph finite_state_machine {\n"
               + "graph [ dpi = 400 ];\n"
               + "rankdir=LR;\n"
               + "size=\"8,5\"\n"
               + "node [shape = doublecircle]; " + final_state.getStateID() +
               ";\n"
               + "node [shape = circle];\n";

    s += starting_state.toDotString(true);
    for (State state : states)
      s += state.toDotString(false);
    s += final_state.toDotString(false);
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

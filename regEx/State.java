package regEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class State {

  private ArrayList<Transition> transitionsList;
  private boolean isAccepting = false;
  private int stateID;

  public static int stateCounter = 0;
  public static void resetCounter() { stateCounter = 0; }

  private Set<Integer> merged = null;

  public State(boolean isAccepting) {
    this.transitionsList = new ArrayList<Transition>();
    this.isAccepting = isAccepting;
    this.stateID = stateCounter++;
  }

  public State(boolean isAccepting, ArrayList<Transition> t) {
    this.transitionsList = t;
    this.isAccepting = isAccepting;
    this.stateID = stateCounter++;
  }

  public State(Collection<State> ensemble) {
    this.stateID = stateCounter++;
    this.transitionsList = new ArrayList<Transition>();
    for (State s : ensemble) {
      if (getAccepting() == false)
        setAccepting(s.getAccepting());
      this.transitionsList.addAll(s.getTransitionsList());
    }
    merged = new java.util.HashSet<Integer>();
    for (State s : ensemble)
      merged.add(s.getStateID());
  }

  public int getStateID() { return stateID; }

  public boolean getAccepting() { return isAccepting; }

  public void setAccepting(boolean isAccepting) {
    this.isAccepting = isAccepting;
  }

  public ArrayList<Transition> getTransitionsList() { return transitionsList; }
  public void resetTransitionsList() {
    transitionsList.clear();
    ;
  }
  public void deleteTransitionWithKey(int toDel) {
    for (int i = 0; i < this.transitionsList.size(); i++)
      if (transitionsList.get(i).getAccepted_Code() == toDel)
        transitionsList.remove(i--);
  }

  public void addTransition(Transition transition) {
    this.transitionsList.add(transition);
  }

  public String toDotString(boolean isStarting) {
    String color = "black";
    if (isAccepting)
      color = "green";
    else if (isStarting)
      color = "blue";

    String label = String.format("%d", stateID);
    if (merged != null)
      label = merged.toString();
    String result =
        String.format("%d [label=\"%s\" color=\"%s\"]", stateID, label, color);

    if (isAccepting)
      result += ";\n" + stateID + " [shape = doublecircle];\n";

    for (Transition t : getTransitionsList())
      result += t.toDotString(getStateID());
    return result;
  }
}

package regEx;

import java.util.ArrayList;
import java.util.Collection;

public class State {

  private ArrayList<Transition> transitionsList;
  private boolean isAccepting = false;
  private int stateID;

  public static int stateCounter = 0;
  public static void resetCounter() { stateCounter = 0; }

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
    transitionsList.removeIf(t -> t.getAccepted_Code() == toDel);
  }

  public void addTransition(Transition transition) {
    this.transitionsList.add(transition);
  }

  public void addAllTransitions(Collection<Transition> transitions) {
    this.transitionsList.addAll(transitions);
  }

  public String toDotString(boolean isStarting) {
    String color = "black";
    if (isAccepting)
      color = "green";
    else if (isStarting)
      color = "blue";

    String result = String.format("%d [label=\"%s\" color=\"%s\"]", stateID,
                                  stateID, color);

    if (isAccepting)
      result += ";\n" + stateID + " [shape = doublecircle];\n";

    for (Transition t : getTransitionsList())
      result += t.toDotString(getStateID());
    return result;
  }

  public static boolean isEquiv(State a, State b) {
    if (a.getAccepting() != b.getAccepting() ||
        a.getTransitionsList().size() != b.getTransitionsList().size())
      return false;

    return a.transitionsList.containsAll(b.transitionsList);
  }
}

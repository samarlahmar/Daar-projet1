package regEx;

import java.util.ArrayList;

public class State implements Comparable<State> {

  private ArrayList<Transition> transitionsList;
  private boolean isAccepting;
  private int stateID;

  public static int stateCounter = 0;
  public static void resetCounter() { stateCounter = 0; }

  public State(boolean isAccepting) {
    this.transitionsList = new ArrayList<Transition>();
    this.isAccepting = isAccepting;
    this.stateID = stateCounter++;
  }

  public int getStateID() { return stateID; }

  public boolean getAccepting() { return isAccepting; }

  public void setAccepting(boolean isAccepting) {
    this.isAccepting = isAccepting;
  }

  public ArrayList<Transition> getTransitionsList() { return transitionsList; }

  public void addTransition(Transition transition) {
    this.transitionsList.add(transition);
  }

  public String toDotString(boolean isStarting) {
    String color = "black";
    if (isAccepting)
      color = "green";
    else if (isStarting)
      color = "blue";

    String result = String.format("%d [label=\"%d\" color=\"%s\"]", stateID,
                                  stateID, color);
    for (Transition t : getTransitionsList())
      result += t.toDotString(getStateID());
    return result;
  }

  @Override
  public int compareTo(State o) {
    return Integer.compare(getStateID(), o.getStateID());
  }
}

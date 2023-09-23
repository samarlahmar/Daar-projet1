package regEx;

import java.util.ArrayList;
import java.util.Map;

public class State {

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

  public State(Map<Integer, State> ensemble) {
    ArrayList<Transition> mergeTransition = new ArrayList<>();
    for (State s : ensemble.values()) {
      if (getAccepting() == false)
        setAccepting(s.getAccepting());
      mergeTransition.addAll(s.getTransitionsList());
    }
  }

  public int getStateID() { return stateID; }

  public boolean getAccepting() { return isAccepting; }

  public void setAccepting(boolean isAccepting) {
    this.isAccepting = isAccepting;
  }

  public ArrayList<Transition> getTransitionsList() { return transitionsList; }
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

    String result = String.format("%d [label=\"%d\" color=\"%s\"]", stateID,
                                  stateID, color);
    for (Transition t : getTransitionsList())
      result += t.toDotString(getStateID());
    return result;
  }
}

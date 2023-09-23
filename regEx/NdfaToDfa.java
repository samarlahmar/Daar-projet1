package regEx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class NdfaToDfa {

  public static Automate makeDfa(Automate Ndfa) { return Ndfa; }

  public static Map<Integer, ArrayList<Integer>> traitement(State state) {
    ArrayList<Transition> transitions = state.getTransitionsList();
    Map<Integer, ArrayList<Integer>> hm = new HashMap<>();
    for (Transition t : transitions) {
      ArrayList<Integer> transitionT = new ArrayList<Integer>();
      transitionT.add(t.getDestination_id());
      ArrayList<Integer> before = hm.get(t.getAccepted_Code());
      if (before != null)
        transitionT.addAll(before);
      hm.put(t.getAccepted_Code(), transitionT);
    }
    return hm;
  }

/*   public static Automate determine(Automate a) {
    Stack<State> toProcess = new Stack<>();
    toProcess.push(a.getStartingState());
    while (!toProcess.isEmpty()) {
      State inWork = toProcess.pop();
      Map<Integer, ArrayList<Integer>> transitionMap = traitement(inWork);
      for (Entry<Integer, ArrayList<Integer>> key_val :
           transitionMap.entrySet()) {
      }
    }
  } */

  public static State makeNewState(Map<Integer, ArrayList<Integer>> m, State st,
                                   Automate A) {
    TreeToNdfa.stateCounter = st.getStateID() + 1;
    for (int t : m.keySet()) {
      for (int id : m.get(t)) {
        State startingState = A.getStartingState();
        State finalState = A.getFinalState();
        State s1;
        if ((startingState.getStateID() == id) &&
            (startingState.getAccepting())) {
          s1 = new State(true);
        } else if ((finalState.getStateID() == id) &&
                   (finalState.getAccepting())) {
          s1 = new State(true);
        }

        else {
          for (State s : A.getStates()) {
            if ((s.getStateID() == id) && (s.getAccepting() == true)) {
              s1 = new State(true);
            }
          }
          s1 = new State(false);
        }
        Transition t1 = new Transition(s1);
        st.addTransition(t1);
      }
    }

    return st;
  }
}
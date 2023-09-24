package regEx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NdfaToDfa {

  private static Map<Set<Integer>, State> old_to_new =
      new HashMap<Set<Integer>, State>();

  private static Automate ndfa = null;

  public static Automate convert(Automate ndfa) {
    old_to_new.clear();
    NdfaToDfa.ndfa = ndfa;

    State starting_state = ndfa.getStartingState();
    Set<Integer> starting_set = new HashSet<Integer>();
    starting_set.add(starting_state.getStateID());
    processState(starting_set, starting_state);

    return new Automate(starting_state.getStateID(), old_to_new.values());
  }

  private static void processState(Set<Integer> merged, State base) {

    HashMap<Integer, Set<Integer>> letterToStates =
        new HashMap<Integer, Set<Integer>>();

    for (Transition t : base.getTransitionsList()) {
      Set<Integer> tmp = letterToStates.get(t.getAccepted_Code());
      if (tmp == null)
        tmp = new HashSet<Integer>();

      tmp.add(t.getDestination_id());
      letterToStates.put(t.getAccepted_Code(), tmp);
    }

    base.resetTransitionsList();
    old_to_new.put(merged, base);

    for (Entry<Integer, Set<Integer>> newState : letterToStates.entrySet()) {
      if (old_to_new.containsKey(newState.getValue()))
        base.addTransition(new Transition(newState.getKey(),
                                          old_to_new.get(newState.getValue())));
      else {
        LinkedList<State> toMerge = new LinkedList<State>();
        for (Integer i : newState.getValue())
          toMerge.add(ndfa.getState(i));

        State created = new State(toMerge);
        processState(newState.getValue(), created);
        base.addTransition(new Transition(newState.getKey(), created));
      }
    }
    old_to_new.put(merged, base);
  }
}
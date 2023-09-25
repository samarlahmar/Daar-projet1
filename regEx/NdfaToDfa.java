package regEx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
    Set<Integer> reachable = allEpsilonReachable(base);
    for (Integer i : reachable) {
      State s = ndfa.getState(i);
      base.setAccepting(base.getAccepting() || s.getAccepting());
      for (Transition t : s.getTransitionsList()) {
        base.addTransition(t);
      }
    }
    base.deleteTransitionWithKey(RegEx.Epsilon);

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
        State created = new State(newState.getValue()
                                      .stream()
                                      .map(i -> ndfa.getState(i))
                                      .collect(Collectors.toList()));
        processState(newState.getValue(), created);
        base.addTransition(new Transition(newState.getKey(), created));
      }
    }
    old_to_new.put(merged, base);
  }

  private static Set<Integer> allEpsilonReachable(State b) {
    Set<Integer> reachable = new HashSet<Integer>();
    for (Transition t : b.getTransitionsList()) {
      if (t.getAccepted_Code() == RegEx.Epsilon) {
        reachable.add(t.getDestination_id());
        reachable.addAll(
            allEpsilonReachable(ndfa.getState(t.getDestination_id())));
      }
    }
    return reachable;
  }
}
package regEx;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NdfaToDfa {

  public static void convert(final Automate ndfa)
      throws IOException, InterruptedException {
    final Set<Integer> starting_set = new HashSet<Integer>();
    starting_set.add(ndfa.startingStateId);
    processState(starting_set, ndfa, new HashMap<Set<Integer>, Integer>(),
                 new HashMap<Integer, Set<Integer>>());
    ndfa.states.keySet().removeIf(i -> i < ndfa.startingStateId);
    minimize(ndfa);
  }

  private static void
  processState(final Set<Integer> merged, final Automate ndfa,
               final Map<Set<Integer>, Integer> old_to_new,
               final Map<Integer, Set<Integer>> epsilonReachable) {
    if (old_to_new.containsKey(merged))
      return;

    final Set<Integer> reachable = new HashSet<Integer>();
    merged.forEach(i -> {
      if (!epsilonReachable.containsKey(i))
        allEpsilonReachable(i, ndfa, epsilonReachable);
      if (!reachable.contains(i))
        reachable.addAll(epsilonReachable.get(i));
    });

    final State ensemble = new State();
    final Integer ensemble_id = ndfa.addState(ensemble);
    old_to_new.put(merged, ensemble_id);
    if (old_to_new.size() == 1)
      ndfa.startingStateId = ensemble_id;

    reachable.forEach(i -> { ensemble.absorbeState(ndfa.getState(i)); });
    ensemble.deleteTransitionWithKey(RegEx.EPSILON);

    for (Entry<Integer, Collection<Integer>> newState :
         ensemble._transitions.entrySet()) {
      final Set<Integer> new_ensemble =
          new HashSet<Integer>(newState.getValue());

      processState(new_ensemble, ndfa, old_to_new, epsilonReachable);
      ensemble.setTransition(newState.getKey(), old_to_new.get(new_ensemble));
    }
  }

  private static void
  allEpsilonReachable(final int id, final Automate ndfa,
                      Map<Integer, Set<Integer>> epsilonReachable) {
    if (epsilonReachable.containsKey(id))
      return;

    final Collection<Integer> transitions =
        ndfa.getState(id)._transitions.get(RegEx.EPSILON);

    final HashSet<Integer> tmp = new HashSet<Integer>();
    tmp.add(id);
    epsilonReachable.put(id, tmp);
    if (transitions == null)
      return;

    transitions.forEach(i -> {
      allEpsilonReachable(i, ndfa, epsilonReachable);
      epsilonReachable.get(id).addAll(epsilonReachable.get(i));
    });
  }

  private static void minimize(final Automate dfa) {
    final Map<Integer, Integer> minmized = new HashMap<Integer, Integer>();
    for (final Entry<Integer, State> toProcess : dfa.states.entrySet()) {
      Integer found = toProcess.getKey();
      for (Integer i : minmized.values())
        if (State.isEquiv(toProcess.getValue(), dfa.getState(i))) {
          found = i;
          break;
        }
      minmized.put(toProcess.getKey(), found);
    }

    for (final Entry<Integer, Integer> e : minmized.entrySet())
      if (e.getKey() != e.getValue())
        dfa.deleteState(e.getKey());
      else {
        final State editedState = dfa.getState(e.getKey());
        editedState.dfaTransition = new HashMap<Integer, State>();
        for (Entry<Integer, Collection<Integer>> entry :
             editedState._transitions.entrySet()) {

          Integer found = minmized.get(entry.getValue().iterator().next());

          editedState.dfaTransition.put(entry.getKey(), dfa.getState(found));
          editedState.setTransition(entry.getKey(), found);
        }
      }
  }
}

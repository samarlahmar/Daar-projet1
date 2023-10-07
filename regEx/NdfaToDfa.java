package regEx;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NdfaToDfa {

  /**
   * The function converts a non-deterministic finite automaton (NDFA) to a
   * deterministic finite automaton (DFA) and then minimizes the DFA.
   *
   * @param ndfa The parameter "ndfa" is an object of type "Automate" which
   *     represents a
   * non-deterministic finite automaton (NDFA).
   */
  public static void convert(final Automate ndfa)
      throws IOException, InterruptedException {
    NDFA_To_DFA(ndfa);
    minimize(ndfa);
  }

  /**
   * The function converts a non-deterministic finite automaton (NDFA) to a
   * deterministic finite automaton (DFA).
   *
   * @param ndfa The parameter "ndfa" is an object of type "Automate".
   */
  public static void NDFA_To_DFA(final Automate ndfa) {
    final Set<Integer> starting_set = new HashSet<Integer>();
    starting_set.add(ndfa.startingStateId);
    processState(starting_set, ndfa, new HashMap<Set<Integer>, Integer>(),
                 new HashMap<Integer, Set<Integer>>());
    ndfa.states.keySet().removeIf(i -> i < ndfa.startingStateId);
  }

  /**
   * The function processes a state in an automaton by merging states, finding
   * reachable states, creating a new ensemble state, and updating transitions.
   *
   * @param merged The parameter "merged" is a set of integers that represents a
   *     merged state in an
   * automaton.
   * @param ndfa The `ndfa` parameter is an instance of the `Automate` class,
   *     which represents a
   * non-deterministic finite automaton. It is used to store the states and
   * transitions of the automaton.
   * @param set_to_state The parameter "set_to_state" is a map that maps a set
   *     of integers to an integer. It is used to keep track of the mapping
   * between a set of states in the NFA and the corresponding state in the DFA.
   * @param epsilonReachable The epsilonReachable parameter is a map that stores
   *     the epsilon reachable  states for each state in the Automate ndfa. It
   * is of type Map<Integer, Set<Integer>> where the key is the state id and the
   * value is a set of state ids that can be reached from the key state using
   * epsilon
   */
  private static void
  processState(final Set<Integer> merged, final Automate ndfa,
               final Map<Set<Integer>, Integer> set_to_state,
               final Map<Integer, Set<Integer>> epsilonReachable) {
    if (set_to_state.containsKey(merged))
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
    set_to_state.put(merged, ensemble_id);
    if (set_to_state.size() == 1)
      ndfa.startingStateId = ensemble_id;

    reachable.forEach(i -> { ensemble.absorbeState(ndfa.getState(i)); });

    for (Entry<Integer, Collection<Integer>> newState :
         ensemble._transitions.entrySet()) {
      final Set<Integer> new_ensemble =
          new HashSet<Integer>(newState.getValue());

      processState(new_ensemble, ndfa, set_to_state, epsilonReachable);
      ensemble.setTransition(newState.getKey(), set_to_state.get(new_ensemble));
    }
  }

  /**
   * The function recursively finds all states that can be reached from a given
   * state through epsilon transitions in an NFA.
   *
   * @param id The parameter "id" represents the ID of a state in an automaton.
   * @param ndfa The parameter "ndfa" is an instance of the "Automate" class,
   *     which represents a non-deterministic finite automaton (NFA).
   * @param epsilonReachable A map that stores the epsilon reachable states for
   *     each state in the Automate (NDFA).
   */
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

  /**
   * The function "minimize" takes a DFA (Deterministic Finite Automaton) as
   * input and minimizes it by merging equivalent states.
   *
   * @param dfa The parameter `dfa` is an object of type `Automate`.
   */
  public static void minimize(final Automate dfa) {
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

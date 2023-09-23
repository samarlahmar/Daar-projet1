package regEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class NdfaToDfa {

  public static Automate makeDfa(Automate Ndfa) { return Ndfa; }

  public static Map<Integer, ArrayList<State>> traitement(State state , Automate a) {
    ArrayList<Transition> transitions = state.getTransitionsList();
    Map<Integer, ArrayList<State>> hm = new HashMap<>();
    for (Transition t : transitions) {
      ArrayList<State> transitionT = new ArrayList<>();
      transitionT.add(a.getState(t.getDestination_id()));
      ArrayList<State> before = hm.get(t.getAccepted_Code());
      if (before != null)
        transitionT.addAll(before);
      hm.put(t.getAccepted_Code(), transitionT);
    }
    return hm;
  }

  public static Automate determine(Automate a) {
    Map<State, Map<Integer, ArrayList<State>>> matrix = new HashMap<>();
    Map<State, Boolean> memory = new HashMap<>();
    Stack<State> stack = new Stack<>();
    stack.push(a.getStartingState());
    while (!stack.isEmpty()) {
      State toProcess = stack.pop();
      if (memory.containsKey(toProcess))
        continue;
      memory.put(toProcess, true);
      Map<Integer, ArrayList<State>> traitement = traitement(toProcess,a);
      matrix.put(toProcess, traitement);
      Map<Integer, State> ensemble = new HashMap<>();
      for (Integer i : traitement.keySet()) {
        for (State j : traitement.get(i)) {
          ensemble.put(j.getStateID(), j);
        }
      State newState = new State(ensemble);
      stack.push(newState);
      }
      ArrayList<State> states = new ArrayList<>();
       for (State s : matrix.keySet() ){
        states.add(s) ;
       }
       State startingState = states.get(0) ;
       State finalState =states.get(states.size()-1);
       Automate dfa = new Automate(startingState,finalState) ;
      for (State s : matrix.keySet() ){
        Map<Integer, State> ensemble1 = new HashMap<>() ;
         Map<Integer, ArrayList<State>> transition = matrix.get(s) ;
         if (s == startingState){
          for(Integer t : transition.keySet() ){
          for (State j : transition.get(t)) {
          ensemble1.put(j.getStateID(), j);
        }
          State s1 = new State(ensemble1);
          Transition t1 = new Transition(s1) ;
          dfa.getStartingState().addTransition(t1) ;
         }

      }


      
    }

  }

  
}
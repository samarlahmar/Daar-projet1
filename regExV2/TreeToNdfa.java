package regExV2;

import java.util.concurrent.atomic.AtomicInteger;

public class TreeToNdfa {
  private static AtomicInteger _stateCounter;
  public static Automate makeNDFA(RegExTree tree) {
    _stateCounter = new AtomicInteger(0);
    if (tree == null)
      return null;

    return makeNDFA_AUX(tree);
  }

  public static Automate makeNDFA_AUX(RegExTree tree) {
    if (!tree.isRootOperator())
      return CreateBasisCaseAutomate(tree.getRoot());
    switch (tree.getRoot()) {
    case RegEx.CONCAT:
      return CreateConcatAutomate(makeNDFA_AUX(tree.subTrees.get(0)),
                                  makeNDFA_AUX(tree.subTrees.get(1)));
    case RegEx.ETOILE:
      return CreateStarAutomate(makeNDFA_AUX(tree.subTrees.get(0)));
    case RegEx.ALTERN:
      return CreateOrAutomate(makeNDFA_AUX(tree.subTrees.get(0)),
                              makeNDFA_AUX(tree.subTrees.get(1)));
    case RegEx.PLUS:
      return CreateAddAutomate(makeNDFA_AUX(tree.subTrees.get(0)));
    default:
      return null;
    }
  }

  public static Automate CreateBasisCaseAutomate(int rootCode) {
    State s1 = new State();
    State s2 = new State(true);
    Automate output = new Automate(s1, s2, _stateCounter);
    output.getStartingState().addTransition(rootCode, output.tmpNDFAFinalId);
    return output;
  }

  public static Automate CreateConcatAutomate(Automate a, Automate b) {
    State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.isAccepting = false;
    aFinalState.addTransition(b.startingStateId);
    a.mergeAutomaton(b);
    a.tmpNDFAFinalId = b.tmpNDFAFinalId;
    return a;
  }

  public static Automate CreateStarAutomate(Automate a) {
    State s1 = new State();
    State s2 = new State(true);
    int s2Id = a.addState(s2);

    s1.addTransition(a.startingStateId);
    s1.addTransition(s2Id);

    State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(a.startingStateId);
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }

  public static Automate CreateAddAutomate(Automate a) {
    State s1 = new State();
    s1.addTransition(a.startingStateId);

    State s2 = new State(true);
    int s2Id = a.addState(s2);

    State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(a.startingStateId);
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }

  public static Automate CreateOrAutomate(Automate a, Automate b) {
    State s2 = new State(true);
    int s2Id = a.addState(s2);

    State s1 = new State();
    s1.addTransition(a.startingStateId);
    s1.addTransition(b.startingStateId);

    State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    State bFinalState = b.getTmpNDFAFinalState();
    bFinalState.addTransition(s2Id);
    bFinalState.isAccepting = false;

    a.mergeAutomaton(b);
    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }
}
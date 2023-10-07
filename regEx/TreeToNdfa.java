package regEx;

import java.util.concurrent.atomic.AtomicInteger;

public class TreeToNdfa {
  private static AtomicInteger _stateCounter;
  /**
   * The function "makeNDFA" creates a non-deterministic finite automaton (NDFA)
   * based on a given regular expression tree.
   *
   * @param tree The "tree" parameter is of type RegExTree, which represents a
   *     regular expression
   * parsed into a tree structure.
   * @return The method is returning an object of type Automate.
   */
  public static Automate makeNDFA(final RegExTree tree) {
    _stateCounter = new AtomicInteger(0);
    if (tree == null)
      return null;

    return makeNDFA_AUX(tree);
  }

  public static Automate makeNDFA_AUX(final RegExTree tree) {
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
    case RegEx.DOT:
      return CreateDotAutomate();

    default:
      return null;
    }
  }

  /**
   * The function creates a deterministic finite automaton (DFA) that accepts
   * any input character from a starting state to a final state.
   *
   * @return The method is returning an instance of the Automate class.
   */
  public static Automate CreateDotAutomate() {
    final State s1 = new State();
    final State s2 = new State(true);
    final Automate output = new Automate(s1, s2, _stateCounter);
    for (int i = 0; i < 256; i++)
      output.getStartingState().addTransition(i, output.tmpNDFAFinalId);

    return output;
  }

  /**
   * The function creates a basic case Automate object with two states and a
   * transition between them.
   *
   * @param rootCode The parameter "rootCode" is an integer value that
   *     represents the code for a
   * transition in the automaton.
   * @return The method is returning an instance of the Automate class.
   */
  public static Automate CreateBasisCaseAutomate(final int rootCode) {
    final State s1 = new State();
    final State s2 = new State(true);
    final Automate output = new Automate(s1, s2, _stateCounter);
    output.getStartingState().addTransition(rootCode, output.tmpNDFAFinalId);
    return output;
  }

  /**
   * The function creates a new automaton by concatenating two existing
   * automata.
   *
   * @param a The parameter "a" is an instance of the Automate class
   * @param b The parameter "b" is an instance of the Automate class.
   * @return The method is returning an Automate object.
   */
  public static Automate CreateConcatAutomate(final Automate a,
                                              final Automate b) {
    final State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.isAccepting = false;
    aFinalState.addTransition(b.startingStateId);
    a.mergeAutomaton(b);
    a.tmpNDFAFinalId = b.tmpNDFAFinalId;
    return a;
  }

  /**
   * The function "CreateStarAutomate" takes an Automate object as input and
   * modifies it to create a new Automate object that represents the Kleene star
   * operation on the original Automate.
   *
   * @param a The parameter "a" is an object of type Automate.
   * @return The method is returning an Automate object.
   */
  public static Automate CreateStarAutomate(final Automate a) {
    final State s1 = new State();
    final State s2 = new State(true);
    final int s2Id = a.addState(s2);

    s1.addTransition(a.startingStateId);
    s1.addTransition(s2Id);

    final State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(a.startingStateId);
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }

  public static Automate CreateAddAutomate(Automate a) {
    final State s1 = new State();
    s1.addTransition(a.startingStateId);

    final State s2 = new State(true);
    final int s2Id = a.addState(s2);

    final State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(a.startingStateId);
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }
  public static Automate CreateOrAutomate(Automate a, Automate b) {
    final State s2 = new State(true);
    final int s2Id = a.addState(s2);

    final State s1 = new State();
    s1.addTransition(a.startingStateId);
    s1.addTransition(b.startingStateId);

    final State aFinalState = a.getTmpNDFAFinalState();
    aFinalState.addTransition(s2Id);
    aFinalState.isAccepting = false;

    final State bFinalState = b.getTmpNDFAFinalState();
    bFinalState.addTransition(s2Id);
    bFinalState.isAccepting = false;

    a.mergeAutomaton(b);
    a.setStartingState(s1);
    a.tmpNDFAFinalId = s2Id;
    return a;
  }
}
package regEx;

public class TreeToNdfa {

	public static int stateCounter = 0;

	public static Automate makeNDFA(RegExTree tree) {
		if (tree == null)
			return null;

		if (!tree.isRootOperator())
			return CreateBasisCaseAutomate(tree.getRoot());
		switch (tree.getRoot()) {
			case RegEx.CONCAT:
				return CreateConcatAutomate(makeNDFA(tree.subTrees.get(0)), makeNDFA(tree.subTrees.get(1)));
			case RegEx.ETOILE:
				return CreateStarAutomate(makeNDFA(tree.subTrees.get(0)));
			case RegEx.ALTERN:
				return CreateOrAutomate(makeNDFA(tree.subTrees.get(0)),
						makeNDFA(tree.subTrees.get(1)));
			case RegEx.PLUS:
				return CreateConcatAutomate(makeNDFA(tree.subTrees.get(0)),
						CreateStarAutomate(makeNDFA(tree.subTrees.get(0))));
			default:
				return null;
		}

	}

	private static State allocateState(boolean isAccepting) {
		State s = new State(isAccepting, stateCounter);
		stateCounter++;
		return s;
	}

	public static Automate CreateBasisCaseAutomate(int rootCode) {
		State s1 = allocateState(false);
		State s2 = allocateState(true);
		s1.addTransition(new Transition(rootCode, s2));
		return new Automate(s1, s2);
	}

	public static Automate CreateConcatAutomate(Automate a, Automate b) {
		State bStartingState = b.getStartingState();
		State aFinalState = a.getFinalState();

		aFinalState.setAccepting(false);
		aFinalState.addTransition(new Transition(bStartingState));

		a.addState(aFinalState);
		a.setFinalState(b.getFinalState());
		a.addState(bStartingState);
		a.addAllStates(b.getStates());
		return a;
	}

	public static Automate CreateStarAutomate(Automate a) {
		State s1 = allocateState(false);
		State s2 = allocateState(true);
		State aFinalState = a.getFinalState();
		State aStartingState = a.getStartingState();

		s1.addTransition(new Transition(aStartingState));
		s1.addTransition(new Transition(s2));

		aFinalState.addTransition(new Transition(aStartingState));
		aFinalState.addTransition(new Transition(s2));
		aFinalState.setAccepting(false);

		a.addState(aStartingState);
		a.addState(aFinalState);

		a.setStartingState(s1);
		a.setFinalState(s2);
		return a;
	}

	public static Automate CreateOrAutomate(Automate a, Automate b) {
		State s1 = allocateState(false);
		State s2 = allocateState(true);
		State aStartingState = a.getStartingState();
		State bStartingState = b.getStartingState();
		State aFinalState = a.getFinalState();
		State bFinalState = b.getFinalState();

		s1.addTransition(new Transition(aStartingState));
		s1.addTransition(new Transition(bStartingState));

		aFinalState.addTransition(new Transition(s2));
		aFinalState.setAccepting(false);

		bFinalState.addTransition(new Transition(s2));
		bFinalState.setAccepting(false);

		a.addState(aStartingState);
		a.addState(aFinalState);
		a.addState(bStartingState);
		a.addState(bFinalState);

		a.addAllStates(b.getStates());

		a.setStartingState(s1);
		a.setFinalState(s2);
		return a;
	}
}
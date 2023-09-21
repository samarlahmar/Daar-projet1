package regEx;

import java.util.ArrayList;

public class Automate {

	private State starting_state;
	private ArrayList<State> states;
	private State final_state;

	public Automate(State starting_state, State final_state) {
		this.starting_state = starting_state;
		this.final_state = final_state;
		this.states = new ArrayList<State>();

	}

	public ArrayList<State> getStates() {
		return this.states;
	}

	public void setStates(ArrayList<State> states) {
		this.states = states;
	}

	public void setStartingState(State starting_state) {
		this.starting_state = starting_state;
	}

	public State getStartingState() {
		return this.starting_state;
	}

	public void setFinalState(State final_state) {
		this.final_state = final_state;
	}

	public State getFinalState() {
		return this.final_state;
	}

	public void addState(State s) {
		this.states.add(s);
	}

	public void addAllStates(ArrayList<State> states) {
		for (State s : states) {
			this.states.add(s);
		}
	}

	/*
	 * public String toString() {
	 * String s = getState().getId() + " ";
	 * ;
	 * if ((nextAutomates == null) || (transitions == null)) {
	 * return (s);
	 * }
	 * String result = s + transitions.get(0).getId() + " " +
	 * nextAutomates.get(0).toString();
	 * for (int i = 1; i < nextAutomates.size(); i++)
	 * result += " " + transitions.get(i).getId() + " " +
	 * nextAutomates.get(i).toString();
	 * return result + " ";
	 * 
	 * }
	 */

	public int getNumberOfStates() {
		return states.size();
	}

	public String toDotString() {
		String s = "digraph finite_state_machine {\n" + "rankdir=LR;\n" + "size=\"8,5\"\n"
				+ "node [shape = doublecircle]; "
				+ final_state.getStateID() + ";\n" + "node [shape = circle];\n";

		s += starting_state.toDotString(true);
		for (State state : states)
			s += state.toDotString(false);
		s += final_state.toDotString(false);
		s += "}";
		return s;
	}

}

package regEx;

import java.util.ArrayList;
public class Automate {

	private State state;
	private ArrayList<transition> transitions ;
	private ArrayList<Automate> nextAutomates;

	public Automate(State state , ArrayList<transition> transitions , ArrayList<Automate> nextAutomates) {
		this.setState(state) ;
		this.transitions= transitions ;
		this.setNextAutomates(nextAutomates);
		
		
	}

	public ArrayList<Automate> getNextAutomates() {
		return nextAutomates;
	}

	public void setNextAutomates(ArrayList<Automate> nextAutomates) {
		this.nextAutomates = nextAutomates;
	}

	public void setTransitions(ArrayList<transition> transitions) {
		this.transitions = transitions ;
	}
	
	public transition getTransitions(transition transitions) {
		return transitions  ;
	}
	
	public void addNextAutomate (Automate a) {
		this.nextAutomates.add(a) ;
	}
	
	public void addtransition (transition a) {
		this.transitions.add(a) ;
	}
	
	public String toString() {
		String s = getState().getId()+" "; ;
		if ((nextAutomates== null)|| (transitions == null ) ){
			 return(s) ;
		 }
		    String result = s +transitions.get(0).getId()+" "+nextAutomates.get(0).toString();
		    for (int i=1;i<nextAutomates.size();i++) result+=" "+transitions.get(i).getId()+" "+nextAutomates.get(i).toString();
		    return result+" ";
		    
	}
	
	public  int getNumberOfStates() {
		int r =1 ;
		if ((nextAutomates.size() == 0) || (nextAutomates == null)) {
			return r ;
		}
		int res = r+nextAutomates.get(0).getNumberOfStates() ;
		for (int i=1;i<nextAutomates.size();i++) res+= nextAutomates.get(i).getNumberOfStates() ;
 		return res ;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	
}

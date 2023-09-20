package regEx;

import java.util.ArrayList;


public class TreeToNdfa {



private static ArrayList<Integer> result = new ArrayList<>();

public static int number = 0;
public static int num = 0;

public static ArrayList<Integer> bottomUp(RegExTree tree) {
	
		        if (tree == null) {
		            return null;
		        }

		        // Parcours bottom-up des sous-arbres
		        for (RegExTree subTree : tree.subTrees) {
		            bottomUp(subTree);
		        }

		        // Traitement du nœud courant (ici, affichage de la valeur du nœud)
		        result.add(tree.root);
		       
		       return result ;
		    }

public static Automate CreateBasisCaseAutomate(Integer i) { 
	
	
	State s1 = new State(number, false, true) ;
	number++ ;
	transition t = new transition(i) ;
	ArrayList<transition> transitions = new ArrayList<>() ;
	transitions.add(t);
	State s2 = new State(number, true, false) ;
	number++ ;
	Automate subAutomate = new Automate(s2, null, null);
	
	ArrayList<Automate> subAutomates = new ArrayList<>() ;
	subAutomates.add(subAutomate);
	
	Automate AutomateResult = new Automate(s1, transitions, subAutomates);
	
	
	return AutomateResult ;
} 


public static Automate CreateConcatAutomate(Automate a1 , Automate a2) { 
	transition t = new transition(RegEx.Epsilon) ;
	Automate last = a1.getNextAutomates().get(a1.getNextAutomates().size()-1) ;
	last.addtransition(t); 
	last.getState().setDeterministe(false); 
	last.addNextAutomate(a2);
	return a1 ;
}

public static Automate CreateStarAutomate(Automate a) { 
	
	State s1 = new State(num, false, true) ;
	num++ ;
	a.getState().setStarting(false);
	int lastnum = a.getNumberOfStates() ;
	ResetStateNumbers(a) ;
	
	State s4 = new State(num+1+lastnum, false, true) ;
	Automate a4 = new Automate(s4, null, null) ;
	Automate last = a.getNextAutomates().get(a.getNextAutomates().size()-1) ;
	transition t = new transition(RegEx.Epsilon) ;
	last.getState().setDeterministe(false); ;
	last.addtransition(t); 
	last.addNextAutomate(a);
	last.addtransition(t); 
	last.addNextAutomate(a4);
	
	Automate a1 = new Automate(s1, null, null) ;
	a1.addtransition(t);
	a1.addNextAutomate(a);
	a1.addtransition(t);
	a1.addNextAutomate(a4);
	return a1 ;
}

public static void ResetStateNumbers(Automate a) {
	if ((a.getNextAutomates().size() == 0) || (a.getNextAutomates() == null)) {
		a.getState().setId(num);
		num++ ;
	} 
	ResetStateNumbers(a.getNextAutomates().get(0)) ;
	for (int i=1;i<a.getNextAutomates().size();i++) ResetStateNumbers(a.getNextAutomates().get(i))  ;
		
	
	
}



}
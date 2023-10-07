package regEx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class State {
  protected Map<Integer, Collection<Integer>> _transitions;
  protected Map<Integer, State> dfaTransition;
  protected boolean isAccepting = false;

  // Declaring a string format that will be used to generate a DOT
  // representation of a state in a finite automaton.
  private static final String stateFormat =
      "%d [label=\"%s\" color=\"%s\" shape=\"%s\"]\n";
  private static final String transitionFormat = "%d -> %d [label=\"%c\"]\n";

  public State() {
    this._transitions = new HashMap<Integer, Collection<Integer>>();
  }

  public State(final boolean isAccepting) {
    this._transitions = new HashMap<Integer, Collection<Integer>>();
    this.isAccepting = isAccepting;
  }

  /**
   * The function returns the destination state for a given symbol in a
   * collection of transitions.
   *
   * @param symbol The symbol parameter is an Integer that represents a symbol
   *     used in the transitions.
   * @return The method is returning an Integer value.
   */
  public Integer getDestinationState(final Integer symbol) {
    final Collection<Integer> destinations = _transitions.get(symbol);
    if (destinations == null)
      return null;
    return destinations.iterator().next();
  }

  /**
   * The function returns the destination state in a DFA given a symbol.
   *
   * @param symbol The symbol parameter is an Integer representing the input
   *     symbol for the DFA transition.
   * @return The method is returning a State object.
   */
  public State getDFADestinationState(final Integer symbol) {
    return dfaTransition.get(symbol);
  }

  /**
   * The function "isEquiv" checks if two State objects are equivalent based on
   * their accepting status and transitions.
   *
   * @param a The parameter "a" is an object of type "State".
   * @param b The parameter "b" is an object of type "State".
   * @return The method is returning a boolean value.
   */
  public static boolean isEquiv(State a, State b) {

    return a.isAccepting == b.isAccepting &&
        a._transitions.size() == b._transitions.size() &&
        a._transitions.equals(b._transitions);
  }

  public void addTransition(final Integer symbol,
                            Collection<Integer> newDestinations) {

    Collection<Integer> oldDestination = this._transitions.get(symbol);
    if (oldDestination == null)
      oldDestination = new HashSet<Integer>();
    newDestinations.addAll(oldDestination);
    this._transitions.put(symbol, newDestinations);
  }

  public void setTransition(final Integer symbol,
                            Collection<Integer> newDestinations) {
    this._transitions.put(symbol, newDestinations);
  }

  /**
   * The function "absorbeState" merges the transitions and updates the
   * accepting state  boolean value.
   *
   * @param other The "other" parameter is an instance of the "State" class.
   */
  public void absorbeState(final State other) {
    other._transitions.forEach((k, v) -> { this.addTransition(k, v); });
    this.isAccepting = this.isAccepting || other.isAccepting;
  }

  public void deleteTransitionWithKey(final Integer symbol) {
    this._transitions.remove(symbol);
  }

  /**
   * The function takes an integer symbol as input and returns the corresponding
   * character representation, with special cases for epsilon, dot
   *
   * @param symbol The symbol parameter is an Integer representing a symbol in a
   *     regular expression.
   * @return The method is returning a Character.
   */
  private Character getChar(final Integer symbol) {
    if (symbol == RegEx.EPSILON)
      return 'ε';
    if (symbol == RegEx.DOT)
      return '.';
    if (Character.isLetterOrDigit(symbol))
      return (char)symbol.intValue();

    return '?';
  }

  /**
   * The function generates a DOT string representation of a state in a finite
   * automaton.
   *
   * @param outBuffer The `outBuffer` parameter is a `StringBuilder` object that
   *     is used to build the
   * dot string representation of the state.
   * @param stateID The stateID parameter represents the ID of a state in a
   *     finite state machine.
   */
  public void toDotString(final StringBuilder outBuffer, final int stateID) {
    String color = "black";
    String shape = "circle";
    if (isAccepting) {
      color = "green";
      shape = "doublecircle";
    }
    outBuffer.append(
        String.format(stateFormat, stateID, stateID, color, shape));

    for (Entry<Integer, Collection<Integer>> entry : _transitions.entrySet()) {
      final int keycode = entry.getKey();

      for (final int destination : entry.getValue())
        outBuffer.append(String.format(transitionFormat, stateID, destination,
                                       getChar(keycode)));
    }
  }

  public void addTransition(final Integer symbol,
                            final Integer newDestination) {
    final ArrayList<Integer> tmp = new ArrayList<Integer>();
    tmp.add(newDestination);
    this.addTransition(symbol, tmp);
  }

  public void setTransition(final Integer symbol,
                            final Integer newDestination) {
    this.setTransition(symbol, Collections.singleton(newDestination));
  }

  public void addTransition(final Integer newDestination) {
    addTransition(RegEx.EPSILON, newDestination);
  }

  public Collection<Integer> getTransition(final Integer symbol) {
    return _transitions.get(symbol);
  }
}

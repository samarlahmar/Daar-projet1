package regExV2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class State {
  protected Map<Integer, Collection<Integer>> _transitions;
  public boolean isAccepting = false;

  protected static final String stateFormat =
      "%d [label=\"%s\" color=\"%s\" shape=\"%s\"]\n";
  protected static final String transitionFormat = "%d -> %d [label=\"%c\"]";

  public State() {
    this._transitions = new HashMap<Integer, Collection<Integer>>();
  }

  public State(final boolean isAccepting) {
    this._transitions = new HashMap<Integer, Collection<Integer>>();
    this.isAccepting = isAccepting;
  }

  public Collection<Integer> getDestinationStates(final Integer symbol) {
    return _transitions.get(symbol);
  }

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

  public void addTransition(final Integer symbol,
                            final Integer newDestination) {
    Collection<Integer> oldDestination = this._transitions.get(symbol);
    if (oldDestination == null)
      oldDestination = new HashSet<Integer>();

    oldDestination.add(newDestination);
    this.addTransition(symbol, oldDestination);
  }

  public void setTransition(final Integer symbol,
                            final Integer newDestination) {
    this.setTransition(symbol, Collections.singleton(newDestination));
  }

  public void addTransition(final Integer newDestination) {
    addTransition(RegEx.Epsilon, newDestination);
  }

  public void setTransition(final Integer newDestination) {
    setTransition(RegEx.Epsilon, newDestination);
  }

  public Collection<Integer> getTransition(final Integer symbol) {
    return _transitions.get(symbol);
  }

  public void absorbeState(final State other) {
    other._transitions.forEach((k, v) -> { this.addTransition(k, v); });
    this.isAccepting = this.isAccepting || other.isAccepting;
  }

  public void deleteTransitionWithKey(final Integer symbol) {
    this._transitions.remove(symbol);
  }

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
        outBuffer.append(
            String.format(transitionFormat, stateID, destination,
                          keycode != RegEx.Epsilon ? keycode : 'ε'));
    }
  }
}

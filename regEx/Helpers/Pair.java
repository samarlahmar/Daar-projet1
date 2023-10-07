package regEx.Helpers;

/**
 * The Pair class is a generic class that represents a pair of two values.
 */
public class Pair<T1, T2> {
  public T1 first;
  public T2 second;

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public String toString() {
    return "(" + first.toString() + ", " + second.toString() + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Pair))
      return false;
    Pair<?, ?> other = (Pair<?, ?>)obj;
    return first.equals(other.first) && second.equals(other.second);
  }
}

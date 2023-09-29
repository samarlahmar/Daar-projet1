package regEx;

public class Transition {

  private int _accepted_Code;
  private int _destination_id;

  public Transition(int new_code, State destination) {
    this.setAccepted_Code(new_code);
    this.setDestination_id(destination.getStateID());
  }

  public Transition(State destination) {
    this.setAccepted_Code(RegEx.Epsilon);
    this.setDestination_id(destination.getStateID());
  }

  public void setAccepted_Code(int new_code) { this._accepted_Code = new_code; }

  public int getAccepted_Code() { return this._accepted_Code; }

  public char getAccepted_Char() {
    if (this._accepted_Code == RegEx.Epsilon)
      return 'ε';

    return Character.toChars(this._accepted_Code)[0];
  }

  public void setDestination_id(int new_destination_id) {
    this._destination_id = new_destination_id;
  }

  public int getDestination_id() { return this._destination_id; }

  public int travel(int code) {
    if (code == this._accepted_Code)
      return this._destination_id;
    return -1;
  }

  public String toDotString(int source_id) {
    return source_id + " -> " + this._destination_id + " [label=\"" +
        this.getAccepted_Char() + "\"];\n";
  }
  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof Transition))
      return false;
    Transition other = (Transition)obj;
    return this._accepted_Code == other._accepted_Code &&
        this._destination_id == other._destination_id;
  }
}

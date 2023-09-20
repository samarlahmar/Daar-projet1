package regEx;

public class State {

	private int id;
	private boolean isStarting;
	private boolean isDeterministe;

	public State(int id , boolean isDeterministe , boolean isStarting) {
		this.setId(id) ;
		this.isDeterministe = isDeterministe ;
		this.setStarting(isStarting) ;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public boolean getDeterministe() {
		return isDeterministe;
	}

	public void setDeterministe(boolean isDeterministe) {
		this.isDeterministe = isDeterministe;
	}

	public boolean isStarting() {
		return isStarting;
	}

	public void setStarting(boolean isStarting) {
		this.isStarting = isStarting;
	}
}

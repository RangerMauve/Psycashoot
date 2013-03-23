package rangermauve.games.psycashoot.events;

public class TickEvent implements Event {
	private int delta;

	public TickEvent(int delta) {
		this.delta = delta;
	}

	public int getDelta() {
		return delta;
	}

	public String getType() {
		return "TickEvent";
	}
}

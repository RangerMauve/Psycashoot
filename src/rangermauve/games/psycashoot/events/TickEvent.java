package rangermauve.games.psycashoot.events;

/**
 * This is called for every game update tick
 * @author Mauve
 */
public class TickEvent implements Event {
	private int delta;
	
	/**
	 * Call the event with the elapsed time
	 * @param delta the time in milliseconds
	 */
	public TickEvent(int delta) {
		this.delta = delta;
	}
	
	/**
	 * Gets the elapsed time
	 * @return the time in milliseconds
	 */
	public int getDelta() {
		return delta;
	}
	
	public String getType() {
		return "TickEvent";
	}
}

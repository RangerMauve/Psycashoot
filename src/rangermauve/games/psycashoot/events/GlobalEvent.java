package rangermauve.games.psycashoot.events;

/**
 * Wrapper for regular events that will be 
 * passed from the global scope to all entities
 * @author Mauve
 *
 */
public class GlobalEvent implements Event {
	private Event toShoot;
	
	/**
	 * Encapsulate an event to be passed to eligible entities
	 * @param event event to be passed on
	 */
	public GlobalEvent(Event event) {
		this.toShoot = event;
	}
	
	/**
	 * @return the encapsulated event
	 */
	public Event getEvent() {
		return toShoot;
	}

	public String getType() {
		return "GlobalEvent";
	}
}
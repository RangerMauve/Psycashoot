package rangermauve.games.psycashoot.events;

/**
 * This is the basic interface for any events processed in the game
 * 
 * @author Mauve
 * 
 */
public interface Event {
	/**
	 * The unique name of the event
	 * 
	 * @return The type of the event
	 */
	public String getType();
}

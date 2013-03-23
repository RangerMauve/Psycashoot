package rangermauve.games.psycashoot.events;

import rangermauve.games.psycashoot.Entity;

/**
 * A generic event class that can be used for anything involving an Entity
 * @author Mauve
 *
 */
public class EntityEvent implements Event{
	private Entity entity;
	private String type = "EntityEvent";
	
	/**
	 * Construct with origin entity
	 * @param entity The entity affected by the event
	 */
	public EntityEvent(Entity entity){
		this.entity = entity;
	}
	
	/**
	 * Sets the event for easy event creation
	 * @param type The unique event name
	 */
	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return this.type;
	}
	
	/**
	 * Gets the entity affected by the event
	 * @return the affected entity
	 */
	public Entity getEntity(){
		return this.entity;
	}
}

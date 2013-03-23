package rangermauve.games.psycashoot.events;

import rangermauve.games.psycashoot.Entity;

/**
 * Makes handling EntityEvents easier
 * @author Mauve
 *
 */
public class EntityEventHandler extends EventHandler{
	public void handle(Event event){
		if(event instanceof EntityEvent){
			this.handle(((EntityEvent)event).getEntity());
		}
	}
	
	/**
	 * Takes Entity out of EntityEvent for ease of use
	 * @param entity the affected Entity
	 */
	public void handle(Entity entity){
		// Do nothing by default
	}
}

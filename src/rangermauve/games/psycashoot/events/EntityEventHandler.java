package rangermauve.games.psycashoot.events;

import rangermauve.games.psycashoot.Entity;

public class EntityEventHandler extends EventHandler{
	public void handle(Event event){
		if(event instanceof EntityEvent){
			this.handle(((EntityEvent)event).getEntity());
		}
	}
	
	public void handle(Entity entity){
		// Do nothing by default
	}
}

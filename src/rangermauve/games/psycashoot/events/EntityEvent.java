package rangermauve.games.psycashoot.events;

import rangermauve.games.psycashoot.Entity;

public class EntityEvent implements Event{
	private Entity entity;
	private String type = "EntityEvent";
	
	public EntityEvent(Entity entity){
		this.entity = entity;
	}
	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public Entity getEntity(){
		return this.entity;
	}
}

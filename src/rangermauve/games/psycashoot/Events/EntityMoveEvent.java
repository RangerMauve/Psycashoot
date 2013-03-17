package rangermauve.games.psycashoot.Events;

import processing.core.PVector;
import static processing.core.PApplet.*;

public class EntityMoveEvent implements Event {
	private String entityId;
	private float x;
	private float y;

	public EntityMoveEvent(String id, PVector position) {
		this.entityId = id;
		this.x = position.x;
		this.y = position.y;
	}

	public String getName() {
		return "EntityMoveEvent";
	}

	public String getEntityId() {
		return entityId;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

}

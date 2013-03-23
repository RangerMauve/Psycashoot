package rangermauve.games.psycashoot.events;

import rangermauve.games.psycashoot.Entity;

public class CollisionEvent implements Event {
	private Entity origin, other;

	public CollisionEvent(Entity origin, Entity other) {
		this.origin = origin;
		this.origin = other;
	}

	public Entity getOrigin() {
		return origin;
	}

	public Entity getOther() {
		return other;
	}

	public String getType() {
		return "EntityCollision";
	}
}

package rangermauve.games.psycashoot;

import processing.core.PVector;
import rangermauve.games.psycashoot.Events.EntityMoveEvent;
import rangermauve.games.psycashoot.Events.EventManager;

public abstract class Entity extends EventManager {
	private World world;
	private PVector radius = new PVector(1, 1);
	private PVector position = new PVector();
	private boolean moveable = false;
	private boolean collideable = true;
	private String id = null;

	public void moveTo(PVector position) {
		world.getSectionAt(this.position).remove(this);
		world.getSectionAt(position).add(this);
		this.position.set(position);
		world.emit(new EntityMoveEvent(this.id, this.position));
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public PVector getRadius() {
		return radius;
	}

	public void setRadius(PVector radius) {
		this.radius = radius;
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector position) {
		this.position = position;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public boolean isCollideable() {
		return collideable;
	}

	public void setCollideable(boolean collideable) {
		this.collideable = collideable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
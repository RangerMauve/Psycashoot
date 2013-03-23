package rangermauve.games.psycashoot;

import processing.core.PGraphics;
import processing.core.PVector;
import static processing.core.PVector.*;
import static processing.core.PApplet.abs;
import rangermauve.games.psycashoot.events.EntityEvent;
import rangermauve.games.psycashoot.events.EventManager;

public abstract class Entity extends EventManager {
	private World world;
	private PVector radius = new PVector(1, 1);
	private PVector position = new PVector(), velocity = new PVector(),
			acceleration = new PVector(), fposition = new PVector(),
			fvelocity = new PVector(), facceleration = new PVector();
	private float damping = 0.01f;
	private boolean moveable = false;
	private boolean collideable = true;
	private String id = null;

	public void draw(PGraphics g) {
		// Draw code for the entity
	}

	public void emitChange(String type) {
		EntityEvent e = new EntityEvent(this);
		e.setType(type);
		this.emit(e);
		world.emit(e);
	}

	void updatePhysics() {
		this.position.set(this.fposition);
		this.velocity.set(this.fvelocity);
		this.acceleration.set(this.facceleration);

		this.velocity.add(this.acceleration);
		this.position.add(this.velocity);

		this.velocity.mult(1f - damping);
		this.acceleration.mult(1f - damping);

		this.fposition.set(this.position);
		this.fvelocity.set(this.velocity);
		this.facceleration.set(this.acceleration);

		world.getSectionAt(this.position).remove(this);
		world.getSectionAt(position).add(this);
		this.position.set(position);
		this.emitChange("EntityMove");
	}

	public PVector getPosition() {
		return this.position.get();
	}

	public void moveTo(PVector position) {
		this.fposition.set(position);
	}

	public void moveBy(PVector distance) {
		this.moveTo(add(this.position, distance));
	}

	public PVector getVelocity() {
		return this.velocity.get();
	}

	public void hasten(PVector velocity) {
		this.fvelocity.add(velocity);
	}

	public void hasten(float velocity) {
		this.fvelocity.mult(velocity);
	}

	public void setVelocity(PVector velocity) {
		this.fvelocity.set(velocity);
	}

	public PVector getAcceleration() {
		return this.acceleration.get();
	}

	public void accelerate(PVector acceleration) {
		this.facceleration.add(acceleration);
	}

	public void accelerate(float acceleration) {
		this.facceleration.mult(acceleration);
	}

	public void setAcceleration(PVector acceleration) {
		this.facceleration.set(acceleration);
	}

	public float getDamping() {
		return this.damping;
	}

	public void setDamping(float damping) {
		this.damping = damping;
		if (damping < 0f)
			this.damping *= -1;
	}

	public void stop() {
		this.facceleration.mult(0f);
		this.fvelocity.mult(0f);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.emitChange("EntityChangeWorld");
		this.world = world;
	}

	public PVector getRadius() {
		return radius.get();
	}

	public void setRadius(PVector radius) {
		this.emitChange("EntityRadius");
		this.radius = radius;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.emitChange("EntityMoveability");
		this.moveable = moveable;
	}

	public boolean isCollideable() {
		return collideable;
	}

	public void setCollideable(boolean collideable) {
		this.emitChange("EntityCollidability");
		this.collideable = collideable;
	}

	public boolean isColliding(Entity other) {
		if (this.collideable
				&& other.collideable
				&& ((this.position.x - this.radius.x < other.position.x
						+ other.radius.x)
						|| (this.position.x + this.radius.x > other.position.x
								- other.radius.x)
						|| (this.position.y - this.radius.y < other.position.y
								+ other.radius.y) || (this.position.y
						+ this.radius.y > other.position.y - other.radius.y)))
			return false;
		return true;
	}

	public PVector getMoveAway(Entity other) {
		PVector res = new PVector();
		// Just in case, lazy checking of collision
		if (!(this.collideable && other.collideable))
			return res;

		PVector neg = new PVector(), pos = new PVector();

		neg.set(sub(other.position, this.position));
		neg.sub(this.radius);
		neg.add(other.radius);
		if (abs(neg.x) < abs(neg.y))
			neg.y = 0f;
		else
			neg.x = 0f;

		pos.set(sub(this.position, other.position));
		pos.add(this.radius);
		pos.sub(other.radius);
		if (abs(pos.x) < abs(pos.y))
			pos.y = 0f;
		else
			pos.x = 0f;

		if (neg.mag() < pos.mag())
			res.set(neg);
		else
			res.set(pos);
		
		return res;
	}

	public String getId() {
		return id + "";
	}

	public void setId(String id) {
		this.emitChange("EntityID");
		this.id = id;
	}
}
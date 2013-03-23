package rangermauve.games.psycashoot;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.pow;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;
import static processing.core.PVector.add;
import static processing.core.PVector.sub;

import java.util.HashSet;
import java.util.Set;

import processing.core.PGraphics;
import processing.core.PVector;
import rangermauve.games.psycashoot.events.EntityEvent;
import rangermauve.games.psycashoot.events.EventManager;

/**
 * The base class for things that will be displayed in the world
 * 
 * @author Mauve
 * 
 */
public abstract class Entity extends EventManager {
	private World world;
	private PVector dimensions = new PVector(1, 1);
	private PVector position = new PVector(), velocity = new PVector(),
			acceleration = new PVector(), fposition = new PVector(),
			fvelocity = new PVector(), facceleration = new PVector();
	private float rotation = 0f;
	private float velocityDamping = 0.01f;
	private float accelerationDamping = 0.01f;
	private boolean moveable = false;
	private boolean solid = false;
	private boolean collideable = false;
	private String id = null;

	/**
	 * Called by client to draw the entity to the screen. Drawing matrix already
	 * set so entity is positioned properly.
	 * 
	 * @param g
	 *            The graphics surface to draw to
	 */
	public abstract void draw(PGraphics g);

	/**
	 * Initializes the Entity so that it's constructor-independent
	 */
	public abstract void init();

	/**
	 * Emits a EntityEvent set to the type provided. Generic way of notifying
	 * the world that the entity's state has changed.
	 * 
	 * @param type
	 *            The name of the event to emit
	 */
	public void emitChange(String type) {
		EntityEvent e = new EntityEvent(this);
		e.setType(type);
		this.emit(e);
		world.emit(e);
	}

	// Used by world
	void updatePhysics(int ticks) {
		PVector oldpos = this.position;
		this.position.set(this.fposition);
		this.velocity.set(this.fvelocity);
		this.acceleration.set(this.facceleration);

		this.velocity.add(this.acceleration);
		if (this.isMoveable())
			this.position.add(this.velocity);
		this.velocity.mult(ticks);
		this.acceleration.mult(ticks);
		this.velocity.mult(pow(1f - velocityDamping, ticks));
		this.acceleration.mult(pow(1f - velocityDamping, ticks));

		this.fposition.set(this.position);
		this.fvelocity.set(this.velocity);
		this.facceleration.set(this.acceleration);

		if (!oldpos.equals(position)) {
			this.emitChange("EntityMove");
			world.getSectionAt(oldpos).remove(this);
			world.getSectionAt(position).add(this);
		}
	}

	/**
	 * Looks at sections of the world that the entity may be touching and
	 * returns a set of all entities that may be affected.
	 * 
	 * @return Set of affectable entities
	 */
	public Set<Entity> getAffectableEntities() {
		Set<Entity> ents = new HashSet<Entity>();
		int res = world.getResolution();
		int xstart = (int) (this.position.x - this.getDimensions().x - res);
		int xend = (int) (this.position.x + this.getDimensions().x + res);
		int ystart = (int) (this.position.y - this.getDimensions().y - res);
		int yend = (int) (this.position.y + this.getDimensions().y + res);
		for (int x = xstart; x < xend; x += res) {
			for (int y = ystart; y < yend; y += res) {
				ents.addAll(world.getSectionAt(new PVector(x, y)));
			}
		}
		if (ents.contains(this))
			ents.remove(this);
		return ents;
	}

	/**
	 * @return the current position of the entity
	 */
	public PVector getPosition() {
		return this.position.get();
	}

	/**
	 * Calculates the distance between the entity and a given point
	 * 
	 * @param other
	 *            Point to calculate distance to
	 * @return the distance to the point
	 */
	public float getDistance(PVector other) {
		return abs(sub(this.position, other).mag());
	}

	/**
	 * Calculates the distance to get to another entity
	 * 
	 * @param other
	 *            Entity to get distance to
	 * @return the distance between the entities
	 */
	public float getDistance(Entity other) {
		return this.getDistance(other.position);
	}

	/**
	 * Moves entity to absolute position
	 * 
	 * @param position
	 */
	public void moveTo(PVector position) {
		this.fposition.set(position);
	}

	/**
	 * Moves the entity by a certain amount
	 * 
	 * @param distance
	 *            Vector for displacement of entity
	 */
	public void moveBy(PVector distance) {
		this.moveTo(add(this.position, distance));
	}

	/**
	 * Returns the current velocity of the entity
	 * 
	 * @return velocity vector in pixels per tick
	 */
	public PVector getVelocity() {
		return this.velocity.get();
	}

	/**
	 * Changes the velocity of the entity
	 * 
	 * @param velocity
	 *            Amount to change velocity by
	 */
	public void hasten(PVector velocity) {
		this.fvelocity.add(velocity);
	}

	/**
	 * Changes the speed of the entity by a multiplier
	 * 
	 * @param velocity
	 *            amount to scale the velocity by
	 */
	public void hasten(float velocity) {
		this.fvelocity.mult(velocity);
	}

	/**
	 * Sets the velocity of the entity
	 * 
	 * @param velocity
	 *            velocity to set to
	 */
	public void setVelocity(PVector velocity) {
		this.fvelocity.set(velocity);
	}

	/**
	 * Returns the current acceleration of the entity
	 * 
	 * @return acceleration of the entity in pixels per tic squared
	 */
	public PVector getAcceleration() {
		return this.acceleration.get();
	}

	/**
	 * Increases acceleration of entity
	 * 
	 * @param acceleration
	 *            amount to increase acceleration by
	 */
	public void accelerate(PVector acceleration) {
		this.facceleration.add(acceleration);
	}

	/**
	 * Scales the acceleration
	 * 
	 * @param acceleration
	 *            amount to scale the acceleration by
	 */
	public void accelerate(float acceleration) {
		this.facceleration.mult(acceleration);
	}

	/**
	 * Sets the acceleration of the entity
	 * 
	 * @param acceleration
	 *            new acceleration of the entity
	 */
	public void setAcceleration(PVector acceleration) {
		this.facceleration.set(acceleration);
	}

	/**
	 * Returns the amount the velocity is slowed by per tick. Setting it to 1
	 * means that the entity will not be able to move. Setting it to 0 will
	 * prevent the entity from loosing speed over time. Values higher than 1 are
	 * ignored. Default is 0.01, or 1%
	 * 
	 * @return the current velocity damping
	 */
	public float getVelocityDamping() {
		return this.velocityDamping;
	}

	/**
	 * Sets the velocity damping of the entity
	 * 
	 * @param damping
	 */
	public void setDamping(float damping) {
		if (damping > 1f)
			return;
		this.velocityDamping = damping;
		if (damping < 0f)
			this.velocityDamping *= -1f;
		this.emitChange("EntityVelocityDampingChange");
	}

	/**
	 * Returns the amount the acceleration is slowed by per tick. Setting it to
	 * 1 means that the entity will keep it speed and ignore acceleration.
	 * Setting it to 0 will prevent the entity from loosing acceleration over
	 * time. Values higher than 1 are ignored. Default is 0.01, or 1%
	 * 
	 * @return the current acceleration damping
	 */
	public float getAccelerationDamping() {
		return accelerationDamping;
	}

	/**
	 * Sets the acceleration damping of the entity
	 * 
	 * @param accelerationDamping
	 *            new acceleration damping
	 */
	public void setAccelerationDamping(float damping) {
		if (damping > 1f)
			return;
		this.accelerationDamping = damping;
		if (damping < 0f)
			this.accelerationDamping *= -1f;
		this.accelerationDamping = damping;
		this.emitChange("EntityAccelerationDampingChange");
	}

	/**
	 * Stops the entity by setting it's acceleration and velocity to 0.
	 */
	public void stop() {
		this.emitChange("EntityForceStop");
		this.facceleration.mult(0f);
		this.fvelocity.mult(0f);
	}

	/**
	 * The world that the entity is currently located in.
	 * 
	 * @return
	 */
	public World getWorld() {
		return world;
	}

	void setWorld(World world) {
		this.emitChange("EntityChangeWorldChange");
		this.world = world;
	}

	/**
	 * Returns the dimensions of the Entity which are scaled by the rotation
	 * 
	 * @return rotated dimension vector
	 */
	public PVector getDimensions() {
		PVector v = this.dimensions.get();
		v.rotate(this.rotation);
		return v;
	}

	/**
	 * Sets the dimensions (width/2, height/2) of the entity
	 * 
	 * @param dimension
	 */
	public void setDimensions(PVector dimension) {
		this.emitChange("EntityRadiusChange");
		this.dimensions = dimension;
	}

	/**
	 * Returns the current rotation as an angle
	 * 
	 * @return angle in radians
	 */
	public float getRotationAngle() {
		return this.rotation;
	}

	/**
	 * Returns the current rotation as a unit vector
	 * 
	 * @return vector to represent the rotation, mag of 1
	 */
	public PVector getRotationVector() {
		return PVector.fromAngle(this.rotation);
	}

	/**
	 * Used in calculating if the Entity will can be moved by collisions or
	 * momentum.
	 * 
	 * @return whether the entity can be moved
	 */
	public boolean isMoveable() {
		return moveable;
	}

	/**
	 * Changes whether the entity can be moved
	 * 
	 * @param moveable
	 *            whether the entity will be movable or not
	 */
	public void setMoveable(boolean moveable) {
		this.emitChange("EntityMoveabilityChange");
		this.moveable = moveable;
	}

	/**
	 * Returns whether entity undergoes collision detection. If true, then it
	 * will generate an event when colliding with something.
	 * 
	 * @return whether collision detection is performed
	 */
	public boolean isCollideable() {
		return collideable;
	}

	/**
	 * Changes whether the entity undergoes collision detection
	 * 
	 * @param collideable
	 *            whether collision detection should occure
	 */
	public void setCollideable(boolean collideable) {
		this.emitChange("EntityCollidabilityChange");
		this.collideable = collideable;
	}

	/**
	 * Returns whether the entity can be moved by a collision.
	 */
	public boolean isSolid() {
		return solid;
	}
	
	/**
	 * Sets whether the entity can be moved by a collision
	 * @param solid whether the entity can be moved by collision
	 */
	public void setSolid(boolean solid) {
		this.solid = solid;
	}
	
	/**
	 * Returns whether the entity is colliding with another entity
	 * @param other the entity to perform the collision with
	 * @return whether there is a collision
	 */
	public boolean isColliding(Entity other) {
		if ((this.position.x - this.getDimensions().x < other.position.x
				+ other.getDimensions().x)
				|| (this.position.x + this.getDimensions().x > other.position.x
						- other.getDimensions().x)
				|| (this.position.y - this.getDimensions().y < other.position.y
						+ other.getDimensions().y)
				|| (this.position.y + this.getDimensions().y > other.position.y
						- other.getDimensions().y))
			return false;
		return true;
	}
	
	/**
	 * Returns distance the entity would have to move to no longer collide with another
	 * @param other entity to get collision distance from
	 * @return distance to move away
	 */
	public PVector getMoveAway(Entity other) {
		PVector res = new PVector();
		// Just in case, lazy checking of collision
		if (!(this.collideable && other.collideable))
			return res;

		PVector neg = new PVector(), pos = new PVector();

		neg.set(sub(other.position, this.position));
		neg.sub(this.getDimensions());
		neg.add(other.getDimensions());
		if (abs(neg.x) < abs(neg.y))
			neg.y = 0f;
		else
			neg.x = 0f;

		pos.set(sub(this.position, other.position));
		pos.add(this.getDimensions());
		pos.sub(other.getDimensions());
		if (abs(pos.x) < abs(pos.y))
			pos.y = 0f;
		else
			pos.x = 0f;

		if (neg.mag() < pos.mag())
			res.set(neg);
		else
			res.set(pos);

		if (other.isMoveable())
			res.mult(0.5f);
		return res;
	}
	
	/**
	 * Returns the unique ID that the entity is identified with in the world.
	 * @return
	 */
	public String getId() {
		return id + "";
	}
	
	// Only the world should be able to do this
	void setId(String id) {
		this.emitChange("EntityIDChange");
		this.id = id;
	}
}
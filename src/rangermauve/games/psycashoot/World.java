package rangermauve.games.psycashoot;

import static processing.core.PApplet.floor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import processing.core.PVector;
import rangermauve.games.psycashoot.events.CollisionEvent;
import rangermauve.games.psycashoot.events.Event;
import rangermauve.games.psycashoot.events.EventHandler;
import rangermauve.games.psycashoot.events.EventManager;
import rangermauve.games.psycashoot.events.GlobalEvent;
import rangermauve.games.psycashoot.events.TickEvent;
import rangermauve.utils.RandomString;

/**
 * The default world simulator. The world is split up into sections that are
 * defined by it's resolution. Sections contain entities based on their
 * positions.
 * 
 * @author Mauve
 * 
 */
public class World extends EventManager {
	private int resolution = 16;
	private long lastTime = 0;
	private int tickLength = 200;
	private long delta = 0;
	private int deltaTicks = 0;
	private HashMap<String, WorldSection> sections = new HashMap<String, WorldSection>();
	private HashMap<String, Entity> entities = new HashMap<String, Entity>();
	private boolean isRunning = false;
	private boolean isUpdating = false;
	private Thread worldThread;

	/**
	 * Initializes some world variables and handlers
	 * 
	 * @param resolution
	 *            The resolution of the world sections
	 */
	public World(int resolution) {
		this.resolution = resolution;
		this.on("GlobalEvent", new EventHandler() {
			public void handleEvent(Event event) {
				if (event instanceof GlobalEvent) {
					GlobalEvent ge = (GlobalEvent) event;
					Event e = ge.getEvent();
					for (Entity en : getEntities()) {
						if (en.hasRegistered(e.getType())) {
							en.emit(e);
						}
					}
				}
			}
		});

	}

	/**
	 * Starts the world simulation thread
	 */
	public void start() {
		if (this.isRunning)
			return;
		this.lastTime = System.nanoTime() / 1000000;
		this.isRunning = true;
		this.isUpdating = true;
		worldThread = new WorldThread();
		worldThread.start();
	}

	/**
	 * Pauses updating the entities, events are still processed
	 */
	public void pause() {
		this.isUpdating = false;
	}

	/**
	 * Resumes entity updating
	 */
	public void resume() {
		this.isUpdating = true;
	}

	/**
	 * Stops the world simulation entirely
	 */
	public void stop() {
		this.isRunning = false;
	}

	/**
	 * This is called every loop when the server is updating
	 */
	public void update() {
		// Take care of the tick updating
		this.updateTicks();

		// Update the positions and physics of all the entities
		this.updatePhysics();

		// Check for collision on all entities
		this.updateCollision();
	}

	/**
	 * Updates the delta in time, and the deltaTicks. Emits global event for
	 * each game tick.
	 */
	public void updateTicks() {
		this.delta = (System.nanoTime() / 1000000) - lastTime;
		lastTime = System.nanoTime() / 1000000;
		lastTime -= delta % tickLength;
		int deltaTicks = (int) delta / tickLength;
		for (int i = deltaTicks; i != 0; --i) {
			this.emitGlobal(new TickEvent(tickLength));
		}
	}

	/**
	 * Updates the positions of all the entities based on the deltaTicks
	 */
	public void updatePhysics() {
		for (Entity e : this.getEntities()) {
			e.updatePhysics(deltaTicks);
		}
	}

	/**
	 * Checks for collision between all entities and moves them if they are
	 * moveable. If the other entity is collidable a CollisionEvent will be
	 * triggered. If the entity is solid and moveable, and the other entity is
	 * solid it will do a check. If the other entity is not moveable, it will
	 * the entity will move so that it would not be colliding anymore, else it
	 * will move half that distance.
	 */
	public void updateCollision() {
		for (Entity entity : this.getEntities()) {
			if (entity.isCollideable())
				for (Entity other : entity.getAffectableEntities()) {
					if (other.isCollideable()) {
						// Perform check for event generation
						if (!(!other.isMoveable() && !other.isSolid())) {
							this.emit(new CollisionEvent(entity, other));
							entity.emit(new CollisionEvent(entity, other));
						}

						// Perform check for entity movement
						if (entity.isSolid() && entity.isMoveable()) {
							entity.moveBy(entity.getMoveAway(other));
						}
					}
				}
		}
	}

	/**
	 * Gets the section that the point is within
	 * 
	 * @param point
	 *            point from where to get a section from
	 * @return the section that the point is within
	 */
	public WorldSection getSectionAt(PVector point) {
		String key = getSectionKey(point);
		if (!sections.containsKey(key)) {
			sections.put(key, new WorldSection());
		}
		return sections.get(key);
	}

	// Generates key for section map
	private String getSectionKey(PVector point) {
		int x = floor(point.x) / this.resolution;
		int y = floor(point.y / this.resolution);
		return x + "," + y;
	}

	/**
	 * Adds entity to the world. Sets entity with an id so that it can be
	 * retrieved later.
	 * 
	 * @param id
	 *            The unique ID to assign the entity.
	 * @param entity
	 *            The entity to add to the world.
	 */
	public void addEntity(String id, Entity entity) {
		entity.setId(id);
		this.entities.put(id, entity);
		this.getSectionAt(entity.getPosition()).add(entity);
	}

	/**
	 * Adds entity to the world. An ID is generated as an 9 character string.
	 * 
	 * @param entity
	 *            The entity to add to the world.
	 */
	public void addEntity(Entity entity) {
		String id = RandomString.make(8);
		while (entities.containsKey(id)) {
			id = RandomString.make(8);
		}
		this.addEntity(id, entity);
	}

	/**
	 * Removes the entity from the world based on its id.
	 * 
	 * @param id
	 *            The id of the entity to remove
	 */
	public void removeEntity(String id) {
		this.entities.remove(id);
	}

	/**
	 * Gets reference to entity object from ID
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity object or null
	 */
	public Entity getEntity(String id) {
		return entities.get(id);
	}
	
	/**
	 * Returns whether the entity ID has been registered within the world.
	 * @param id the unique entity id to check for
	 * @return whether the id has been registered with an entity
	 */
	public boolean hasEntity(String id){
		return entities.containsKey(id);
	}

	/**
	 * @return The world section resolution
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * @return The length of a game tick in milliseconds
	 */
	public int getTickLength() {
		return tickLength;
	}

	/**
	 * @param tickLength
	 *            amount of real time that a tick lasts
	 */
	public void setTickLength(int tickLength) {
		this.tickLength = tickLength;
	}

	/**
	 * @return Returns whether the world simulation is running
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @return Returns whether the entity updating is paused
	 */
	public boolean isPaused() {
		return !isUpdating;
	}

	/**
	 * Returns a set of all entities in the world. This includes entities from
	 * all sections
	 * 
	 * @return HashSet containing entities
	 */
	public Set<Entity> getEntities() {
		Set<Entity> res = new HashSet<Entity>();
		res.addAll(entities.values());
		return res;
	}

	/**
	 * Emits an event that is also broadcast to entities
	 * 
	 * @param event
	 */
	public void emitGlobal(Event event) {
		this.emit(new GlobalEvent(event));
	}

	public class WorldSection extends HashSet<Entity> {
		// Maybe add some custom code to the sections
	}

	private class WorldThread extends Thread {
		public WorldThread() {
			super("WorldThrad");
			setDaemon(true);
		}

		public void run() {
			while (isRunning()) {
				processEvents();
				if (!isPaused()) {
					update();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
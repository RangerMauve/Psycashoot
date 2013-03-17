package rangermauve.games.psycashoot;

import static processing.core.PApplet.floor;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PVector;
import rangermauve.games.psycashoot.Events.Event;
import rangermauve.games.psycashoot.Events.EventManager;

public class World extends EventManager {
	private int resolution = 64;
	private long lastTime = 0;
	private int tickLength = 200;
	private HashMap<String, WorldSection> sections = new HashMap<String, WorldSection>();
	private HashMap<String, Entity> entities = new HashMap<String, Entity>();

	public World() {
		this.lastTime = System.nanoTime() / 1000000;
	}
//some stuff
	public void update() {
		long delta = (System.nanoTime() / 1000000) - lastTime;
		lastTime = System.nanoTime() / 1000000;
	}

	public WorldSection getSectionAt(PVector point) {
		String key = getSectionKey(point);
		if (!sections.containsKey(key)) {
			sections.put(key, new WorldSection());
		}
		return sections.get(key);
	}

	public String getSectionKey(PVector point) {
		int x = floor(point.x) / this.resolution;
		int y = floor(point.y / this.resolution);
		return x + "," + y;
	}

	public void addEntity(String id, Entity entity) {
		entity.setId(id);
		this.entities.put(id, entity);
		this.getSectionAt(entity.getPosition()).add(entity);
	}

	public void removeEntity(Entity entity) {
		this.entities.remove(entity.getId());
		this.getSectionAt(entity.getPosition()).remove(entity);
	}
	
	public void emitGlobal(Event event){
		
	}
	public class WorldSection extends ArrayList<Entity> {

	}
}
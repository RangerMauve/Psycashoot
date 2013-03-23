package rangermauve.games.psycashoot;

import static processing.core.PApplet.floor;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PVector;
import rangermauve.games.psycashoot.events.Event;
import rangermauve.games.psycashoot.events.EventHandler;
import rangermauve.games.psycashoot.events.EventManager;
import rangermauve.games.psycashoot.events.GlobalEvent;
import rangermauve.games.psycashoot.events.TickEvent;

public class World extends EventManager implements Runnable {
	private int resolution = 64;
	private long lastTime = 0;
	private int tickLength = 200;
	private HashMap<String, WorldSection> sections = new HashMap<String, WorldSection>();
	private HashMap<String, Entity> entities = new HashMap<String, Entity>();
	private boolean isRunning = true;
	private boolean isUpdating = false;
	private Thread worldThread;

	public World() {
		this.lastTime = System.nanoTime() / 1000000;
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
		worldThread = new Thread(this);
		worldThread.setDaemon(true);
		worldThread.start();
	}
	
	public void run(){
		this.isUpdating = true;
		while(this.isRunning){
			if(this.isUpdating){
				this.update();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void update() {
		// Take care of the tick updating
		long delta = (System.nanoTime() / 1000000) - lastTime;
		lastTime = System.nanoTime() / 1000000;
		lastTime -= delta % tickLength;
		int ticks = (int)delta/tickLength;
		for(int i = ticks; i != 0; --i){
			this.emitGlobal(new TickEvent(tickLength));
		}
		
		// Update the positions and physics of all the entities
		
		// Check for collision on all entities
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
	
	public Entity getEntity(String id){
		return entities.get(id);
	}

	public ArrayList<Entity> getEntities() {
		ArrayList<Entity> res = new ArrayList<Entity>();
		res.addAll(entities.values());
		return res;
	}

	public void emitGlobal(Event event) {
		this.emit(new GlobalEvent(event));
	}

	public class WorldSection extends ArrayList<Entity> {

	}
}
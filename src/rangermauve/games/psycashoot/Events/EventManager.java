package rangermauve.games.psycashoot.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Manages events, allows you to bind to events and fire them off.
 * @author Mauve
 * 
 */
public class EventManager {
	private LinkedBlockingQueue<Event> events;
	private HashMap<String, ArrayList<EventHandler>> handlers;

	/**
	 * Returns whether the event type has been registered yet.
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @see Event
	 */
	public boolean hasRegistered(String eventName) {
		return handlers.containsKey(eventName)
				&& !handlers.get(eventName).isEmpty();
	}

	/**
	 * Returns the handlers associated with an event or null if none.
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @see Event
	 */
	public ArrayList<EventHandler> getHandlers(String eventName) {
		if (!this.hasRegistered(eventName))
			return null;
		return handlers.get(eventName);
	}

	/**
	 * Synchronously processes all events in event Queue.
	 */
	public void processEvents() {
		Event next = null;
		synchronized (events) {
			while (!events.isEmpty()) {
				next = events.poll();
				if (this.hasRegistered(next.getName())) {
					for (EventHandler h : this.getHandlers(next.getName())) {
						h.handleEvent(next);
					}
				}
			}
		}
	}

	/**
	 * Adds event to the event Queue.
	 * 
	 * @param event
	 *            The event you want to Queue
	 */
	public void emit(Event event) {
		this.events.add(event);
	}

	/**
	 * Adds handler to be called when event is called.
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @param handler
	 *            The handler that will be bound
	 */
	public void bind(String eventName, EventHandler handler) {
		if (!this.hasRegistered(eventName)) {
			this.handlers.put(eventName, new ArrayList<EventHandler>());
		}
		this.handlers.get(eventName).add(handler);
	}

	/**
	 * Adds handler to be called when event is called. Same as EventHandler.bind
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @param handler
	 *            The handler that will be bound
	 */
	public void on(String eventName, EventHandler handler) {
		this.bind(eventName, handler);
	}

	/**
	 * Removed handler from specified event if it is registered.
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @param handler
	 *            The handler that will be removed
	 */
	public void unBind(String eventName, EventHandler handler) {
		if (!this.hasRegistered(eventName))
			return;
	}

	/**
	 * Removed handler from specified event if it is registered. Same as
	 * EventHandler.unBind.
	 * 
	 * @param eventName
	 *            Name of the event that's being looked up
	 * @param handler
	 *            The handler that will be removed
	 */
	public void off(String eventName, EventHandler handler) {
		this.unBind(eventName, handler);
	}
}

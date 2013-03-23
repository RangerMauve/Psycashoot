package rangermauve.games.psycashoot.events;

public class GlobalEvent implements Event {
	private Event toShoot;

	public GlobalEvent(Event event) {
		this.toShoot = event;
	}

	public String getType() {
		return "GlobalEvent";
	}

	public Event getEvent() {
		return toShoot;
	}

}
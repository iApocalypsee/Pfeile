package player;


public class Bot extends Player {

	public Bot(String name, int spawnX, int spawnY, EntityAttributes attributes) {
		super(name, spawnX, spawnY, attributes);
	}
	
	/**
	 * Berechnet den Zug des Bots. TODO KI-Code kommt hier rein.
	 */
	public void calculateTurn() {
		move(1, 0);
		endTurn();
	}

}

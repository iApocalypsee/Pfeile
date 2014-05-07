package player;


public class Bot extends Player {

	public Bot(String name, SpawnEntityInstanceArgs instanceArgs) {
		super(name, instanceArgs);
	}
	
	/**
	 * Berechnet den Zug des Bots. TODO KI-Code kommt hier rein.
	 */
	public void calculateTurn() {
		move(1, 0);
		endTurn();
	}

}

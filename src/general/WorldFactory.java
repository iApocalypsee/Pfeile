package general;

import gui.GameScreen;

/**
 * @author Josip
 * @version 2/9/14
 */
public class WorldFactory {

    private WorldFactory() {
    }

    public static synchronized World generateDefault(int sizeX, int sizeY) {
        return new World(sizeX, sizeY);
    }

    public static synchronized World generateDefault(int sizeX, int sizeY, long seed) {
        return new World(sizeX, sizeY, seed);
    }
    
    /** Kontroliert, ob der Spieler / Bot auf jenem Feld spawnen kann */
	public static boolean isSpawnPossible(int x, int y, GameScreen s) {
		// Kann das Feld auf dem der Spieler / Bot spawnen soll betretten werden? 
		if (s.getWorld().getFieldAt(x, y).isAccessible() == true) {
			// Kann der Spieler / Bot auf minderstens einem Feld weitergehen?
			if (s.getWorld().getFieldAt(x-1, y).isAccessible() == true 
					|| s.getWorld().getFieldAt(x, y-1).isAccessible() == true 
					|| s.getWorld().getFieldAt(x+1, y).isAccessible() == true 
					|| s.getWorld().getFieldAt(x, y+1).isAccessible() == true) {
				
				// Spieler und Bot dürfen nicht auf dem selben Feld stehen
				if (s.getWorld().getActivePlayer() != null) {
					if (s.getWorld().getActivePlayer().getBoardX() == x && 
							s.getWorld().getActivePlayer().getBoardY() == y) {
						return false;
					} else 
						return true;
				} else 
					// Wenn kein Spieler spieler da ist, dann muss es auch true sein
					return true; 
			} else 
				return false; 
		} else 
			return false;
	}

}

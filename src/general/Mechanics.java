package general;

public final class Mechanics {

	/**
	 * @version 09.12.2013
	 */
	
	/* ZÃ¼ge, die man in einer Runde machen kann */
	// public static int movesPerRound = -1;
	// 
	// turnsPerRound !!!!!!
	
	
	/** CLASSIC: Spieleranzahl standardmï¿½ï¿½ig auf zwei, spï¿½ter mï¿½ssten mehr in einer Partie dazukommen */
	public static int playerNumber = 2, stdArrowNumber;
	
	/** Zeitbegrenzung */
	public static int timeBrake = -1;
	
	
	/** Maximales Leben */
	public static int lifeMax = -1;
	
	
	/** Regeneration */
	public static int lifeRegeneration = -1;
	
	/** Schaden-Muliplikator */
	public static float damageMulti = -1f;
	
	
	/** KI-Stï¿½rke */
	public static int KI = -1;
	
	/** Pfeilanzahl, die man vor jeder Runde auswählen muss */
	public static int arrowNumberPreSet = -1;
	
	/** Pfeilanzahl, die man jederzeit in seinen eigenen Zug auswählen kann
	 * und dann damit den KI angreifen muss */
	public static int arrowNumberFreeSet = -1;
	
	/** Pfeilanzahl, die noch von den frei setzbaren (in ArrowSelectionScreen) Pfeil übrig ist */
	public static int arrowNumberFreeSetUseable = -1;
	
	/** GesamtPfeilAnzahl: arrowNumberFreeSet + arrowNumberPreSet */
	public static int totalArrowNumber;
	
	/** Weltengrï¿½ï¿½e in X-Richtung fï¿½r fields: Anzahl der Felder */
	public static int worldSizeX;

	/** Weltengrï¿½ï¿½e in Y-Richtung fï¿½r fields: Anzahl der Felder */
	public static int worldSizeY;


	/** Anzahl der Zï¿½ge pro Runde */
	public static int turnsPerRound;
	
	/** Anzahl der erledigten Züge pro Runde */
	public static int currentTurn = 0;
	
	/** boolean-Wert:
	 * true:  Zug ist zuende (d.h. durch Drücken eines ZugBeenden-Buttons)
	 * false: Spieler ist noch am Zug */
	public static boolean isTurnEnd;
	
	/** Handicap-Wert des Spielers */
	public static byte HandicapPlayer = 0;
	/** Handicap-Wert des KI */
	public static byte HandicapKI = 0; 
	
	/** Handicap-Wert des Spielers 
	 * Je höher, desto höher die Unterstützung für den Spieler
	 */
	public static int handicapPlayer;
	
	/** Handicap-Wert des Computers 
	 * Je höher, desto höher die Unterstützung für den Computer
	 */
	public static int handicapKI;

	/** Rundet Auf 25 (Meter) genau */
	public static int roundTo25 (int number) {
		
		// Hier ist es immer 0; ohne Aufruf, hat lastDigits = Integer.parseInt(....) probleme
		if (number <= 9 && number >= -9) {
			return 0; 
		}
		
		// Nur mit positiven Zahlen rechnen, im nachhinein dann das Vorzeichen wieder hinzufügen (gespeichert in isPositive)
		boolean isPositive = number > 0;

		number = Math.abs(number); 
		
		// Letzten beiden Ziffern 
		int lastDigits = Integer.parseInt(Character.toString(String.valueOf(number).charAt(String.valueOf(number).length() - 1))) + 
				Integer.parseInt(Character.toString(String.valueOf(number).charAt(String.valueOf(number).length() - 2))) * 10; 
		
		// Letze beiden Stellen wegscheiden 
		number = number / 100; 
		number = number * 100; 
		
		// Hier werden die neuen Stellen wieder hinzugefügt
		if (lastDigits >= 0 && lastDigits <= 12) {
			// number = number + 0; 
		} else if (lastDigits >= 13 && lastDigits <= 37) 
			number = number + 25;
		else if (lastDigits >= 38 && lastDigits <= 62) 
			number = number + 50; 
		else if (lastDigits >= 63 && lastDigits <= 87) 
			number = number + 75; 
		else if (lastDigits >= 88 && lastDigits <= 99)
			number = number + 100; 
		
		// Vorzeichen hinzufügen und zurückgeben
		return isPositive ? number : -number;
	}
}

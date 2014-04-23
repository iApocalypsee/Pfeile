package gui;

import general.Mechanics;
import player.AttackQueue;
import player.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @version 09.12.2013
 * @deprecated {@link general.Field} und dessen Unterklassen verwenden. Allerdings müssen noch
 * einige Methoden von dieser Field in die neue Field verschoben werden.
 */
@Deprecated
public class Field implements Drawable {

	/** Indizes für Terrain und Art */
	private int fieldType;

	/**
	 * aktuelle Position des Feldes im Gitternetz (posX, posY) von 'WorldSize'
	 * {entspricht Quadrant I im Kordinatensystem}: PositionX
	 */
	private int posX;
	/**
	 * aktuelle Position des Feldes im Gitternetz (posX, posY) von 'WorldSize'
	 * {entspricht Quadrant I im Kordinatensystem}: PositionY
	 */
	private int posY;

	/** Position X (f_PosX, f_PosY) auf Bildschirm für GUI */
	private int posXscreen;
	/** Position Y auf dem Bildschirm für GUI */
	private int posYscreen;

	/**
	 * Randeinschub in x Richtung für GUI.
	 */
	public static final int insetX = 50;

	/**
	 * Randeinschub in y Richtung für GUI.
	 */
	public static final int insetY = 25;

	/** Feldnummer zur Nummerierung von 0 - Ende */
	private int fieldNr;

	/** BoundingBox zur leichteren Handhabung mit dem Field */
	private Rectangle boundingField;

	/**
	 * Name des Feldtyps
	 */
	private String fieldType_name;

	/**
	 * Ist das Feld zugänglich?
	 */
	private boolean isAccessable;

	/**
	 * liegt das Feld im Blickradius einer Einheit / eines Spielers?
	 * 
	 * Arraywert: 0 --> Spieler; 1 --> KI;
	 * 
	 */
	private boolean[] isVisible = new boolean[2];

	/**
	 * Ist Spieler ist auf dem Feld?
	 */
	private boolean playerOn = false;
	
	/**
	 * Die Liste aller auf dem Feld stehender lebender "Figuren".
	 */
	private List<Entity> standingEntities;

	/** Zufallszahlgenerator */
	private Random random = new Random();

	/**
	 * Die ArrowQueue für das Feld.
	 */
	private List<AttackQueue> queue;

	/** Welche Felder wurden bereits erzeugt */
	public static List<Field> fieldsList;

	/**
	 * Array für alle Fieldtexturen: der Wert des Arrays entspricht den
	 * FieldType-Wert; Index: 10 ist das Bild, bei dem auf die Textur nicht
	 * geladen werden konnte.
	 */
	private static BufferedImage[] imges_Fields = new BufferedImage[12];

	/** Grasland */
	public static final int INDEX_IMAGE_GRASS = 0;

	/** Wald */
	public static final int INDEX_IMAGE_FOREST = 1;

	/** Flachland / Weideland */
	public static final int INDEX_IMAGE_PLAINS = 2;

	/** Hochland / Hügelebene */
	public static final int INDEX_IMAGE_HIGHLANDS = 3;

	/** Gebirge */
	public static final int INDEX_IMAGE_MOUNTAINS = 4;

	/** Wüste */
	public static final int INDEX_IMAGE_DESERT = 5;

	/** Eiswüste */
	public static final int INDEX_IMAGE_ICEDESERT = 6;

	/** See */
	public static final int INDEX_IMAGE_SEA = 7;

	/** Urwald / Dschungel */
	public static final int INDEX_IMAGE_JUNGLE = 8;

	/** Ödland / Ruinen */
	public static final int INDEX_IMAGE_WASTELAND = 9;

	/** Bild-Konnte-Nicht-Geladen-Werden Bild */
	public static final int INDEX_IMAGE_TEXTURENOTFOUND = 10;

	/** Das Feld kann nicht eingesehen werden (in Nebel des Krieges verschluckt) */
	public static final int INDEX_IMAGE_NOTVISIBLEFIELD = 11;
	
	private static final Color selectionColor = new Color(1.0f, 1.0f, 1.0f, 0.2f);

	/** Im Static-Block werden die Bilder geladen */
	static {
		try {

			for (int i = 0; i < imges_Fields.length; i++) {
				if (imges_Fields[i] == null) {
					if (i == INDEX_IMAGE_DESERT)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/desert_Field.png"));
					else if (i == INDEX_IMAGE_FOREST)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/forest_Field.png"));
					else if (i == INDEX_IMAGE_GRASS)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/grass_Field.png"));
					else if (i == INDEX_IMAGE_HIGHLANDS)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/highlands_Field.png"));
					else if (i == INDEX_IMAGE_ICEDESERT)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/iceDesert_Field.png"));
					else if (i == INDEX_IMAGE_JUNGLE)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/jungle_Field.png"));
					else if (i == INDEX_IMAGE_MOUNTAINS)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/mountains_Field.png"));
					else if (i == INDEX_IMAGE_NOTVISIBLEFIELD)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/notVisible_Field.png"));
					else if (i == INDEX_IMAGE_PLAINS)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/plains_Field.png"));
					else if (i == INDEX_IMAGE_SEA)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/sea_Field.png"));
					else if (i == INDEX_IMAGE_WASTELAND)
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/wasteland_Field.png"));
					else
						imges_Fields[i] = ImageIO
								.read(Field.class
										.getClassLoader()
										.getResourceAsStream(
												"resources/gfx/field textures/textureNotFound_Field.png"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			// stattdessen ein anderes Bild verwenden
		}
	}

	/** KONSTRUKTOR DER KLASSE 'Field' */
	public Field() {

		this.createFieldNumber();
		this.createFieldPosition();
		this.createFieldType();
		this.correctFieldType();

		setQueue(new LinkedList<AttackQueue>());
		standingEntities = new LinkedList<Entity>();

	}

	/** muss Spieler noch Field zugwiesen bekommen? 
	 *  */
	public static void shouldSpawnPlayer() {
		// Anzahl der vorher festgelegten Spieler 
		byte currentAmountOfPlayerSpawn = 0;

        for (Field aFieldsList : fieldsList) {
            if (aFieldsList.isPlayerOn() == true)
                currentAmountOfPlayerSpawn++;
        }
		
		// doField ist das Feld das bearbeitet wird
		Field doField;
		
		Random r = new Random ();
		
		while (true) {
			int i = r.nextInt(fieldsList.size());
			

			// bei zwei (Player + KI) verlassen
			if (currentAmountOfPlayerSpawn == GameScreen.getInstance().getWorld().getPlayerCount()) {
				return;
			}
			
			// doField = nächstes Feld aus der Field-Liste
			doField = fieldsList.get(i);
			
			// Wenn das Feld unzugänglich ist, verlassen 
			if (!doField.isAccessable()) {
				continue;
			}

			// Anzahl der bereits gespawnten Player
			if (doField.playerOn) {
				// wenn auf dem Feld bereits ein Spieler ist: neuer Versuch mit nächstem Feld
				continue;
			}
			
			// dieses Feld nicht am Rand
			if (doField.getPosX() - Mechanics.worldSizeX != 0
					&& doField.getPosY() - Mechanics.worldSizeY != 0) {
				
				// wenn einer der Felder null ist, aufhören (null.playerOn ist nicht möglich)
				if (Field.getFieldEast(doField) != null && 
						Field.getFieldNorth(doField) != null && 
						Field.getFieldSouth(doField) != null &&
						Field.getFieldWest(doField) != null) {
					
					/* wenn auf dem eigenen und angrenzenden Feldern kein Spieler
					 * bereits steht und das Feld zugänglich (isAccesable = true)
					 * wird weitergeleitet */
					if (Field.getFieldEast(doField).playerOn == false
							&& Field.getFieldWest(doField).playerOn == false
							&& Field.getFieldNorth(doField).playerOn == false
							&& Field.getFieldSouth(doField).playerOn == false) {
						
						// wenn minderstens einer der angrenzenden Felder betrettbar (isAccessble == true) ist
						// dann wird die Field.spawnPlayer(...) aufgerufen
						if (Field.getFieldEast(doField).isAccessable() == true
							|| Field.getFieldWest(doField).isAccessable() == true
							|| Field.getFieldNorth(doField).isAccessable()
							|| Field.getFieldSouth(doField).isAccessable() == true) 
						{
							Field.spawnPlayer(doField.getFieldNr(), currentAmountOfPlayerSpawn);
							currentAmountOfPlayerSpawn++;
						}
					}
				}
			}
		}
	}

	/**
	 * Setzt den Spieler auf das Feld.
	 * 
	 * @param FieldIndex (= FieldNr)
	 */
	public static void spawnPlayer(int FieldIndex, int currentAmountOfPlayerSpawn) {
		GameScreen.getInstance().getWorld().getPlayerByIndex(currentAmountOfPlayerSpawn).teleport(Field.getFieldAtFieldNr(FieldIndex).posX, (Field.getFieldAtFieldNr(FieldIndex).posY));
		Field.getFieldAtFieldNr(FieldIndex).playerOn = true;
	}

	/**
	 * Feldnummergenerierung: setzt die Feldnummer 'fieldNr' (vom Typ int)
	 * richtig/weist sie dem Feld zu
	 */
	private void createFieldNumber() {
		setFieldNr(fieldsList.size());
	}

	/**
	 * Postion des Feldes im Gitternetz (posX, posY) von WorldSize FIXME
	 * [aktuell entsteht allerdings kein Quadrat sonderen ein Rechteck; -->
	 * abweichung von Mechanics.worldSize-Werten ==> ggf. ändern]
	 */
	private void createFieldPosition() {

		// wenn noch kein Feld zugeweisen wurde
		if (this.fieldNr == 0) {
			this.posX = 1;
			this.posY = 1;

			// sonst das hier
		} else {
			// letztes Feld wird in 'lastField' gespeichert
			Field lastField = fieldsList.get(fieldsList.size() - 1);

			// wenn eine neue y-Zeile angefangen werden muss
			if (lastField.posX % (Mechanics.worldSizeX + 1) == 0) {
				this.posX = 1;
				this.posY = lastField.posY + 1;

				// wenn nicht, dann hier
			} else {

				for (int i = 1; i <= Mechanics.worldSizeY; i++) {
					if (Mechanics.worldSizeX * i > fieldNr) {
						this.posX = lastField.posX + 1;
						this.posY = lastField.posY;
					} else if (Mechanics.worldSizeX * i < fieldNr) {
						if (fieldNr % Mechanics.worldSizeX == 0) {
							this.posX = 1;
							this.posY = lastField.posY + 1;
						} else {
							if (Mechanics.worldSizeX * i > fieldNr) {
								this.posX = lastField.posX + 1;
								this.posY = lastField.posY;
							}
						}
					}
				}
			}
		}
	}

	/** Terrai-Typ-Generierung */
	private void createFieldType() {
		// TODO ggf.: fieldType_name = "Steppe"
		fieldType = random.nextInt(100) + 1; // von 1 - 100 ==> 1% entspricht
												// genau einer Zahl

		if (fieldType >= 1 && fieldType <= 12) { // 12%
			fieldType_name = "Grasland";
			fieldType = Field.INDEX_IMAGE_GRASS;
			isAccessable = true;
		} else if (fieldType >= 13 && fieldType <= 27) { // 15%
			fieldType_name = "Wald";
			fieldType = Field.INDEX_IMAGE_FOREST;
			isAccessable = true;
		} else if (fieldType >= 28 && fieldType <= 39) { // 12%
			fieldType_name = "Flachland / Weideland"; // Weideland
			fieldType = Field.INDEX_IMAGE_PLAINS;
			isAccessable = true;
		} else if (fieldType >= 40 && fieldType <= 50) { // 11%
			fieldType_name = "Hügelebene / Hochland"; // h�gliges Ackerland;
														// Voralpenland
			fieldType = Field.INDEX_IMAGE_HIGHLANDS;
			isAccessable = true;
		} else if (fieldType >= 51 && fieldType <= 60) { // 10%
			fieldType_name = "Gebirge";
			fieldType = Field.INDEX_IMAGE_MOUNTAINS;
			isAccessable = false;
		} else if (fieldType >= 61 && fieldType <= 72) { // 12%
			fieldType_name = "Wüste";
			fieldType = Field.INDEX_IMAGE_DESERT;
			isAccessable = false;
		} else if (fieldType >= 73 && fieldType <= 84) { // 12%
			fieldType_name = "Eiswüste";
			fieldType = Field.INDEX_IMAGE_ICEDESERT;
			isAccessable = false;
		} else if (fieldType >= 85 && fieldType <= 89) { // 5%
			fieldType_name = "See";
			fieldType = Field.INDEX_IMAGE_SEA;
			isAccessable = false;
		} else if (fieldType >= 90 && fieldType <= 97) { // 8%
			fieldType_name = "Urwalt / Dschungel";
			fieldType = Field.INDEX_IMAGE_JUNGLE;
			isAccessable = false;
		} else if (fieldType >= 98 && fieldType <= 100) { // 3%
			fieldType_name = "Ödland / Ruinenstadt";
			fieldType = Field.INDEX_IMAGE_WASTELAND;
			isAccessable = true;
		} else {
			createFieldType();
		}
	}

	/**
	 * Kontrolliert, dass 'fieldTyp' richtig initialisiert wurde und korrigiert
	 * ggf.
	 */
	private void correctFieldType() {

		// wenn es bereits felder instanziert wurden:
		if (fieldsList.size() != 0) {

			// das zu korriegerende Feld wird auf 'doField' übertragen;
			Field doField = getFieldSouth(this);
			testCorrectFieldType(doField);

			doField = getFieldNorth(this);
			testCorrectFieldType(doField);

			doField = getFieldWest(this);
			testCorrectFieldType(doField);

			doField = getFieldEast(this);
			testCorrectFieldType(doField);

		}
	}

	/** Testet, ob der Feldtyp richtig zu gewiesen wurde */
	private void testCorrectFieldType(Field doField) {

		// wenn das Feld = null ist, verlassen
		if (doField != null) {
			// wenn es die beiden vom gleichen Typ sind: sofort verlassen
			if (doField.getFieldType() != fieldType) {

				if ((doField.getFieldType() == 9 && fieldType == 8)
						|| (doField.getFieldType() == 8 && fieldType == 9)) {
					createFieldType(); // Dschungel und Ödland
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 9 && fieldType == 7)
						|| (doField.getFieldType() == 7 && fieldType == 9)) {
					createFieldType(); // See und Ödland
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 8 && fieldType == 6)
						|| (doField.getFieldType() == 6 && fieldType == 8)) {
					createFieldType(); // Dschungel und Eiswüste
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 8 && fieldType == 5)
						|| (doField.getFieldType() == 5 && fieldType == 8)) {
					createFieldType(); // Dschungel und Wüste
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 7 && fieldType == 5)
						|| (doField.getFieldType() == 5 && fieldType == 7)) {
					createFieldType(); // See und Wüste
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 6 && fieldType == 5)
						|| (doField.getFieldType() == 5 && fieldType == 6)) {
					createFieldType(); // Wüste und Eiswüste
					correctFieldType();
					return;
				}
				if ((doField.getFieldType() == 5 && fieldType == 1)
						|| (doField.getFieldType() == 1 && fieldType == 5)) {
					createFieldType(); // Wüste und Wald
					correctFieldType();
					return;
				}
			}
		}
	}
	/** erstellt Werte für GUI-Darstellung: Position 
	 * einmaliger Aufruf für alle Fields*/
	public static void createGUIPosition() {
		// die Positionen der Felder am Bildschirm werden gesetzt
		for (int i = 0; i < fieldsList.size(); i++) {
			fieldsList.get(i).setPosXscreen((Math.round(
					Field.insetX + fieldsList.get(i).posX * Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND).getWidth() * Mechanics.widthStretching))); 
			fieldsList.get(i).setPosYscreen((Math.round(
					Field.insetY + fieldsList.get(i).posY * Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND).getHeight() * Mechanics.heightStretching))); 
		}
		
		// Die Bounding-Box wird geladen
		for (int i = 0; i < fieldsList.size(); i++) {
			fieldsList.get(i).boundingField = new Rectangle(
					fieldsList.get(i).posXscreen, fieldsList.get(i).posYscreen, 
					Math.round(Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND).getWidth() * Mechanics.widthStretching), 
					Math.round(Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND).getHeight() * Mechanics.heightStretching));
		}
	}

	/**
	 * Zeichenmethode (von 'World' aufgerufen): zeichnet das Feld, das diese
	 * Methode aufruft
	 */
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(
				Field.getImage(this.getFieldType()),
				posXscreen,
				posYscreen,
				Math.round(Field.getImage(this.getFieldType()).getWidth()
						* Mechanics.widthStretching),
				Math.round(Field.getImage(this.getFieldType()).getHeight()
						* Mechanics.heightStretching), null);

		if (this.getBoundingField().contains(Screen.getMousePosition())) {
			if(GameScreen.getInstance().getWorld().getActivePlayer().isAttemptingShoot()) {
				
				if(Screen.isLeftMousePressed()) {
					
				} else {
					
				}
				
				if(Screen.isRightMousePressed()) {
					
				} else {
					
				}
				
			} else {
				if(Screen.isLeftMousePressed()) {

					// mach da nichts, passt schon so und sieht gut aus

				} else {
					g.setColor(selectionColor);
					g.fillRect(
							getBoundingField().x,
							getBoundingField().y,
							Math.round((Field.getImage(this.getFieldType()).getWidth() * Mechanics.widthStretching)),
							Math.round(Field.getImage(this.getFieldType()).getHeight()
									* Mechanics.heightStretching));
				}

				if(Screen.isRightMousePressed()) {
					if(GameScreen.getInstance().getWorld().getActivePlayer() == GameScreen.getInstance().getWorld().getTurnPlayer()) {
						if(!(GameScreen.getInstance().getWorld().getActivePlayer().getBoardPosition().x == this.posX && GameScreen.getInstance().getWorld().getActivePlayer().getBoardPosition().y == this.posY)) {
							GameScreen.getInstance().getWorld().getTurnPlayer().move(this.posX - GameScreen.getInstance().getWorld().getTurnPlayer().getBoardPosition().x,
									this.posY - GameScreen.getInstance().getWorld().getTurnPlayer().getBoardPosition().y);
						}
					}
				}
			}
		}
	}

	/**
	 * Erzeugt das FieldArray [int posX] [int posY] aus der List 'fieldsList' ;
	 * Exeption, wenn zu viele / zu wenige Felder (als 'Mechanics.GameScreen.getWorld()SizeX *
	 * Mechanics.WorldSizeY')
	 */
	public static void createArrayFromList() throws Exception {
		if (fieldsList.size() > Mechanics.worldSizeX * Mechanics.worldSizeY - 1) {
			throw new IndexOutOfBoundsException(
					"Zu viele Felder wurden initiliesiert! "
							+ "Die Liste 'fieldsList' ist länger (größer als 'Mechanics.WorldSizeX * Mechanics.WorldSizeY') als das Arrey 'fields' es zulässt.");
		} else if (fieldsList.size() <= 0) {
			throw new java.lang.ArrayStoreException(
					"Es wurden KEINE FELDER initiliesiert! "
							+ "Die Liste 'fieldsList' ist leer und wurde nicht oder inkorrekt initialisert.");
		} else {
            /* DIESER FALL TRITT 100-PROZENTIG NICHT EIN
			if(GameScreen.getWorld().get == null) {
				GameScreen.getWorld().setFields(new Field[Mechanics.worldSizeX - 1][Mechanics.worldSizeY - 1]);
			}

			for (int i = 0; i < fieldsList.size(); i++) {
				GameScreen.getWorld().getFields()[
				                  fieldsList.
				                  get(i).
				                  getPosX() - 1]
				                		  [fieldsList.get(i)
						.getPosY() - 1] 
								= fieldsList.get(i);
				
			}
            */
		}
		

	}

	// --------------------------------------------------------------------------------
	// --------------------- GETTER und SETTER
	// ----------------------------------------
	// --------------------------------------------------------------------------------

	/**
	 * liefert das Feld an einer speziellen Position (PosX, PosY) im Gitternetz
	 * der Felder zur�ck
	 * 
	 * @param PosX
	 *            - Position des Feldes im Gitternetz der (PosX, PosY) Werte:
	 *            X-Koordinate
	 * @param PosY
	 *            - Position des Feldes im Gitternetz der (PosX, PosY) Werte:
	 *            Y-Koordinate
	 * @return Field and der Stelle (PosX, PosY) - wenn kein Feld �bereinstimmt
	 *         wird null �bergeben
	 */
	public static Field getFieldAtPosXPosY(int PosX, int PosY) {
		for (int i = 0; i < fieldsList.size(); i++) {
			if (fieldsList.get(i).getPosX() == PosX
					&& fieldsList.get(i).getPosY() == PosY)
				return fieldsList.get(i);
		}
		return null;
	}

	/**
	 * liefert das Feld mit der Feldnunner zurück
	 * 
	 * @param fieldNr
	 *            - Feldnummer des Feldes, dass zurückgegeben werden soll
	 * @return Field mit der Feldnummer - wenn kein Feld übereinstimmt wird null
	 *         übergeben
	 */
	public static Field getFieldAtFieldNr(int fieldNr) {
		for (int i = 0; i < fieldsList.size(); i++) {
			if (fieldsList.get(i).getFieldNr() == fieldNr)
				return fieldsList.get(i);
		}
		return null;
	}

	/**
	 * Getter f�r das s�dliche Feld vom aktuellen Feld aus betrachtet Im
	 * Kordinatensystem (Quadrant I) ein y-Wert niedriger (==> s�d-west = 1 | 1)
	 */
	public static Field getFieldSouth(Field currentField) {
		Field thisField = null;

		for (int i = 0; i < fieldsList.size(); i++) {
			thisField = fieldsList.get(i);

			if (thisField.getPosX() == currentField.getPosX()
					&& thisField.getPosY() == currentField.getPosY() - 1) {
				return thisField;
			}
		}
		return null;
	}

	/** Getter f�r das n�rdliche Feld vom aktuellen Feld aus betrachtet (y+1) */
	public static Field getFieldNorth(Field currentField) {
		Field thisField = null;

		for (int i = 0; i < fieldsList.size(); i++) {
			thisField = fieldsList.get(i);

			if (thisField.getPosX() == currentField.getPosX()
					&& thisField.getPosY() == currentField.getPosY() + 1) {
				return thisField;
			}
		}
		return null;
	}

	/**
	 * Getter f�r das westliche / linke Feld vom aktuellen Feld aus betrachtet
	 * (x-1)
	 */
	public static Field getFieldWest(Field currentField) {
		Field thisField = null;

		for (int i = 0; i < fieldsList.size(); i++) {
			thisField = fieldsList.get(i);

			if (thisField.getPosX() == currentField.getPosX() - 1
					&& thisField.getPosY() == currentField.getPosY()) {
				return thisField;
			}
		}

		return thisField;
	}

	/**
	 * Getter f�r das �stliche / rechte Feld vom aktuellen Feld aus betrachtet
	 * (x+1); Kein Feld: null
	 */
	public static Field getFieldEast(Field currentField) {
		Field thisField = null;

		for (int i = 0; i < fieldsList.size(); i++) {
			thisField = fieldsList.get(i);

			if (thisField.getPosX() == currentField.getPosX() + 1
					&& thisField.getPosY() == currentField.getPosY()) {
				return thisField;
			}
		}
		return null;
	}

	/**
	 * updated die neue GUI-Position, wenn an 'Mechanics.heightStreching' oder
	 * 'Mechanics.widthStreching' etwas ge�ndert wurde
	 */
	public static void updateGUIposition() {
		// die Positionen der Felder am Bildschirm werden gesetzt
		for (int i = 0; i < fieldsList.size(); i++) {
			fieldsList.get(i).setPosXscreen(
					(Math.round(Field.insetX
							+ fieldsList.get(i).posX
							* Field.getImage(Field.INDEX_IMAGE_TEXTURENOTFOUND)
									.getWidth() * Mechanics.widthStretching)));
			fieldsList.get(i)
					.setPosYscreen(
							(Math.round(Field.insetY
									+ fieldsList.get(i).posY
									* Field.getImage(
											Field.INDEX_IMAGE_TEXTURENOTFOUND)
											.getHeight()
									* Mechanics.heightStretching)));
		}
		// Die Bounding-Box wird ge�ndert
		for (int i = 0; i < fieldsList.size(); i++) {
			fieldsList.get(i).boundingField.x = fieldsList.get(i).posXscreen;
			fieldsList.get(i).boundingField.y = fieldsList.get(i).posYscreen;
			fieldsList.get(i).boundingField.width = Math.round(Field.getImage(
					Field.INDEX_IMAGE_TEXTURENOTFOUND).getWidth()
					* Mechanics.widthStretching);
			fieldsList.get(i).boundingField.height = Math.round(Field.getImage(
					Field.INDEX_IMAGE_TEXTURENOTFOUND).getHeight()
					* Mechanics.heightStretching);
		}

		System.err.println("  _updateGUIposition_  ");
	}

	/**
	 * @return Die x Position des Feldes (im Feldergitternetz).
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * @return Die y Position des Feldes (im Feldergitternetz).
	 */
	public int getPosY() {
		return posY;
	}

	/** giebt die Feldnummer aus */
	public int getFieldNr() {
		return fieldNr;
	}

	/**
	 * setzt die Feldnummer; nur wenn unbedingt n�tig verwenden, sonst nicht, da
	 * es die Reinfolge durcheinander bringen kann
	 */
	private void setFieldNr(int fieldNr) {
		this.fieldNr = fieldNr;
	}

	/**
	 * Setter, ob auf das Feld eingesehen werden kann. Wenn das Feld schon
	 * einmal eingesehen wurde, kann der Wert nicht mehr durch den Setter selbst
	 * ge�ndert werden. Gesichtete Felder werden nicht wieder irgendwann vom
	 * Nebel des Krieges "verschluckt".
	 * 
	 * @param isVisible
	 *            Der neue Sichtbarkeitswert des Felds
	 */
	public void setVisible(boolean isVisible) {
		// if (this.isVisible[player.Player.getCurrentTurn()] != true) {
		// this.isVisible[player.Player.getCurrentTurn()] = isVisible;
		// } else
		// return;
	}

	/** Getter f�r den Namen / Typ des Feldes */
	public String getFieldType_Name() {
		return fieldType_name;
	}

	/** Setter f�r den Namen / Typ des Feldes */
	public void setFieldType_Name(String fieldType_name) {
		this.fieldType_name = fieldType_name;
	}

	/**
	 * Getter f�r den NamenNummer / Typ des Feldes in Zahl (bei Methode
	 * 'createFieldType' nachschauen)
	 */
	public int getFieldType() {
		return fieldType;
	}

	/** Setter f�r die Nummer des Feldtyps */
	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * Gibt das Bild zur�ck f�r den jeweiligen Index des Feldes Bei unm�glichen
	 * Index wird 'img_TextureNotFound' geladen
	 */
	public static BufferedImage getImage(int indexOfFieldType) {

		if (indexOfFieldType >= 0 && indexOfFieldType <= imges_Fields.length)
			return imges_Fields[indexOfFieldType];
		else
			return imges_Fields[Field.INDEX_IMAGE_TEXTURENOTFOUND];
	}

	/** Gibt das Bild der Aufrufenden Inzanz der Methode zur�ck */
	public BufferedImage getImage() {
		return imges_Fields[this.getFieldType()];
	}

	/**
	 * gibt die X-Positon (im Feldergitternetz) zur�ck
	 * 
	 * @param fieldNr
	 * @return Position Y (in Feldkorrdinaten)
	 */
	public static int getPosXFromFieldNr(int fieldNr) {

		for (int i = 0; i < fieldsList.size(); i++) {
			if (Field.fieldsList.get(i).getFieldNr() == fieldNr) {
				return fieldsList.get(i).getPosX();
			}
		}
		return (Integer) null;
	}

	/**
	 * gibt die Y-Positon (im Feldergitternetz) zur�ck
	 * 
	 * @param fieldNr
	 * @return Position Y (in Feldkorrdinaten)
	 */
	public static int getPosYFromFieldNr(int fieldNr) {

		for (int i = 0; i < fieldsList.size(); i++) {
			if (Field.fieldsList.get(i).getFieldNr() == fieldNr) {
				return fieldsList.get(i).getPosY();
			}
		}
		return (Integer) null;
	}

	/** Befindet sich ein Spieler auf dem Feld */
	public boolean isPlayerOn() {
		return playerOn;
	}

	/** Setzt ob sich ein Spieler auf dem Feld befindet */
	public void setPlayerOn(boolean playerOn) {
		this.playerOn = playerOn;
	}

	/** Rechteck um das Feld: --> Kolisionserkennung */
	public Rectangle getBoundingField() {
		return boundingField;
	}

	/** Kann das Feld Betreten werden */
	public boolean isAccessable() {
		return isAccessable;
	}

	/** das Feld f�r den Spieler 'int Spielernummer' einsehbar */
	public boolean getVisible(int playerIndex) {
		if (playerIndex >= 0 && playerIndex <= isVisible.length)
			return isVisible[playerIndex];
		else
			return false;
	}

	/** Gibt die X-Position auf den Bildschirm zur�ck */
	public int getPosXscreen() {
		return posXscreen;
	}

	/** Gibt die Y-Position auf dem Bildschirm zur�ck */
	public int getPosYscreen() {
		return posYscreen;
	}

	/** Setzt die X-Positon auf dem Bildschrim (--> GUI) */
	public void setPosXscreen(int posXscreen) {
		this.posXscreen = posXscreen;
	}

	/** Setzt die Y-Positon auf dem Bildschrim (--> GUI) */
	public void setPosYscreen(int posYscreen) {
		this.posYscreen = posYscreen;
	}

	/**
	 * Gibt die Distanz zwischen den beiden Feldern (diorganal) zurück TODO auf
	 * 25m rechnen (aktuell: Metergenau)
	 */
	public int getDistance(Field fieldBeginn, Field FieldEnd) {

		int distanceXdirection = 0;
		int distanceYdirection = 0;

		distanceXdirection = FieldEnd.getPosX() - fieldBeginn.getPosX();
		distanceYdirection = FieldEnd.getPosY() - fieldBeginn.getPosY();
		int distance = (int) Math
				.round(Math.sqrt(distanceYdirection * distanceXdirection
						+ distanceYdirection * distanceYdirection));

		return distance;
	}

	/**
	 * Gibt die X-Distance von den Feldern zur�ck (welches Feld Start / Ziel
	 * ist, ist egal: gr��ere XPosition - kleinere XPosition) (Die Eingabewerte
	 * sind 'int posXStart': X-Position des Einen Feldes; 'int posYAim':
	 * X-Position des Anderen Feldes
	 */
	public int getDistanceX(int posXStart, int posXAim) {
		if (posXStart > posXAim)
			return posXStart - posXAim;
		else if (posXAim > posXStart)
			return posXAim - posXStart;
		else
			return 0;
	}

	/**
	 * Gibt die Y-Distance von den Feldern zurück (welches Feld Start / Ziel
	 * ist, ist egal: größere YPosition - kleinere YPosition) (Die Eingabewerte
	 * sind 'int posXStart': Y-Position des Einen Feldes; 'int posYAim':
	 * Y-Position des Anderen Feldes
	 */
	public int getDistanceY(int posYStart, int posYAim) {
		if (posYStart > posYAim)
			return Math.abs(posYStart - posYAim);
		else if (posYAim > posYStart)
			return Math.abs(posYAim - posYStart);
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return fieldType_name;
	}

	/**
	 * @return the queue
	 */
	public List<AttackQueue> getQueue() {
		return queue;
	}

	/**
	 * @param queue
	 *            the queue to set
	 */
	private void setQueue(List<AttackQueue> queue) {
		this.queue = queue;


	}

	public List<Entity> getStandingEntities() {
		return standingEntities;
	}
	
}

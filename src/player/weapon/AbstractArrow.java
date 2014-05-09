package player.weapon;

import general.Mechanics;
import gui.GameScreen;

/**
 * Die abstrakte Pfeil-Klasse, von der die Pfeilarten abgeleitet werden. Wenn
 * Spieler Pfeile bekommen, bekommen sie Pfeil-Instanzen durch den Konstruktor
 * der Subklasse. Diese Objekte kommen in das Inventar des Spielers.
 * 
 * <b> Indexes: </b> Feuer = 0; Wasser = 1; Sturm = 2; Stein = 3; Eis = 4; Blitz
 * = 5; Licht = 6; Schatten = 7;
 * 
 * @version 25.11.2013
 */
@SuppressWarnings("unused")
public abstract class AbstractArrow extends RangedWeapon {

	// TODO
	// alle Werte die auf 25m gerechnet werden mÃ¼ssen, mÃ¼ssen noch von 100m auf
	// 25m gerechnet werden
	// alle

	/**
	 * Wert des Schadens (nach Abnahme durch Entfernung)
	 */
	protected float attackValueCurrent;

	/**
	 * Wert in 'float', wie stark der Pfeil an Schaden verliert : <b> jeweils
	 * nach 25m <b>
	 * 
	 * Prozentsatz (0.1f = 10%), was dem Pfeil an Schaden abgezogen wird zÃ¤hlt
	 * erst nach 25m Entfernung
	 * */
	protected float damageLosingRate;

	/** Wahrscheinlichkeit mit der Man sich selbst trifft */
	protected float selfHittingRate;

	/**
	 * Treffunsicherheitsquote (Grundwert) Je hï¿½her, desto unwahrscheinlicher
	 * ein Treffer Je weiter die Distance, desto unwahrscheinlicher ein Treffer
	 */
	protected float aimMissing;

	/** Treffunsicherheitsquote: Faktor der Erhï¿½hung (pro 25m) */
	protected float aimMissingRate;

	/** aktuelle Treffunsicherheitsquote */
	protected float aimMissingCurrent;

	/** Grundreichweite des Pfeils : in Metern angegeben [25m genau] */
	protected int rangeValue;

	/**
	 * Reichweite des Pfeils : in Metern angegeben [25m genau] Verwenden, falls
	 * andere Pfeile [d.h. Sturmpfeil] oder Felder / andere Eigenschaften die
	 * Reichweite verï¿½ndern
	 */
	protected int rangeValueCurrent;

	/**
	 * Wie weit der Pfeil bisher geflogen ist : in Metern angegeben Feld: [auf
	 * 25m genau] Laenge = 100 Breite = 100
	 */
	protected int distanceReached;

	// /** Wie Schnel sich der Pfeil bewegt */
	// protected int arrowSpeed;
	//
	// /** Wie groß seine Beschleunigung ist */
	// protected int acceleration;
	//
	// /** Wie weit der Schaden des Pfeils nach dem Auftreffen reicht */
	// protected int damageRadius;

	/** Positon im Koordinatensystem des Pfeils: Die X-Position */
	protected int fieldX;
	/** Position im Koordinatensystem des Pfeils: Y-Position */
	protected int fieldY;
	/** Nummer des Feldes Ã¼ber das sich der Pfeil befindet */
	protected int fieldNr;
	/** X-Position des Pfeils - für GUI */
	protected int posX;
	/** Y-Position des Pfeils - für GUI */
	protected int posY;
	/**
	 * X-Positon im Koordinatensystem der Felder: Hier ist die Position X des
	 * Zielfeldes
	 */
	protected int fieldXAim;
	/**
	 * Y-Positon im Koordinatensystem der Felder: Hier ist die Position Y des
	 * Zielfeldes
	 */
	protected int fieldYAim;

	/**
	 * <b> KONSTUCKTOR: <b> float : Grundschaden des Pfeils float :
	 * Grundverteidigung des Pfeils int : Grundreichweite des Pfeils float :
	 * Selbsttrefferwahrscheinlichkeit float : Treffunsicherheitsquote float :
	 * Faktor der Erhï¿½hung der Treffunsicherheitsquote float : Faktor der
	 * Verringerung der Schadenswirkung
	 * 
	 * // int : arrowSpeed - Geschwindigkeit des Pfeils // int : acceleration -
	 * Beschleunigung des Pfeils // int : damageRadius - Schadensradius des
	 * Pfeils
	 */
	public AbstractArrow(float attackVal, float defenseVal, int rangeVal,
			float selfHittingRate, float aimMissing, float aimMissingRate,
			float damageLosingRate, String name) {
		super(name);
		this.setAttackValue(attackVal);
		this.setAttackValCurrent(attackVal);
		this.setDefenseValue(defenseVal);
		// Reichweite des Pfeils wird minimal (+/- 1 Feld) an die Entfernung
		// angepasst
		if (Mechanics.worldSizeX <= 7) {
			this.setRangeValue(rangeVal - 100);
			this.setRangeValueCurrent(rangeVal - 100);
		} else if (Mechanics.worldSizeX > 7 && Mechanics.worldSizeX <= 17) {
			this.setRangeValue(rangeVal);
			this.setRangeValueCurrent(rangeVal);
		} else {
			this.setRangeValue(rangeVal + 100);
			this.setRangeValueCurrent(rangeVal + 100);
		}
		this.setSelfHittingRate(selfHittingRate);
		this.setAimMissing(aimMissing);
		this.setAimMissingRate(aimMissingRate);
		this.setAimMissingCurrent(aimMissing);
		this.setDamageLosingRate(damageLosingRate);
		this.setDistanceReached(0);
		this.setFieldX(GameScreen.getInstance().getWorld().getPlayerByIndex(GameScreen.getInstance().getWorld().getTurnPlayer().getIndex()).getX());
		this.setFieldY(GameScreen.getInstance().getWorld().getPlayerByIndex(GameScreen.getInstance().getWorld().getTurnPlayer().getIndex()).getY());
		// this.reFreshFieldNr();
		this.setPosX(gui.GameScreen.getInstance().getWorld().getPlayerByIndex(GameScreen.getInstance().getWorld().getTurnPlayer().getIndex()).getX());
		this.setPosY(gui.GameScreen.getInstance().getWorld().getPlayerByIndex(GameScreen.getInstance().getWorld().getTurnPlayer().getIndex()).getY());

		// TODO this.setFieldXAim( X-Wert von Player oder ArrowQueue);
		// TODO this.setFieldYAim( Y-Wert von Player oder ArrowQueue);

		// arrowSpeed
		// acceleration
		// damageRadius

	}

	/** Gibt den Aktuellen Wert des Schadens zurï¿½ck */
	public float getAttackValCurrent() {
		return attackValueCurrent;
	}

	/** Setzt den Aktuellen Wert des Schadens (nicht Grundschaden) zurï¿½ck */
	public void setAttackValCurrent(float newAtackValueCurrent) {
		attackValueCurrent = newAtackValueCurrent;
	}

	/** Gibt die Schadensverlustrate zurï¿½ck */
	public float getDamageLosingRate() {
		return damageLosingRate;
	}

	/** setzt die Schadensverlustrate (nur in dieser Klasse aufrufbar!) */
	private void setDamageLosingRate(float newDamageLosingRate) {
		this.damageLosingRate = newDamageLosingRate;
	}

	/** Selbsttrefferrate */
	public float getSelfHittingRate() {
		return this.selfHittingRate;
	}

	/** Selbsttrefferrate setzen */
	private void setSelfHittingRate(float newSelfHittingRate) {
		this.selfHittingRate = newSelfHittingRate;
	}

	/** Treffunsicherheitsquote (Grundwert) */
	public float getAimMissing() {
		return this.aimMissingRate;
	}

	/** Treffunsicherheitsquote (Grundwert) */
	private void setAimMissing(float newAimMissing) {
		this.aimMissingRate = newAimMissing;
	}

	/** Treffunsicherheitsquote (Faktor der Erhï¿½hung) */
	public float getAimMissingRate() {
		return this.aimMissingRate;
	}

	/** Treffunsicherheitsquote (Faktor der Erhï¿½hung) */
	private void setAimMissingRate(float newAimMissingRate) {
		this.aimMissingRate = newAimMissingRate;
	}

	/**
	 * Treffunsicherheitsquote (aktueller Wert =! Grundwert [Erhï¿½hung durch
	 * Entfernung])
	 */
	public float getAimMissingCurrent() {
		return this.aimMissingRate;
	}

	/**
	 * Treffunsicherheitsquote (aktueller Wert [nicht gleich Grundwert; Erhï¿½hung
	 * durch Entfernung]
	 */
	public void setAimMissingCurrent(float newAimMissingCurrent) {
		this.aimMissingRate = newAimMissingCurrent;
	}

	/** Grund-Reichweite (vom Start zum Ziel) des Pfeils in 25m */
	public int getRangeValue() {
		return this.rangeValue;
	}

	/**
	 * Setzt die Grund-Reichweite (vom Start zum Ziel) des Pfeils neu.
	 * 
	 * @param rangeValue
	 *            Der neue Reichweite des Pfeils.
	 */
	private void setRangeValue(int rangeValue) {
		this.rangeValue = rangeValue;
	}

	/**
	 * Aktuelle Reichweite {vom Start zum Ziel} (nicht Grundweite: sie wurde
	 * ggf. durch andere Pfeile,... geï¿½ndert) in 25m genau
	 */
	public int getRangeValueCurrent() {
		return this.rangeValueCurrent;
	}

	public void setRangeValueCurrent(int newRangeValueCurrent) {
		this.rangeValueCurrent = newRangeValueCurrent;
	}

	/** Zurï¿½ckgelgete Distanz des Pfeils [in 25m genau] */
	public int getDistanceReached() {
		return distanceReached;
	}

	/**
	 * Setzt: Zurückgelgete Distanz des Pfeils [in 25m genau]
	 * 
	 * @param newDistanceReached
	 *            (int-Wert)
	 */
	public void setDistanceReached(int newDistanceReached) {
		this.distanceReached = newDistanceReached;
	}

	/** X-Position bei den Feldern */
	public int getFieldX() {
		return fieldX;
	}

	/** X-Position bei den Feldern */
	public void setFieldX(int fieldX) {
		this.fieldX = fieldX;
	}

	/** Y-Position bei den Feldern */
	public int getFieldY() {
		return fieldY;
	}

	/** Y-Position bei den Feldern */
	public void setFieldY(int fieldY) {
		this.fieldY = fieldY;
	}

	/** Gibt die Feldnummer des Feldes zurï¿½ck, auf dem der Pfeil ist */
	public int getFieldNr() {
		return fieldNr;
	}

	/**
	 * Setzt die Feldnummer neu SETZT DAMIT AUCH EINE NEUE POSITON: die Position
	 * des Feldes der 'int fieldNr'
	 */
	public void setFieldNr(int fieldNr) {
		this.fieldNr = fieldNr;
	}

	/**
	 * Gibt die Feldposition X des Feldes (im Field-Koordinatensystem) zurï¿½ck,
	 * dessen Ziel (Zielfeld) der Pfeil anvisiert hat
	 */
	public int getFieldXAim() {
		return fieldXAim;
	}

	/**
	 * Seitzt die Feldposition X des Feldes (aus dem Field-Koordinatensystem)
	 * zurï¿½ck, dessen Ziel (Zielfeld) der anvisiert hat
	 */
	private void setFieldXAim(int fieldXAim) {
		this.fieldXAim = fieldXAim;
	}

	/**
	 * Gibt die Feldposition Y des Feldes (im Field-Koordinatensystem) zurï¿½ck,
	 * dessen Ziel (Zielfeld) der Pfeil anvisiert hat
	 */
	public int getFieldYAim() {
		return fieldYAim;
	}

	/**
	 * Seitzt die Feldposition Y des Feldes (aus dem Field-Koordinatensystem)
	 * zurï¿½ck, dessen Ziel (Zielfeld) der anvisiert hat
	 */
	private void setFieldYAim(int fieldYAim) {
		this.fieldYAim = fieldYAim;
	}

	/** Position X auf dem Bildschrim (Pixel) */
	public int getPosX() {
		return posX;
	}

	/** Position X auf dem Bildschirm (Pixel) */
	public void setPosX(int posX) {
		this.posX = posX;
	}

	/** Position Y auf dem Bildschirm (Pixel) */
	public int getPosY() {
		return posY;
	}

	/** Position Y auf dem Bildschirm (Pixel) */
	public void setPosY(int posY) {
		this.posY = posY;
	}

	@Override
	public int getMaximumStackCount() {
		return 1;
	}
}

// /** Setzt die Feldnummer neu;
// * Der neue Wert wird automaisch aus den Werten fï¿½r die X- und Y- Position in
// den Feldkoordinaten berechnet */
// public void reFreshFieldNr () {
// this.fieldNr = Field.getFieldNrFromPos(fieldX, fieldY);
// }
// /** setzt die X UND Y Positon bei den Feldern neu;
// * Der neue Wert wird automatisch aus der FieldNr dieses Pfeils berechnet
// */
// public void reFreshPosXYfield () {
// this.posX = com.github.pfeile.gui.Field.getPosXFromFieldNr (fieldNr);
// this.posY = com.github.pfeile.gui.Field.getPosYFromFieldNr (fieldNr);
// }
// --> arrowSpeed
// --> acceleration
// --> damageRadius


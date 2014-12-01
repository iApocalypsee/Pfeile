package player.weapon;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import general.Mechanics;
import geom.functions.FunctionCollection;

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
public abstract class AbstractArrow extends RangedWeapon implements gui.Drawable {

	/**
	 * Wert des Schadens (nach Abnahme durch Entfernung)
	 */
	protected float attackValueCurrent;

    /** the rotation of the BufferedImage to draw the arrow to the direction of the attacked field */
    protected double rotation;

    /** the speed of the arrow */
	protected double speed;

	/**
	 * Wert in 'float', wie stark der Pfeil an Schaden verliert : <b> jeweils
	 * nach 25m <b>
	 * 
	 * Prozentsatz (0.1f = 10%), was dem Pfeil an Schaden abgezogen wird zählt
	 * erst nach 25m Entfernung
	 * */
	protected float damageLosingRate;

	/** Wahrscheinlichkeit mit der Man sich selbst trifft */
	protected float selfHittingRate;

	/**
	 * Treffunsicherheitsquote (Grundwert) Je h�her, desto unwahrscheinlicher
	 * ein Treffer Je weiter die Distance, desto unwahrscheinlicher ein Treffer
	 */
	protected float aimMissing;

	/** Treffunsicherheitsquote: Faktor der Erh�hung (pro 25m) */
	protected float aimMissingRate;

	/** aktuelle Treffunsicherheitsquote */
	protected float aimMissingCurrent;

	/**
	 * Reichweite des Pfeils : in Metern angegeben [25m genau] Verwenden, falls
	 * andere Pfeile [d.h. Sturmpfeil] oder Felder / andere Eigenschaften die
	 * Reichweite ver�ndern
	 */
	protected int rangeValueCurrent;

    /** changes the damage radius of that arrow */
    private void setDamageRadius (float damageRadius) {
        this.damageRadius = damageRadius;
    }

    /* the radius in tiles, where the enemies get damage */
    public float getDamageRadius () {
        return damageRadius;
    }

    /** Wie weit der Schaden des Pfeils nach dem Auftreffen reicht */
	protected float damageRadius;

	/** Positon im Koordinatensystem des Pfeils: Die X-Position */
	protected int fieldX;
	/** Position im Koordinatensystem des Pfeils: Y-Position */
	protected int fieldY;
	/** Nummer des Feldes über das sich der Pfeil befindet */
	protected int fieldNr;
	/** X-Position des Pfeils - f�r GUI */
	protected int posX;
	/** Y-Position des Pfeils - f�r GUI */
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

    /** X-Position des Ziels auf Bildschirm */
    protected int posXAim;

    /** Y-Poition auf Bildschirm des Ziels */
    protected int posYAim;

	/**
	 * <b> KONSTUCKTOR: <b> float : Grundschaden des Pfeils float :
	 * Grundverteidigung des Pfeils int : Grundreichweite des Pfeils float :
	 * Selbsttrefferwahrscheinlichkeit float : Treffunsicherheitsquote float :
	 * Faktor der Erh�hung der Treffunsicherheitsquote float : Faktor der
	 * Verringerung der Schadenswirkung
	 * 
	 * // int : arrowSpeed - Geschwindigkeit des Pfeils // int : acceleration -
	 * Beschleunigung des Pfeils // int : damageRadius - Schadensradius des
	 * Pfeils
	 */
	public AbstractArrow(float attackVal, float defenseVal, int rangeVal,
			float selfHittingRate, float aimMissing, float aimMissingRate,
			float damageLosingRate, double speed, float damageRadius, String name) {
		super(name);
		setAttackValue(attackVal);
		setAttackValCurrent(attackVal);
		setDefenseValue(defenseVal);
		// Reichweite des Pfeils wird minimal (+/- 1 Feld) an die Entfernung
		// angepasst
		if (Mechanics.worldSizeX <= 7) {
			setRange(rangeVal - 100);
			setRangeValueCurrent(rangeVal - 100);
		} else if (Mechanics.worldSizeX > 7 && Mechanics.worldSizeX <= 17) {
			setRange(rangeVal);
			setRangeValueCurrent(rangeVal);
		} else {
			setRange(rangeVal + 100);
			setRangeValueCurrent(rangeVal + 100);
		}
		setSelfHittingRate(selfHittingRate);
		setAimMissing(aimMissing);
		setAimMissingRate(aimMissingRate);
		setAimMissingCurrent(aimMissing);
		setDamageLosingRate(damageLosingRate);
		setSpeed(speed);
        setDamageRadius(damageRadius);
	}

	/** Gibt den Aktuellen Wert des Schadens zur�ck */
	public float getAttackValCurrent() {
		return attackValueCurrent;
	}

	/** Setzt den Aktuellen Wert des Schadens (nicht Grundschaden) zur�ck */
	public void setAttackValCurrent(float newAtackValueCurrent) {
		attackValueCurrent = newAtackValueCurrent;
	}

	/** Gibt die Schadensverlustrate zur�ck */
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

	/** Treffunsicherheitsquote (Faktor der Erh�hung) */
	public float getAimMissingRate() {
		return this.aimMissingRate;
	}

	/** Treffunsicherheitsquote (Faktor der Erh�hung) */
	private void setAimMissingRate(float newAimMissingRate) {
		this.aimMissingRate = newAimMissingRate;
	}

	/**
	 * Treffunsicherheitsquote (aktueller Wert =! Grundwert [Erh�hung durch
	 * Entfernung])
	 */
	public float getAimMissingCurrent() {
		return this.aimMissingRate;
	}

	/**
	 * Treffunsicherheitsquote (aktueller Wert [nicht gleich Grundwert; Erh�hung
	 * durch Entfernung]
	 */
	public void setAimMissingCurrent(float newAimMissingCurrent) {
		this.aimMissingRate = newAimMissingCurrent;
	}

    /** returns the current attack value of the arrow */
    @Override
    public float getAttackValue () {
        return attackValueCurrent;
    }

    /** TODO: Defense Value is independed from the attacking arrow */
    @Override
    public float getDefenseValue () {
        return super.getDefenseValue();
    }

    /**
	 * Aktuelle Reichweite {vom Start zum Ziel} (nicht Grundweite: sie wurde
	 * ggf. durch andere Pfeile,... ge�ndert) in 25m genau
	 */
	public int getRangeValueCurrent() {
		return rangeValueCurrent;
	}

	public void setRangeValueCurrent(int newRangeValueCurrent) {
		rangeValueCurrent = newRangeValueCurrent;
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

    /**
	 * Gibt die Feldposition X des Feldes (im Field-Koordinatensystem) zur�ck,
	 * dessen Ziel (Zielfeld) der Pfeil anvisiert hat
	 */
	public int getFieldXAim() {
		return fieldXAim;
	}

	/**
	 * Seitzt die Feldposition X des Feldes (aus dem Field-Koordinatensystem)
	 * zur�ck, dessen Ziel (Zielfeld) der anvisiert hat
	 */
	public void setFieldXAim(int fieldXAim) {
		this.fieldXAim = fieldXAim;
	}

	/**
	 * Gibt die Feldposition Y des Feldes (im Field-Koordinatensystem) zur�ck,
	 * dessen Ziel (Zielfeld) der Pfeil anvisiert hat
	 */
	public int getFieldYAim() {
		return fieldYAim;
	}

	/**
	 * Seitzt die Feldposition Y des Feldes (aus dem Field-Koordinatensystem)
	 * zur�ck, dessen Ziel (Zielfeld) der anvisiert hat
	 */
	public void setFieldYAim(int fieldYAim) {
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

    /** gibt die X-Position auf dem Bildschirm vom Ziel zurück. It is equal to the Center-X-Position of the aimed tile minus half the arrowImage.getWidth()*/
    public int getPosXAim () {
        return posXAim;
    }

    /** setzt die x-Position auf dem Bildschirm vom Ziel */
    public void setPosXAim (int posXAim) {
        this.posXAim = posXAim;
    }

    /** Y-Position auf dem Bildschrim vom Ziel */
    public int getPosYAim () {
        return posYAim;
    }

    /** setzt die y-Position auf dem Bildschrim vom Ziel
     * <b> use calculateRotation if neccary </b>*/
    public void setPosYAim (int posYAim) {
        this.posYAim = posYAim;
    }

    /** the speed of the arrow in tiles per turn */
	public double getSpeed() {
		return speed;
	}

	protected void setSpeed(double speed) {
		this.speed = speed;
	}

    @Override
    public int getRange () {
        return super.getRange();
    }

    /** returns the rotation of the BufferedImage. With this value the image is drawn in direction to the aim. It's in radient.*/
    public double getRotation () {
        return rotation;
    }

    /** changes the rotation of the BufferedImage. With this value the image is drawn in the direction to the aim.
     * Basically it updates the the rotation with:
     * <p> <code>rotation = FunctionCollection.angle(getPosX(), getPosY(), getPosXAim(), getPosYAim());</code>
     */
    public void calculateRotation () {
        rotation = FunctionCollection.angle(posX, posY, posXAim, posYAim);
    }

    /** gibt die BufferedImage des Pfeils zur�ck
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	public abstract BufferedImage getImage();

    /** TODO: do the Zoom */
	@Override
	public void draw(Graphics2D g) {
        AffineTransform old = g.getTransform();
        // it should be rotated from the center of the arrowImage
        g.rotate(getRotation(), getPosX() + (int) (0.5 * getImage().getWidth()), getPosY() + (int) (0.5 * getImage().getHeight()));
		g.drawImage(getImage(), getPosX(), getPosY(), getImage().getWidth(), getImage().getHeight(), null);
        g.setTransform(old);
	}
}
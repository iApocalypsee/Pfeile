package player.weapon;

import general.PfeileContext;
import geom.functions.FunctionCollection;
import gui.Drawable;
import player.BoardPositionable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Die abstrakte Pfeil-Klasse, von der die Pfeilarten abgeleitet werden. Wenn
 * Spieler Pfeile bekommen, bekommen sie Pfeil-Instanzen durch den Konstruktor
 * der Subklasse. Diese Objekte kommen in das Inventar des Spielers.
 *
 * The attackValue of the arrow is its maximum value, which isn't constant because of the damageRadius
 *
 * USE ARROW HELPER for the indexes.
 * <b> Indexes: </b> Feuer = 0; Wasser = 1; Sturm = 2; Stein = 3; Eis = 4; Blitz
 * = 5; Licht = 6; Schatten = 7;
 * 
 * @version 25.11.2013
 */
public abstract class AbstractArrow extends RangedWeapon implements Drawable, BoardPositionable {

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

    /** Positon im Koordinatensystem des Pfeils: Die X-Position */
	protected int fieldX;
	/** Position im Koordinatensystem des Pfeils: Y-Position */
	protected int fieldY;
    /** X-Position des Pfeils - f�r GUI */
	protected int posX;
	/** Y-Position des Pfeils - f�r GUI */
	protected int posY;

	/**
	 * <b> KONSTUCKTOR: </b> <p> attackVal: float : Grundschaden des Pfeils <p> defenseVal: float :
	 * Grundverteidigung des Pfeils <p> rangeVal: int : Grundreichweite des Pfeils <p> float :
	 * Selbsttrefferwahrscheinlichkeit <p> float : Treffunsicherheitsquote <p> float :
	 * Faktor der Erh�hung der Treffunsicherheitsquote <p> float : Faktor der
	 * Verringerung der Schadenswirkung
	 */
	public AbstractArrow(float attackVal, float defenseVal, double rangeVal,
			float selfHittingRate, float aimMissing, float aimMissingRate,
			float damageLosingRate, double speed, float damageRadius, String name) {
		super(name);
		setAttackValue(attackVal);
		setDefenseValue(defenseVal);
		// Reichweite des Pfeils wird minimal (+/- 1 Feld) an die Entfernung
		// angepasst
		if (PfeileContext.WORLD_SIZE_X().get() <= 22) {
			setRange(rangeVal - 100);
		} else if (PfeileContext.WORLD_SIZE_X().get() < 40) {
			setRange(rangeVal);
		} else {
			setRange(rangeVal + 100);
		}
		setSelfHittingRate(selfHittingRate);
		setAimMissing(aimMissing);
		setAimMissingRate(aimMissingRate);
		setDamageLosingRate(damageLosingRate);
		setSpeed(speed);
        getAim().setDamageRadius(damageRadius);
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
	private void setAimMissingRate(float aimMissingRate) {
		this.aimMissingRate = aimMissingRate;
	}

    /** X-Position bei den Feldern */
    @Override
	public int getGridX () {
		return fieldX;
	}

	/** X-Position bei den Feldern */
	public void setGridX (int fieldX) {
		this.fieldX = fieldX;
	}

	/** Y-Position bei den Feldern */
    @Override
	public int getGridY () {
		return fieldY;
	}

	/** Y-Position bei den Feldern */
	public void setGridY (int fieldY) {
		this.fieldY = fieldY;
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

    /** the speed of the arrow in tiles per turn */
	public double getSpeed() {
		return speed;
	}

	protected void setSpeed(double speed) {
		this.speed = speed;
	}

    /** returns the rotation of the BufferedImage. With this value the image is drawn in direction to the aim. It's in radient.*/
    public double getRotation () {
        return rotation;
    }

    @Override
    public double damageAt (int posX, int posY) {
        double currentDistance = FunctionCollection.distance(getAim().getGridX(), getAim().getGridY(), posX, posY);

        if (currentDistance >= getAim().getDamageRadius()) {
            return 0;
        } else {
            // this the normalized cos (cos(x * 0.5 * Math.PI) of the ratio from the distance from center
            double distanceRatio = Math.cos((currentDistance / getAim().getDamageRadius()) * 0.5 * Math.PI);
            // distanceRatio * distanceRatio: because the curve is more smoothly at the edges.
            return distanceRatio * distanceRatio * getAttackValue() * PfeileContext.DAMAGE_MULTI().get();
        }
    }

    /** changes the rotation of the BufferedImage. With this value the image is drawn in the direction to the aim.
     * Basically it updates the the rotation with:
     * <p> <code>rotation = FunctionCollection.angle(getPosX(), getPosY(), getAim().getPosXGui(), getAim().getPosYGui());</code>
     */
    public void calculateRotation () {
        rotation = FunctionCollection.angle(posX, posY, getAim().getPosXGui(), getAim().getPosYGui());
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
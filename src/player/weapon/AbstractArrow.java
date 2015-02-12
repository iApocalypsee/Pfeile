package player.weapon;

import comp.ImageComponent;
import comp.InternalFrame;
import comp.Label;
import general.PfeileContext;
import geom.functions.FunctionCollection;
import gui.FrameContainerObject;
import gui.screen.GameScreen;
import player.BoardPositionable;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public abstract class AbstractArrow extends RangedWeapon implements BoardPositionable {

    private ImageComponent component;

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
		// Reichweite des Pfeils wird minimal (+/- 1 Tile) an die Entfernung angepasst
		if (PfeileContext.WORLD_SIZE_X().get() <= 22) {
			setRange(rangeVal - 1);
		} else if (PfeileContext.WORLD_SIZE_X().get() < 40) {
			setRange(rangeVal);
		} else {
			setRange(rangeVal + 1);
		}
		setSelfHittingRate(selfHittingRate);
		setAimMissing(aimMissing);
		setAimMissingRate(aimMissingRate);
		setDamageLosingRate(damageLosingRate);
		setSpeed(speed);
        getAim().setDamageRadius(damageRadius);
		component = new ImageComponent(0, 0, getImage(), GameScreen.getInstance());
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased (MouseEvent e) {
				// Variables.
				FrameContainerObject containerObject = GameScreen.getInstance().getFrameContainer();
				InternalFrame dataFrame = new InternalFrame(50, 50, 135, 125, GameScreen.getInstance());
				String arrowType = "Arrow type: " + getName();
				String damage = "Damage: " + getAttackValue();
				String defense = "Defense: " + getDefenseValue();
				String speed = "Speed: " + getSpeed();


				Label arrowTypeLabel = new Label(10, 15, GameScreen.getInstance(), arrowType);
				Label damageLabel = new Label(10, 27, GameScreen.getInstance(), damage);
				Label defenseLabel = new Label(10, 39, GameScreen.getInstance(), defense);
				Label speedLabel = new Label(10, 51, GameScreen.getInstance(), speed);

				// Add the label to show the actual data to the screen.
				dataFrame.add(arrowTypeLabel);
				dataFrame.add(damageLabel);
				dataFrame.add(defenseLabel);
				dataFrame.add(speedLabel);
				// When the frame closes, it should be removed from the container object as well.
				dataFrame.onClosed().register(new AbstractFunction0<Object>() {
					@Override
					public Object apply () {
						containerObject.removeFrame(dataFrame);
						return BoxedUnit.UNIT;
					}
				});

				containerObject.addFrame(dataFrame);
			}
		});
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
	public float getAimMissing() { return aimMissingRate; }

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

    /** X-position in tiles */
    @Override
	public int getGridX () { return fieldX; }

	/** x-position in tiles */
	public void setGridX (int gridX) { this.fieldX = gridX; }

	/** y-position in tiles */
    @Override
	public int getGridY () { return fieldY; }

	/** sets the y-position in tiles */
	public void setGridY (int gridY) { this.fieldY = gridY; }

	/** x-position on the screen (in px).
	 * <p>
     * This call is redirected to <code>getComponent().setX(posX)</code> and rotates the arrow by calling <code>calculateRotation()</code> */
	public void setPosX(int posX) {
		component.setX(posX);
		calculateRotation();
	}

	/** sets the y-position on the screen (in px).
	 * <p>
     * This call is redirected to <code>getComponent().setY(posY)</code> and the ImageComponent will be rotated <code>calulateRotation()</code>*/
	public void setPosY(int posY) {
		component.setY(posY);
		calculateRotation();
	}

    /** the speed of the arrow in tiles per turn */
	public double getSpeed() { return speed; }

	protected void setSpeed(double speed) { this.speed = speed; }

    /** this returns the Component of an Arrow. */
    public ImageComponent getComponent () { return component; }

    @Override
    public double damageAt (int posX, int posY) {
		// the distance between the LivingEntity (posX/posY) and the Aim
        double currentDistance = FunctionCollection.distance(posX, posY, getAim().getGridX(), getAim().getGridY());

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
     * <p> <code>rotation = FunctionCollection.angle(...getCenterX(), ...getCenterY(), getAim().getPosXGui(), getAim().getPosYGui());</code>
     */
    public void calculateRotation () {
        component.rotateDegree(Math.toDegrees(FunctionCollection.angle(
                getComponent().getPreciseRectangle().getCenterX(), getComponent().getPreciseRectangle().getCenterY(), getAim().getPosXGui(), getAim().getPosYGui())));
    }

    /** returns the BufferedImage of this arrow.
	 * @see <code> ArrowHelper.getArrowImage(int selectedIndex) </code> */
	public abstract BufferedImage getImage();
}
package player;

import general.Delegate;
import general.Function0Delegate;
import geom.functions.FunctionCollection;
import newent.Player;

import java.io.Serializable;

/**
 * Contains only data relevant to life of an entity.
 */
public class Life {

	private double lifeMax;
	private double lifeRegen;
	private double life;

	/** Called when the life has been changed. */
	public final Delegate<LifeChangedEvent> onLifeChanged = new Delegate<>();

    /** Called when the life is equal to or under 0. This happens, then the livingEntity dies. */
	public final Function0Delegate onDeath = new Function0Delegate();

	/**
	 * Creates a new instance from the Life class with customized preferences.
	 * @param lifeMax The maximum life that the object has. (required: > 0.0)
	 * @param lifeRegeneration The regeneration per turn.
	 * @param startingLife The starting amount of life.
	 */
	public Life(double lifeMax, double lifeRegeneration, double startingLife) {
		scala.Predef.require(lifeMax > 0.0);
		this.lifeMax = lifeMax;
		this.lifeRegen = lifeRegeneration;
		this.life = startingLife;

		onLifeChanged.registerJava(v1 -> {
			if(v1.getNewLife() <= 0) {
				onDeath.apply();
			}
		});
	}

    /** Creates a new instance from the Life class by using the standard values.
     * This is similar to: <code>new Life (Player.LifeMax().get(), Player.LifeRegeneration().get(), Player.LifeMax().get())</code> with checking
     * if these values have been initialized.
     *
     * <b>This is just a constructor for the player. So use the other constructor. </b>
     */
    @Deprecated
    public Life () {
        if (Player.maximumLife().get() <= 0)
            lifeMax = Player.maximumLife().get();
        else {
            System.err.println("The value for Player.LifeMax().get() is not valid. It might be unset. Life is \"maximales Leben: normal\"");
            lifeMax = 400;
        }
        if (Player.lifeRegeneration().get() <= 0)
            lifeRegen = Player.lifeRegeneration().get();
        else {
            System.err.println("The value for Player.LifeRegeneration().get() is not valid. It maight be unset. \"Lebensregeneration: normal\"");
            if (Player.maximumLife().get() <= 0)
                lifeRegen = (int) Math.round(0.5 * (400 * 0.02) + 4.5);
            else
                lifeRegen = (int) Math.round(0.5 * (Player.lifeRegeneration().get() * 0.02 + 4.5));
        }
        life = lifeMax;
    }

    /** GETTER: return 'life */
	public double getLife() {
		return life; 
	}

	/** GETTER: return 'LIFEMAX' */
	public double getMaxLife() {
		return lifeMax;
	}

	/** GETTER: return 'relativeLife' */
	public double getRelativeLife() {
		return (life / lifeMax) * 100;
	}

	public double getLifeRegeneration() {
		return lifeRegen;
	}

	/** SETTER: set 'life'. If the <code>newLife</code> is higher than the <code>getMaxLife()</code> the life is set
     * to maxLife. If the life is below <code>0</code>, the life will be 0.
     *
     * @param newLife the absolute number of the life
     * @see player.Life#changeLife(double)
     * */
	public void setLife(double newLife) {
        if (newLife > lifeMax)
            newLife = lifeMax;
        else if (newLife < 0)
            newLife = 0;
        final double oldLife = this.life;
        this.life = newLife;
		onLifeChanged.apply(new LifeChangedEvent(oldLife, newLife));
	}

    /**
     * <code>life = life + changeOfLife</code>. The absolute change of life. If the sum is below <code>0</code>, the life
     * is 0; if the sum is higher than <code>getMaxLife()</code>, the life will be the maximum life.
     *
     * @param changeOfLife the delta value of the change of life
     * @see player.Life#setLife(double)
     */
    public void changeLife (double changeOfLife) {
        final double oldLife = this.life;
        this.life = FunctionCollection.clamp(changeOfLife, 0, this.lifeMax);
        onLifeChanged.apply(new LifeChangedEvent(oldLife, this.life));
    }

    /**
     * Causes this object to regenerate <tt>lifeRegeneration</tt> life.
     * This method should be called when a turn cycle ends.
     */
    public void updateLife() {
        changeLife(lifeRegen);
    }

	/**
	 * Fired when the life has been changed. <p></p>
	 * This class is just an event class that carries information to the delegates. It should not
	 * execute any code beforehand.
	 */
	public final class LifeChangedEvent implements Serializable {

        private static final long serialVersionUID = 1L;

        private final double oldLife;
        private final double newLife;

		public LifeChangedEvent(double oldLife, double newLife) {
            this.oldLife = oldLife;
            this.newLife = newLife;
		}

		public double getNewLife() {
			return newLife;
		}

        public double getOldLife() {
            return oldLife;
        }
    }
}

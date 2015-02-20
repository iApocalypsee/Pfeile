package player;

import general.Delegate;
import newent.Player;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

/**
 * Contains only data relevant to life of an entity.
 */
public class Life {

	private double lifeMax;
	private double lifeRegen;
	private double life;

	/** Called when the life has been changed. */
	public final Delegate.Delegate<LifeChangedEvent> onLifeChanged = new Delegate.Delegate<>();

    /** Called when the life is equal to or under 0. This happens, then the livingEntity dies. */
	public final Delegate.Function0Delegate onDeath = new Delegate.Function0Delegate();

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

		onLifeChanged.register(new AbstractFunction1<LifeChangedEvent, BoxedUnit>() {
			@Override
			public BoxedUnit apply(LifeChangedEvent v1) {
				if(v1.getNewLife() <= 0) {
					onDeath.call();
				}
				return BoxedUnit.UNIT;
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
        if (Player.MAXIMUM_LIFE().get() <= 0)
            lifeMax = Player.MAXIMUM_LIFE().get();
        else {
            System.err.println("The value for Player.LifeMax().get() is not valid. It might be unset. Life is \"maximales Leben: normal\"");
            lifeMax = 400;
        }
        if (Player.LIFE_REGENERATION().get() <= 0)
            lifeRegen = Player.LIFE_REGENERATION().get();
        else {
            System.err.println("The value for Player.LifeRegeneration().get() is not valid. It maight be unset. \"Lebensregeneration: normal\"");
            if (Player.MAXIMUM_LIFE().get() <= 0)
                lifeRegen = (int) Math.round(0.5 * (400 * 0.02) + 4.5);
            else
                lifeRegen = (int) Math.round(0.5 * (Player.LIFE_REGENERATION().get() * 0.02 + 4.5));
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
     * to maxLife*/
	public void setLife(double newLife) {
        if (newLife > lifeMax)
            newLife = lifeMax;
        life = newLife;
		onLifeChanged.call(new LifeChangedEvent(newLife));
	}

	/**
	 * Fired when the life has been changed. <p></p>
	 * This class is just an event class that carries information to the delegates. It should not
	 * execute any code beforehand.
	 */
	public final class LifeChangedEvent {

		private final double newLife;

		public LifeChangedEvent(double newLife) {
			this.newLife = newLife;
		}

		public double getNewLife() {
			return newLife;
		}
	}
}

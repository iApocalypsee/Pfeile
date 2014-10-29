package player;

import general.Delegate;
import general.Main;
import general.Mechanics;
import newent.EntityLike;
import newent.LivingEntity;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import java.util.List;

/**
 * Contains only data relevant to life of an entity.
 */
public class Life {

	private double lifemax;
	private double liferegen;
	private double life;

	/** Called when the life has been changed. */
	public final Delegate.Delegate<LifeChangedEvent> onLifeChanged = new Delegate.Delegate<LifeChangedEvent>();
	public final Delegate.Function0Delegate onDeath = new Delegate.Function0Delegate();

	/**
	 * Creates a new instance from the Life class with customized preferences.
	 * @param lifemax The maximum life that the object has.
	 * @param liferegen The regeneration per turn.
	 * @param startingLife The starting amount of life.
	 */
	public Life(double lifemax, double liferegen, double startingLife) {
		scala.Predef.require(lifemax > 0.0);
		this.lifemax = lifemax;
		this.liferegen = liferegen;
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
     * This is similar to: <code>new Life (Mechanics.lifeMax, Mechanics.lifeRegeneration, Mechanics.lifeMax)</code> with checking
     * if these values have been initialized.
     */
    public Life () {
        if (Mechanics.lifeMax <= 0)
            lifemax = Mechanics.lifeMax;
        else {
            System.err.println("The value for Mechanics.lifeMax is not valid. It maight be unset. Life is \"maximales Leben: normal\"");
            lifemax = 400;
        }
        if (Mechanics.lifeRegeneration <= 0)
            liferegen = Mechanics.lifeRegeneration;
        else {
            System.err.println("The value for Mechanics.lifeRegeneration is not valid. It maight be unset. \"Lebensregeneration: normal\"");
            if (Mechanics.lifeMax <= 0)
                liferegen = (int) Math.round(0.5 * (400 * 0.02) + 4.5);
            else
                liferegen = (int) Math.round(0.5 * (Mechanics.lifeMax * 0.02 + 4.5));
        }
        life = lifemax;
    }

    /** GETTER: return 'life */
	public double getLife() {
		return life; 
	}

	/** GETTER: return 'LIFEMAX' */
	public double getMaxLife() {
		return lifemax;
	}

	/** GETTER: return 'relativeLife' */
	public double getRelativeLife() {
		return (life / lifemax) * 100;
	}

	public double getLifeRegeneration() {
		return liferegen;
	}

	/** SETTER: set 'life' */
	public void setLife(double newLife) {
		this.life = newLife;
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

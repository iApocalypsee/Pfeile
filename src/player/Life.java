package player;

import general.Delegate;
import general.Main;
import general.Mechanics;
import gui.Drawable;

import java.awt.*;

/**
 * Contains only data relevant to life of an entity.
 */
public class Life {

	private double lifemax;
	private double liferegen;
	private double life;

	/** Called when the life has been changed. */
	public final Delegate.Delegate<LifeChangedEvent> onLifeChanged = new Delegate.Delegate<LifeChangedEvent>();

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
	 * Fired when the life has been changed.
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

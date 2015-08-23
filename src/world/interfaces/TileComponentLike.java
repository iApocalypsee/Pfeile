package world.interfaces;

import newent.VisionStatus;

import static newent.VisionStatus.*;

/**
 * Interface for components that react to different states of vision.
 * @see newent.VisionStatus
 */
public interface TileComponentLike {

	/**
	 * Prepares this component for `VisionStatus.Hidden`
	 */
	void adaptHidden();

	/**
	 * Prepares this component for `VisionStatus.Revealed`
	 */
	void adaptRevealed();

	/**
	 * Prepares this component for `VisionStatus.Visible`
	 */
	void adaptVisible();

	default void adaptVisionStatus(final VisionStatus status) {
		switch(status) {
			case Hidden:
				adaptHidden();
				break;
			case Revealed:
				adaptRevealed();
				break;
			case Visible:
				adaptVisible();
				break;
		}
	}

}

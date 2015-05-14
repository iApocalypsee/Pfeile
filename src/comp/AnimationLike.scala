package comp

import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * Trait for classes that represent animation implementations.
 */
abstract class AnimationLike extends ImageLike {

  /**
   * Causes the animation to step to the next keyframe.
   * @return The next keyframe as an image.
   */
  def nextKeyframe(): BufferedImage

  /**
   * Causes the animation to step to the previous keyframe.
   * @return The previous keyframe as an image.
   */
  def previousKeyframe(): BufferedImage

  /**
   * Draws the current image and calls automatically for the next keyframe in the animation.
   * @param g The graphics object.
   * @param x Ditto.
   * @param y Ditto.
   * @param width Ditto.
   * @param height Ditto.
   * @return
   */
  override def drawImage(g: Graphics2D, x: Int, y: Int, width: Int, height: Int) = {
    super.drawImage(g, x, y, width, height)
    nextKeyframe()
  }

}

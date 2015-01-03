package comp

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

import scala.math._

import gui.Screen

/**
  * Component that just owns an image as a representation of itself
  * @param x The x position of the image.
  * @param y The y position of the image.
  * @param image The image to display.
  * @param screen The screen to bind the component to. Needed for listener implementation.
  */
class ImageComponent(x: Int, y: Int, image: BufferedImage, screen: Screen) extends Component(x, y, image.getWidth, image.getHeight, screen) {

  /** in Radians */
  private var _rotation = 0.0
  private var _rotTransform = new AffineTransform()

  /**
    * Rotates the component by the angle specified around its center. Consequently, the AffineTransform is translated
    * to <code>getSimplifiedBounds().getCenterX(), getSimplifiedBounds().getCenterY()</code>, rotated and then
    * translated to <code>- getSimplifiedBounds().getCenterX(), - getSimplifiedBounds().getCenterY()</code>.
    * <p>
    * <p>
    * This method is the same like <code>imageComponent.rotateRadians(Math.toRadians(angle))</code>.
    *
    * @param angle The angle to rotate the component by, in degrees.
    */
  def rotateDegree(angle: Double): Unit = {
    rotateRadians(toRadians(angle))
  }

  /**
   * Rotates the component by the angle specified around its center. Consequently, the AffineTransform is translated
   * to <code>getSimplifiedBounds().getCenterX(), getSimplifiedBounds().getCenterY()</code>, rotated and then
   * translated to <code>- getSimplifiedBounds().getCenterX(), - getSimplifiedBounds().getCenterY()</code>.
   *
   * @param angle The angle to rotate the component by, in radians.
   */
  def rotateRadians(angle: Double): Unit = {
    val rotTransform = AffineTransform.getRotateInstance(angle, getSimplifiedBounds.getCenterX, getSimplifiedBounds.getCenterY)
    applyTransformation(rotTransform)
    _rotation = angle
    _rotTransform = rotTransform
  }

  /** the rotation of this ImageComponent in Radians. To set this value use <code>rotateRadians</code> or <code>rotateDegree</code>.*/
  def getRotation: Double = { _rotation }

  override def draw(g: Graphics2D): Unit = {
    val oldTransformation = g.getTransform
    g.setTransform(_rotTransform)
    g.drawImage(image, getX, getY, null)
    g.setTransform(oldTransformation)
  }
}

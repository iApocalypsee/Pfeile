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
class ImageComponent(x: Int, y: Int, image: BufferedImage, screen: Screen) extends Component(x, y, image.getWidth(), image.getHeight(), screen) {

  private var _rotation = 0.0
  private var _rotTransform = new AffineTransform()

  /**
    * Rotates the component by the angle specified.
    * @param angle The angle to rotate the component by, in degrees.
    */
  def rotate(angle: Double): Unit = {
    applyTransformation(AffineTransform.getRotateInstance(toRadians(angle)))
    _rotation += angle
    _rotTransform = AffineTransform.getRotateInstance(toRadians(_rotation))
  }

  override def draw(g: Graphics2D): Unit = {
    val oldTransformation = g.getTransform
    g.setTransform(_rotTransform)
    g.drawImage(image, x, y, null)
    g.setTransform(oldTransformation)
  }
}

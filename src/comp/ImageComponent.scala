package comp

import java.awt.Graphics2D
import java.awt.image.BufferedImage

import gui.screen.Screen

class ImageLikeComponent(x: Int, y: Int, imageLike: ImageLike, screen: Screen) extends Component(x, y, imageLike.image.getWidth, imageLike.image.getHeight, screen) {

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
    getTransformation.rotate(angle)
  }

  /** the rotation of this ImageComponent in Radians. To set this value use <code>rotateRadians</code> or <code>rotateDegree</code>.*/
  def getRotation = getTransformation.rotation

  override def draw(g: Graphics2D): Unit = {
    imageLike.drawImage(g, getX, getY, getWidth, getHeight)
  }

}

/**
  * Component that just owns an image as a representation of itself
  * @param x The x position of the image.
  * @param y The y position of the image.
  * @param image The image to display.
  * @param screen The screen to bind the component to. Needed for listener implementation.
  */
class ImageComponent(x: Int, y: Int, image: BufferedImage, screen: Screen) extends Component(x, y, image.getWidth, image.getHeight, screen) {

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
    getTransformation.rotate(angle)
  }

  /** the rotation of this ImageComponent in Radians. To set this value use <code>rotateRadians</code> or <code>rotateDegree</code>.*/
  def getRotation = getTransformation.rotation

   /**
    * getting the image of the ImageComponent by this <code>synchronized</code> method. So only use it, if you can't avoid it.
    *
    * @return the image of the ImageComponent
    */
  def getBufferedImage: BufferedImage = synchronized {
      image
  }

  override def draw(g: Graphics2D): Unit = {
    val oldTransformation = g.getTransform
    val src_s = getSourceShape.getBounds
    g.setTransform(getTransformation.concatenatedMatrix)
    g.drawImage(image, src_s.x, src_s.y, src_s.width, src_s.height, null)
    g.setTransform(oldTransformation)
  }
}

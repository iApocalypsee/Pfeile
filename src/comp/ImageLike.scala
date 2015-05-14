package comp

import java.awt.image.BufferedImage
import java.awt.{Color, Graphics2D}

import general.Property

/**
  * Everything that can be described as an image can inherit this trait.
  */
trait ImageLike {

  /**
    * The image that is going to get drawn.
    * @return The image representation.
    */
  def image: BufferedImage

  // Java.
  def getImage = image

  /**
    * Draws the given image with the graphics context.
    *
    * This method has been created solely for the purpose of decomplicating several drawing processes
    * of special images. With this method, the user ceases control over the image drawing process, creating
    * room for abstractions.
    *
    * @param g The graphics object.
    * @param x Ditto.
    * @param y Ditto.
    * @param width Ditto.
    * @param height Ditto.
    */
  def drawImage(g: Graphics2D, x: Int, y: Int, width: Int, height: Int): Unit = {
    g.drawImage(image, x, y, width, height, null)
  }

}

/**
  * Ditto.
  * @param image The image to be displayed.
  */
class StaticImage(override val image: BufferedImage) extends ImageLike

/**
 * Establishes a keyframe animation with several keyframes.
 * The keyframes switch with every draw call, so that it creates the illusion of movement.
 * @param keyframeStrip The image in which all steps of the animations are saved.
 * @param keyframes How many keyframes does this animation contain?
 * @param startKeyframe The start keyframe. Zero is the first keyframe.
 */
class KeyframeAnimation(keyframeStrip: BufferedImage, keyframes: Int, startKeyframe: Int) extends AnimationLike {

  def this(keyframeStrip: BufferedImage, keyframes: Int) = this(keyframeStrip, keyframes, 0)

  private val partitions: Seq[BufferedImage] = for (i <- 0 until keyframes) yield {
    orientation match {
      case KeyframeAnimation.HorizontalAnimation =>
        val widthPerKeyframe = keyframeStrip.getWidth / keyframes
        keyframeStrip.getSubimage(i * widthPerKeyframe, 0, widthPerKeyframe, keyframeStrip.getHeight)
      case KeyframeAnimation.VerticalAnimation =>
        val heightPerKeyframe = keyframeStrip.getHeight / keyframes
        keyframeStrip.getSubimage(0, i * heightPerKeyframe, keyframeStrip.getWidth, heightPerKeyframe)
      case _ => throw new NotImplementedError("What kind of orientation is this?")
    }
  }

  private var currentKeyframeIndex = startKeyframe

  def orientation = keyframeStrip.getWidth match {
    case width if width < keyframeStrip.getHeight => KeyframeAnimation.VerticalAnimation
    case width if width > keyframeStrip.getHeight => KeyframeAnimation.HorizontalAnimation
    case _ => throw new NotImplementedError("Source image for animation has same width and height")
  }

  /**
   * Causes the animation to step to the next keyframe.
   * @return The next keyframe as an image.
   */
  override def nextKeyframe(): BufferedImage = {
    currentKeyframeIndex += 1
    if (currentKeyframeIndex >= partitions.size) currentKeyframeIndex = 0
    partitions(currentKeyframeIndex)
  }

  /**
   * Causes the animation to step to the previous keyframe.
   * @return The previous keyframe as an image.
   */
  override def previousKeyframe(): BufferedImage = {
    currentKeyframeIndex -= 1
    if (currentKeyframeIndex < 0) currentKeyframeIndex = partitions.size - 1
    partitions(currentKeyframeIndex)
  }

  /**
   * Returns the image on which the animation currently is.
   * @return The current keyframe.
   */
  override def image = partitions(currentKeyframeIndex)

}

object KeyframeAnimation {
  private val HorizontalAnimation = "horizontal"
  private val VerticalAnimation = "vertical"
}

/**
  * Creates an image filled with one color.
  * @param startColor The color to fill the image with initially. Can be changed later through the color property.
  */
class SolidColor(startColor: Color) extends ImageLike {

  val color = Property(startColor)
  color.appendSetter { color =>
    require(color != null)
    cachedImage = newImage(color)
    color
  }

  private var cachedImage: BufferedImage = null

  private def newImage(withColor: Color): BufferedImage = {
    val img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    val g = img.createGraphics()
    g.setColor(withColor)
    g.fillRect(0, 0, 1, 1)
    g.dispose()
    img
  }

  override def image: BufferedImage = {
    if (cachedImage == null) cachedImage = newImage(color.get)
    cachedImage
  }
}

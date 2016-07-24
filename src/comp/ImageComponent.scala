package comp

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import general.JavaInterop._
import gui.image.{DefaultTextureUsage, TextureUsage}
import gui.screen.Screen

class ImageLikeComponent(x: Int, y: Int, imageLike: ImageLike, screen: Screen) extends Component(x, y, imageLike.image.getWidth, imageLike.image.getHeight, screen) {

  override def draw(g: Graphics2D): Unit = {
    imageLike.drawImage(g, getX, getY, getWidth, getHeight)
  }

}

/**
  * Component that just owns an image as a representation of itself.
  *
  * Upon transforming this component (especially rotating and scaling), it is crucial to know where the upper
  * left corner of this component actually is (because. The rotation/scale pivot point is normally at the __center__ of the
  * component and not at the upper left corner.
  *
  * Because of that, it is probably better to use the [[comp.Component#setCenteredLocation(int, int)]] method for
  * positioning this component instead.
  * This method makes sure that the __center of this component is actually placed on the specified location
  * rather than the upper left corner.__
  *
  * @param x The x position of the image.
  * @param y The y position of the image.
  * @param textureUsage The image to display.
  * @param screen The screen to bind the component to. Needed for listener implementation.
  */
class ImageComponent(x: Int, y: Int, val textureUsage: TextureUsage, screen: Screen) extends Component(x, y, textureUsage.textureBinding.getWidth, textureUsage.textureBinding.getHeight, screen) {
  
  def this(x: Int, y: Int, image: BufferedImage, screen: Screen) = {
    this(x, y, new DefaultTextureUsage(image), screen)
  }

  setSourceShape(textureUsage.resultingSourceShape)
  getTransformation.scale(textureUsage.scaleX, textureUsage.scaleY)
  setLocation(x, y)
  getTransformation.translate(textureUsage.offsetX, textureUsage.offsetY)

  def getBufferedImage = textureUsage.textureBinding

  override def draw(g: Graphics2D): Unit = {
    g.useMatrix(getTransformation.localConcatenatedMatrix) {
      val src_s = getSourceShape.getBounds
      g.drawImage(textureUsage.textureBinding, src_s.x, src_s.y, src_s.width, src_s.height, null)
    }
  }
}

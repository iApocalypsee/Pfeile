package gui.image

import java.awt.geom.{AffineTransform, Point2D}
import java.awt.image.BufferedImage
import java.awt.{Color, Rectangle, Shape}
import java.util

import scala.math.abs

import general.property.StaticProperty
import geom.functions.FunctionCollection.cycle

class TextureAtlas(initShape: Shape, baseImage: BufferedImage) {

  case class AtlasPoint(x: Int, y: Int) extends Comparable[AtlasPoint] {
    override def equals(obj: scala.Any) = obj match {
      case x: AtlasPoint => x.x == this.x && x.y == this.y
      case anythingElse => super.equals(anythingElse)
    }

    override def compareTo(o: AtlasPoint) = {
      if(this.hashCode() > o.hashCode()) 1
      else if(this.hashCode() == o.hashCode()) 0
      else -1
    }
  }

  val shape: Shape = {
    val loc = initShape.getBounds.getLocation
    val resultShape = if(loc.x != 0 || loc.y != 0) {
      val transform = AffineTransform.getTranslateInstance(abs(-loc.x), abs(-loc.y))
      transform.createTransformedShape(initShape)
    } else initShape
    resultShape
  }

  private val m_map = new util.TreeMap[AtlasPoint, BufferedImage]

  val positionCalculation = new StaticProperty[AtlasPoint => Point2D]

  def getTexture(x: Int, y: Int): BufferedImage = {
    val key = AtlasPoint(x, y)
    if(m_map.containsKey(key)) m_map.get(key)
    else {
      val generated = generateTexture(key)
      m_map.put(key, generated)
      generated
    }
  }

  private def generateTexture(point: AtlasPoint): BufferedImage = {
    val initCutPosition = positionCalculation.get(point)

    val image = new BufferedImage(shape.getBounds.width, shape.getBounds.height, BufferedImage.TYPE_INT_ARGB)
    val g = image.createGraphics()
    g.setColor(Color.white)
    g.fill(shape)

    val imageCutX = cycle(initCutPosition.getX.asInstanceOf[Int], image.getWidth, image.getWidth) % image.getWidth
    val imageCutY = cycle(initCutPosition.getY.asInstanceOf[Int], image.getHeight, image.getHeight) % image.getHeight

    val imageCut = new Rectangle(imageCutX, imageCutY, image.getWidth, image.getHeight)
    val genImageRaster = image.getRaster
    val baseImageData = baseImage.getData
    
    val initX = imageCut.x
    val initY = imageCut.y
    for(x <- initX until imageCut.x + imageCut.width;
        normalX = x % baseImage.getWidth;
        progressX = x - initX;
        y <- initY until imageCut.y + imageCut.height;
        normalY = y % baseImage.getHeight;
        progressY = y - initY) {
      val pixelSample = baseImageData.getPixel(normalX, normalY, null.asInstanceOf[Array[Int]])
      val genImagePixelData = genImageRaster.getPixel(progressX, progressY, null.asInstanceOf[Array[Int]])
      if(!(genImagePixelData(0) == 0 && genImagePixelData(1) == 0 && genImagePixelData(2) == 0 && genImagePixelData(3) == 0)) {
        genImageRaster.setPixel(progressX, progressY, pixelSample)
      }
    }
    
    image
  }

}

package gui.image

import java.awt.geom.{AffineTransform, Point2D}
import java.awt.image.BufferedImage
import java.awt.{Color, Rectangle, Shape}

import general.{ScalaUtil, LogFacility}
import gui.image.TextureAtlas.AtlasPoint
import geom.functions.FunctionCollection.cycle

import scala.collection.mutable
import scala.util.{Try, Success, Failure}

class TextureAtlas(baseImage: BufferedImage) {

  private val m_map = mutable.Map[AtlasPoint, BufferedImage]()

  /**
   * Gets the texture at the specified position.
   * @param x
   * @param y
   * @return
   */
  def getTexture(x: Int, y: Int, shape: Shape, cutPositionCalc: AtlasPoint => Point2D = null): Option[BufferedImage] = {
    val key = AtlasPoint(x, y)
    if(m_map.contains(key)) Option(m_map(key))
    else {
      val trial = generateTexture(key, shape, cutPositionCalc).map { texture =>
        m_map += key -> texture
        texture
      }
      trial.toOption
    }
  }

  private def defaultCutPosition(p: AtlasPoint, s: Shape) = new Point2D.Double(s.getBounds2D.getWidth * p.x, s.getBounds2D.getHeight * p.y)

  private def translateToOrigin(shape: Shape): Shape = {
    val locX = shape.getBounds2D.getX
    val locY = shape.getBounds2D.getY
    val resultShape = if(locX != 0 || locY != 0) {
      val transform = AffineTransform.getTranslateInstance(-locX, -locY)
      transform.createTransformedShape(shape)
    } else shape
    resultShape
  }

  private def generateTexture(point: AtlasPoint, anotherShape: Shape, cutPositionArg: AtlasPoint => Point2D): Try[BufferedImage] = {
    val cutPositionFun = if(cutPositionArg == null) defaultCutPosition(_: AtlasPoint, anotherShape) else cutPositionArg
    val initCutPosition = cutPositionFun(point)
    val shape = translateToOrigin(anotherShape)

    val genImage = new BufferedImage(shape.getBounds.width, shape.getBounds.height, BufferedImage.TYPE_INT_ARGB)
    val g = genImage.createGraphics()
    g.setColor(Color.white)
    g.fill(shape)

    val imageCutX = cycle(initCutPosition.getX.asInstanceOf[Int], baseImage.getWidth, baseImage.getWidth)
    val imageCutY = cycle(initCutPosition.getY.asInstanceOf[Int], baseImage.getHeight, baseImage.getHeight)

    val imageCut = new Rectangle(imageCutX, imageCutY, genImage.getWidth, genImage.getHeight)
    val genImageRaster = genImage.getRaster
    val baseImageData = baseImage.getData
    
    val initX = imageCut.x
    val initY = imageCut.y
    for(x <- initX until initX + imageCut.width;
        normalX = x % baseImage.getWidth;
        progressX = x - initX;
        y <- initY until initY + imageCut.height;
        normalY = y % baseImage.getHeight;
        progressY = y - initY) {

      // Mainly for debugging purposes.
      def commonVarTrace = ScalaUtil.varTrace(Map(
        "x" -> x,
        "y" -> y,
        "normalX" -> normalX,
        "normalY" -> normalY,
        "progressX" -> progressX,
        "progressY" -> progressY,
        "baseImage.width" -> baseImage.getWidth,
        "baseImage.height" -> baseImage.getHeight,
        "genImage.width" -> genImage.getWidth,
        "genImage.height" -> genImage.getHeight
      ))

      val pixelSample =
        try baseImageData.getPixel(normalX, normalY, null.asInstanceOf[Array[Int]])
        catch {
          case e: IndexOutOfBoundsException =>
            LogFacility.log(ScalaUtil.errorMessage("Index out of bounds for baseImageData!", e, commonVarTrace), "Error")
            return Failure(e)
        }

      val genImagePixelData =
        try genImageRaster.getPixel(progressX, progressY, null.asInstanceOf[Array[Int]])
        catch {
          case e: IndexOutOfBoundsException =>
            LogFacility.log(ScalaUtil.errorMessage("Index out of bounds for genImagePixelData!", e, commonVarTrace), "Error")
            return Failure(e)
        }

      if(!(genImagePixelData(0) == 0 && genImagePixelData(1) == 0 && genImagePixelData(2) == 0)) {
        genImageRaster.setPixel(progressX, progressY, pixelSample)
      }
    }
    
    Success(genImage)
  }

}

object TextureAtlas {
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
}

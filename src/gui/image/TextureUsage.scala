package gui.image

import java.awt.image.BufferedImage
import java.awt.{Point, Polygon, Shape}
import java.io.File

import scala.io.Source
import scala.reflect.runtime
import scala.reflect.runtime.currentMirror
import scala.util.Try

trait TextureUsage {

  private var m_scaleX = 1.0
  private var m_scaleY = 1.0
  private var m_offsetX = 0
  private var m_offsetY = 0
  private var m_sourceShapeVertices = Seq.empty[Point]

  def scaleX = m_scaleX
  def scaleY = m_scaleY
  def offsetX = m_offsetX
  def offsetY = m_offsetY
  def sourceShapeVertices = m_sourceShapeVertices

  protected def scaleX_=(x: Double) = m_scaleX = x
  protected def scaleY_=(x: Double) = m_scaleY = x
  protected def offsetX_=(x: Int) = m_offsetX = x
  protected def offsetY_=(x: Int) = m_offsetY = x
  protected def sourceShapeVertices_=(x: Seq[Point]) = m_sourceShapeVertices = x

  final def resultingSourceShape: Shape = sourceShapeVertices.foldLeft(new Polygon) { (c, e) =>
    c.addPoint(e.x, e.y)
    c
  }

  private var m_cachedTexture: BufferedImage = null

  def textureBinding: BufferedImage = {
    if(m_cachedTexture == null) m_cachedTexture = textureLoader
    m_cachedTexture
  }

  protected def textureLoader: BufferedImage

  final override def toString = super.toString

}
/*
object TextureUsage {

  private val toolbox = currentMirror.mkToolBox()

  private def commonRequirements(x: TextureUsage): TextureUsage = {
    require(x.sourceShapeVertices.size > 2, s"$x: Source shape must have at least 3 vertices")
    require(x.scaleX != 0.0 && x.scaleY != 0.0, s"$x: Texture may not be scaled by 0")
    x
  }

  def parseUsageDirect(x: File): TextureUsage = parseUsage(x).get

  def parseUsageDirect(x: String): TextureUsage = parseUsage(x).get

  def parseUsage(x: String): Try[TextureUsage] = Try(toolbox.eval(toolbox.parse(x)).asInstanceOf[TextureUsage]).map(commonRequirements)

  def parseUsage(x: File): Try[TextureUsage] = parseUsage(Source.fromFile(x).mkString)

}*/

class DefaultTextureUsage(image: BufferedImage) extends TextureUsage {

  override protected val textureLoader = image

  sourceShapeVertices = Seq(new Point(0, 0), new Point(0, textureBinding.getHeight), new Point(textureBinding.getWidth,
    textureBinding.getHeight), new Point(textureBinding.getWidth, 0))

}

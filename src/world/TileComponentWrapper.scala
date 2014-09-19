package world

import java.awt.{Color, Graphics2D}

import comp.Component
import gui.{AdjustableDrawing, GameScreen, Drawable}

import scala.collection.JavaConversions

class VisualMap(val tiles: List[TileComponentWrapper]) extends Drawable {

  private val _vp = new WorldViewport

  /** Constructs a visual map from a Java list. Interop method. */
  def this(t: java.util.List[TileComponentWrapper]) = this(JavaConversions.asScalaBuffer(t).toList)

  /** Returns the current shifting of the map in the x direction. */
  def getShiftY: Float = _vp.getShiftY
  /** Returns the current shifting of the map in the y direction. */
  def getShiftX: Float = _vp.getShiftX

  /** Moves the visual.
    *
    * @param shiftX The amount of x units to move the map.
    * @param shiftY The amount of y units to move the map.
    */
  def moveMap(shiftX: Float, shiftY: Float): Unit = {
    _vp.setShiftX(shiftX - _vp.getShiftX)
    _vp.setShiftY(shiftY - _vp.getShiftY)
    tiles foreach { c => c.component.setX(c.component.getX + shiftX.asInstanceOf[Int]) }
    tiles foreach { c => c.component.setY(c.component.getY + shiftY.asInstanceOf[Int]) }
  }

  /** Draws the whole map. */
  override def draw(g: Graphics2D): Unit = tiles foreach { c => c.component.draw(g) }
}

/** The tile trait should not know that it is surrounded by a component wrapper class.
  * This wrapper class is taking care of the visuals.
  *
  * @param tile The tile to keep.
  */
class TileComponentWrapper(val tile: TileLike) {

  val component = new Component with AdjustableDrawing {
    setBounds(tile.polygon)
    private lazy val f = {
      // Use the lazy initialization. I know, it's cheating.... :)
      setBackingScreen(GameScreen.getInstance())
      handle({ g => g.setColor(Color.GRAY); g.fillPolygon(getBounds) }, { isMouseFocused })
      tile.drawFunction
    }
    override def draw(g: Graphics2D) = {
      f(g)
      drawAll(g)
    }
  }

}

object TileComponentWrapper {
  /** Converts a [[TileLike]] object to a [[TileComponentWrapper]] object. */
  implicit def tile2ComponentTile(t: TileLike) = new TileComponentWrapper(t)
}

package world

import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt.geom.AffineTransform
import java.awt.{Polygon, Color, Graphics2D}

import comp.{ComponentWrapper, Component}
import general.{LogFacility, Main}
import gui.{AdjustableDrawing, GameScreen, Drawable}
import newent.VisionStatus

import scala.collection.JavaConversions

class VisualMap(world: WorldLike) extends Drawable {

  /** Data object that holds information on how to display the viewport. */
  private val _vp = new WorldViewport

  /** The color which is drawn on top of the tile when it is revealed (but not visible). */
  private val _revealedTileColor = new java.awt.Color(0.0f, 0.0f, 0.0f, 0.2f)

  /** Object that gives information on how to display the map.
    *
    * There are two options currently on displaying the map:
    * <ul>
    *   <li>The full map, thus ignoring every vision map.
    *   <li>The map according to the vision map of the active player.
    * </ul>
    */
  private var _sightType: SightType = VisionSightType

  /** The world that is being displayed currently. */
  private val _displayWorld = world

  /** Returns the current shifting of the map in the x direction. */
  def getShiftY: Float = _vp.getShiftY
  /** Returns the current shifting of the map in the y direction. */
  def getShiftX: Float = _vp.getShiftX

  def sightType = _sightType
  def getSightType = sightType
  def sightType_=(s: SightType) = {
    require(s ne null)
    _sightType = s
  }
  def setSightType(s: SightType) = this.sightType = s

  /** Moves the visual.
    *
    * @param shiftX The amount of x units to move the map.
    * @param shiftY The amount of y units to move the map.
    */
  def moveMap(shiftX: Float, shiftY: Float): Unit = {
    _vp.setShiftX(getShiftX + shiftX - getShiftX)
    _vp.setShiftY(getShiftY + shiftY - getShiftY)

    // Recalculate the position of every tile...
    _displayWorld.terrain.tiles foreach { c =>
      c.component.setX((c.component.getX + shiftX).asInstanceOf[Int])
      c.component.setY((c.component.getY + shiftY).asInstanceOf[Int])
    }
  }

  def zoom(factor: Float): Unit = {
    _vp.setZoom(factor)
    val transform = new AffineTransform
    transform.scale(factor, factor)

    // Recalculate the boundaries of every tile...
    _displayWorld.terrain.tiles foreach { t =>
      t.component.setWidth((t.component.getWidth * factor).asInstanceOf[Int])
      t.component.setHeight((t.component.getWidth * factor).asInstanceOf[Int])
    }
  }

  /** Draws the whole map. */
  override def draw(g: Graphics2D): Unit = {
    _sightType.draw(g)
  }

  /** Represents how the world is being drawn.
    *
    * A world can be draw in various ways; either the full map, or just parts of it.
    * Or even hide it completely (but that is not good).
    */
  sealed trait SightType {
    /** I do not want other classes to access the draw method. */
    protected[VisualMap] def draw(g: Graphics2D): Unit
  }

  /** Draws the map with the currently active player's vision map. */
  object VisionSightType extends SightType {
    protected[VisualMap] override def draw(g: Graphics2D): Unit = _displayWorld.terrain.tiles foreach { tile =>
      // Only draw the tile if the active player has actually revealed the tile.
      val status = Main.getContext.activePlayer.visionMap.visionStatusOf(tile.latticeX, tile.latticeY)

      if(status != VisionStatus.Hidden) {
        tile.component.draw(g)
        if(status == VisionStatus.Revealed) {
          g.setColor(_revealedTileColor)
          g.fill(tile.component.getBounds)
        }
        if(status == VisionStatus.Visible) {
          tile.entities foreach { e => e.component.draw(g) }
        }
      }
    }
  }

  /** Draws the map with no vision system included. So this is the full map. */
  object FullSightType extends SightType {
    protected[VisualMap] override def draw(g: Graphics2D): Unit = {
      _displayWorld.terrain.tiles foreach { tile =>
        tile.component.draw(g)
      }
    }
  }
}

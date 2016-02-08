package world

import java.awt.Graphics2D

import general.{Delegate, Main, PfeileContext}
import geom._
import geom.primitives.PrimitiveType
import gui.Drawable
import newent.{CommandTeam, VisionStatus}
import player.item.Loot
import player.weapon.AttackDrawer
import player.weapon.arrow.AbstractArrow

/**
  * Takes care of the drawing of the tiles in the given world.
  *
  * @param context The context on which this object operates.
  */
class VisualMap(context: PfeileContext) extends Drawable {

  context.turnSystem.onTurnGet.register("center map") {
    case team: CommandTeam => centerMap(team.head.getGridX, team.head.getGridY)
    case _ =>
  }

  /**
    * Called when the world GUI has been changed in some way, either through moving the map
    * or zooming.
    */
  val onWorldGuiChanged = Delegate.createZeroArity

  /** The color which is drawn on top of the tile when it is revealed (but not visible). */
  private val _revealedTileColor = new java.awt.Color(0.0f, 0.0f, 0.0f, 0.2f)

  /**
    * Object that gives information on how to display the map.
    *
    * There are two options currently on displaying the map:
    * <ul>
    *   <li>The full map, thus ignoring every vision map.
    *   <li>The map according to the vision map of the active player.
    * </ul>
    */
  private var _sightType: SightType = VisionSightType

  /** The world that is being displayed currently. */
  private val _displayWorld = context.world

  def getShift: Vector = primitives.cameraMatrix.getDisplacement

  def sightType = _sightType
  def getSightType = sightType
  def sightType_=(s: SightType) = {
    require(s ne null)
    _sightType = s
  }
  def setSightType(s: SightType) = this.sightType = s

  /**
    * Moves the visual.
    *
    * @param shiftX The amount of x units to move the map.
    * @param shiftY The amount of y units to move the map.
    */
  def moveMap(shiftX: Double, shiftY: Double): Unit = {
    moveMap(new Vector(shiftX, shiftY))
  }

  def moveMap(t: Vector): Unit = {
    primitives.cameraMatrix = AffineTransformation.translation(t) * primitives.cameraMatrix
  }

  /**
    * Sets the position of the whole map.
 *
    * @param x The new x position of the origin
    * @param y The new y position of the origin
    */
  def setMapPosition(x: Double, y: Double): Unit = {
    import scala.collection.JavaConversions._

    val shiftX = x - getShift.getX
    val shiftY = y - getShift.getY

    primitives.cameraMatrix = AffineTransformation.translation(new Vector(x, y))


    context.getWorldLootList.getLoots.foreach { loot: Loot =>
      loot.getLootUI.relocateGuiPosition()
    }

    // TODO: other weapons (AttackDrawer.getAttackingWeapons()) apart from Arrows need to be moved.
    AttackDrawer.getAttackingArrows.foreach { arrow: AbstractArrow =>
      arrow.getComponent.move(shiftX.asInstanceOf[Int], shiftY.asInstanceOf[Int])
    }

    onWorldGuiChanged()
  }

  def centerMap(tileX: Int, tileY: Int): Unit = {
    val centerOn = _displayWorld.terrain.tileAt(tileX, tileY)

    centerOn.component match {
      case isometric: IsometricPolygonTileComponent =>

        val center = Main.getGameWindow.getCenterPosition
        val tileNormalPosition = new Vector(isometric.normalX, isometric.normalY)
        val centeredUpperLeft = center - new Point(isometric.getWidth / 2, isometric.getHeight / 2)

        val relevantDelta = centeredUpperLeft - tileNormalPosition
        setMapPosition(relevantDelta.getX.asInstanceOf[Int], relevantDelta.getY.asInstanceOf[Int])

      // The visual map can only handle isometric tiles for now.
      case unknownComponent => throw new NotImplementedError(s"Component of tile is ${unknownComponent.getClass.getName}; " +
        s"Tile#IsometricPolygonTileComponent expected")
    }
  }

  def zoom(factor: Float): Unit = {
    primitives.projectionMatrix = primitives.projectionMatrix * factor
  }

  /** Draws the whole map. */
  override def draw(g: Graphics2D): Unit = {
    _sightType.draw(g)
  }

  /**
    * Represents how the world is being drawn.
    *
    * A world can be draw in various ways; either the full map, or just parts of it.
    * Or even hide it completely (but that is not good).
    */
  sealed trait SightType {
    /** I do not want other classes to access the draw method. */
    protected[VisualMap] def draw(g: Graphics2D): Unit

    def isFullSight = this == FullSightType
    def isVisionSight = !isFullSight
  }

  /** Draws the map with the currently active player's vision map and the visible loots in WorldLootList. */
  object VisionSightType extends SightType {

    private def drawTiles(g: Graphics2D) = {

      val terrain = _displayWorld.terrain
      val usedVision = context.activePlayer.visionMap

      for (
        y <- terrain.height - 1 to 0 by -1;
        x <- 0 until terrain.width;
        visionStatus = usedVision.visionStatusOf(x, y) if visionStatus != VisionStatus.Hidden
      ) {
        val tile = terrain.tileAt(x, y)
        tile.component.draw(primitives.graphics)

        if (visionStatus == VisionStatus.Revealed) {
          g.setColor(_revealedTileColor)
          g.fill(tile.component.getBounds)
          primitives.graphics.setColor(_revealedTileColor)
          primitives.worldMatrix = AffineTransformation.translation(tile.center - Point.origin)
          primitives.renderPrimitive(PrimitiveType.QUADS, tile.corners:_*)
        }
      }

    }

    private def drawEntities(g: Graphics2D) = {
      val terrain = _displayWorld.terrain
      val usedVision = context.activePlayer.visionMap

      for (
        y <- terrain.height - 1 to 0 by -1;
        x <- 0 until terrain.width;
        visionStatus = usedVision.visionStatusOf(x, y) if terrain.isTileValid(x, y) && visionStatus == VisionStatus.Visible;
        entity <- terrain.tileAt(x, y).entities
      ) {
        entity.component.draw(g)
      }
    }

    protected[VisualMap] override def draw(g: Graphics2D): Unit = {
      drawTiles(g)
      drawEntities(g)
      context.getWorldLootList.draw(g)
    }
  }

  /** Draws the map with no vision system included. So this is the full map with every Loot. */
  object FullSightType extends SightType {
    import scala.collection.JavaConversions._

    protected[VisualMap] override def draw(g: Graphics2D): Unit = {
      _displayWorld.terrain.tiles foreach { tile =>
        tile.component.drawChecked(g)
      }

      context.getWorldLootList.getLoots.foreach(loot =>
        loot.getLootUI.draw(g))
    }
  }
}

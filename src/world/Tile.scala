package world

import java.awt._
import java.util.{Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import comp.{Component, DisplayRepresentable}
import general.JavaInterop._
import general.property.StaticProperty
import gui.image.TextureAtlas
import newent.{AttackContainer, GameObject}
import player.weapon.arrow.{AbstractArrow, ImpactDrawerHandler}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.compat.java8.OptionConverters._

/**
  * Base trait for all tiles.
  */
abstract class Tile protected(gridX: Int, gridY: Int, val terrain: Terrain) extends DisplayRepresentable with AttackContainer {

  import world.Tile._

  require(terrain != null)

  // Add this particular class object to the tile type list if not in list yet
  Tile.m_tileTypeList += this.getClass

  /**
    * Returns the color that is used to represent the isometric tile.
    * It should be a generic color that can represent this tile in a proper way.
    */
  def color = textureAtlas.averageColor

  /**
    * The texture facility that the tile is using.
    */
  def textureAtlas: TextureAtlas

  /**
    * The properties of this tile.
    * The TileProperties class is expected to be expanded soon.
    */
  val tileProperties = new TileProperties

  //<editor-fold desc="Instance variables">

  val border = Border

  /**
    * Everything border-related comes in here.
    */
  object Border {

    /**
      * The stroke with which the borders are drawn.
      */
    val stroke = new StaticProperty(StandardDrawStroke)

    // All non-existent by default.
    val northEast = new StaticProperty[Color]
    val northWest = new StaticProperty[Color]
    val southWest = new StaticProperty[Color]
    val southEast = new StaticProperty[Color]

    /**
      * Sets every border color to specified color.
      *
      * @param x The color to paint the borders.
      */
    def paintLines(x: Color): Unit = for (colorProp <- borderColorProps) colorProp set x

    /**
      * Clears every color for every side of the border.
      */
    def clearLines(): Unit = for (colorProp <- borderColorProps) colorProp set null

    /**
      * Collection of every border property for easier manipulation of every color property.
      */
    private def borderColorProps = Seq(northEast, northWest, southWest, southEast)

  }

  //</editor-fold>

  //<editor-fold desc="Attack impact logic">

  onImpact += { e =>
    // Every entity that is an attack container and is standing on THIS tile
    // If there is any weapon the entity needs to feel the attack only on this file
    // If it is an arrow, the damageRadius is calculated with the "damageAt" method by RangedWeapon/AbstractArrow. Every Entity has to take the attack.

    val filteredEntityList = mutable.ListBuffer[GameObject]()

    if (e.weapon.isInstanceOf[AbstractArrow]) {

      // The attack impacts and it is an AbstractArrow, so we can register a new ImpactDrawer
      ImpactDrawerHandler.addImpactDrawer(e)

      this.takeImmediately(e)

      // Every entity has to take the attack. The damage system will eventually calculate 0 damage, but the list doesn't need to be filtered
      terrain.world.entities.entityList.foreach {
        case x: AttackContainer => x.takeImmediately(e)
      }

    } else {
      filteredEntityList ++= terrain.world.entities.helper.getEntitiesAt(this).asScala
    }

  }

  //</editor-fold>

  //<editor-fold desc="Directions (north, east, west, south, etc.)">

  private def directionalGet(xdiff: Int, ydiff: Int) = Option(terrain.tileAt(getGridX + xdiff, getGridY + ydiff))

  def west: Option[Tile] = directionalGet(-1, -1)
  def east: Option[Tile] = directionalGet(1, 1)
  def north: Option[Tile] = directionalGet(-1, 1)
  def south: Option[Tile] = directionalGet(1, -1)
  def northWest: Option[Tile] = directionalGet(-1, 0)
  def northEast: Option[Tile] = directionalGet(0, 1)
  def southWest: Option[Tile] = directionalGet(0, -1)
  def southEast: Option[Tile] = directionalGet(1, 0)

  def getWest = west.asJava
  def getEast = east.asJava
  def getNorth = north.asJava
  def getSouth = south.asJava
  def getNorthWest = northWest.asJava
  def getNorthEast = northEast.asJava
  def getSouthWest = southWest.asJava
  def getSouthEast = southEast.asJava

  /**
    * List with every neighboring tile. Not necessarily in order.
    */
  def neighbors: Seq[Tile] = Seq(north, northEast, east, southEast, south, southWest, west, northWest).flatten
  def getNeighbors: IList[Tile] = neighbors.asJava.toImmutableList

  //</editor-fold>

  //<editor-fold desc="Overrides">

  override val getGridY = gridY
  override val getGridX = gridX
  override protected def startComponent: Component = new IsometricPolygonTileComponent(this)

  //</editor-fold>

  /**
    * The movement points that are required to walk onto this tile.
    */
  def requiredMovementPoints: Int

  /**
    * Gets all game objects that are currently on this tile.
    */
  def entities: Seq[GameObject] = getEntities.asScala.toSeq
  def getEntities: IList[GameObject] = terrain.world.entities.helper.getEntitiesAt(this)

  override def toString = s"(x=$getGridX|y=$getGridY) - ${getClass.getName}"

}

object Tile {

  import scala.math._

  //<editor-fold desc="Tile type list">

  // Tile type list is used in world generation.
  // Every subclass of Tile puts a java.lang.Class
  // object of itself into this list.

  private val m_tileTypeList = mutable.Set[Class[_ <: Tile]]()

  def tileTypeList = m_tileTypeList.toList

  //</editor-fold>

  //<editor-fold desc="Visual appearance">

  lazy val TileHalfWidth = 90
  lazy val TileWidth = TileHalfWidth * 2
  lazy val TileHalfHeight = 45
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt(pow(TileHalfWidth, 2) + pow(TileHalfHeight, 2))

  @deprecated("Use Tile.TileShape instead")
  val ComponentShape = {
    val polygon = new Polygon
    polygon.addPoint(-TileHalfWidth, 0)
    polygon.addPoint(0, -TileHalfHeight)
    polygon.addPoint(TileHalfWidth, 0)
    polygon.addPoint(0, TileHalfHeight)
    polygon
  }

  val TileShape = IsometricTileRelativeShape(0.5, 0.5, 0.5, 0.5)

  /**
    * The color used when the mouse is pointing at the tile.
    */
  val DefaultMouseFocusColor = new Color(175, 175, 175, 50)

  /**
    * The color being used when the tile is on a path predicted by a pathfinder for an entity.
    */
  val MoveTargetColor = new Color(75, 75, 75, 125)

  /**
    * The line stroke used to clarify that the tile is lying on a certain path calculated
    * by the pathfinder.
    */
  val MoveTargetStroke = new BasicStroke(5)

  val StandardDrawStroke = new BasicStroke(2)

  //</editor-fold>

  case class IsometricTileAbsoluteShape private[Tile](north: Point, east: Point, south: Point, west: Point) {
    lazy val polygon: Polygon = {
      val polygon = new Polygon
      polygon.addPoint(north.x, north.y)
      polygon.addPoint(east.x, east.y)
      polygon.addPoint(south.x, south.y)
      polygon.addPoint(west.x, west.y)
      polygon
    }
  }

  case class IsometricTileRelativeShape private[Tile](topMove: Double, rightMove: Double, bottomMove: Double, leftMove: Double) {

    private def calculateInsets(dimension: Int, factor: Double): Int = (dimension * factor).asInstanceOf[Int]

    def construct(x: Int, y: Int, width: Int, height: Int): IsometricTileAbsoluteShape = {

      val topInset = calculateInsets(width, topMove)
      val rightInset = calculateInsets(height, rightMove)
      val bottomInset = calculateInsets(width, bottomMove)
      val leftInset = calculateInsets(height, leftMove)

      IsometricTileAbsoluteShape(
        new Point(x + topInset, y),
        new Point(x + width, y + rightInset),
        new Point(x + bottomInset, y + height),
        new Point(x, y + leftInset))
    }

  }

}

package world

import java.awt._

import comp.Component
import general.property.StaticProperty
import gui.image.TextureAtlas
import newent.{AttackContainer, GameObject}
import player.weapon.arrow.{AbstractArrow, ImpactDrawerHandler}

import scala.collection.mutable

object IsometricPolygonTile {

  import scala.math._

  //<editor-fold desc="Tile type list">

  // Tile type list is used in world generation.
  // Every subclass of IsometricPolygonTile puts a java.lang.Class
  // object of itself into this list.

  private[this] val _tileTypeList = mutable.MutableList[Class[_ <: IsometricPolygonTile]]()

  private def appendToTileTypeList(t: Class[_ <: IsometricPolygonTile]): Unit = {
    if (!_tileTypeList.contains(t)) _tileTypeList += t
  }

  def tileTypeList = _tileTypeList.toList

  //</editor-fold>

  //<editor-fold desc="Visual appearance">

  val TileHalfWidth = 60
  lazy val TileWidth = TileHalfWidth * 2
  val TileHalfHeight = 30
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt(pow(TileHalfWidth, 2) + pow(TileHalfHeight, 2))

  /*
  /**
   * Describes the ratio that the quotient `TileHalfWidth / TileHalfHeight` has to obey.
   */
  val TileDimensionRatio = 2

  assert(TileHalfWidth.asInstanceOf[Double] / TileHalfHeight == TileDimensionRatio, "Invalid ratio of tile width to tile height")
  */

  /*
  def verticalAngle = atan(TileHalfWidth / TileHalfHeight)
  def horizontalAngle = atan(TileHalfHeight / TileHalfWidth)

  println("verticalAngle = " + verticalAngle)
  println("horizontalAngle = " + horizontalAngle)

  */

  @deprecated("Use IsometricPolygonTile.TileShape instead")
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

  case class IsometricTileAbsoluteShape private[IsometricPolygonTile] (north: Point, east: Point, south: Point, west: Point) {
    lazy val polygon: Polygon = {
      val polygon = new Polygon
      polygon.addPoint(north.x, north.y)
      polygon.addPoint(east.x, east.y)
      polygon.addPoint(south.x, south.y)
      polygon.addPoint(west.x, west.y)
      polygon
    }
  }

  case class IsometricTileRelativeShape private[IsometricPolygonTile] (topMove: Double, rightMove: Double, bottomMove: Double, leftMove: Double) {

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

/**
  * A tile implementation that displays itself isometrically.
  *
  * The entities are not stored locally on the tile, instead they are retrieved from the EntityManager
  * instance. Why? Because I don't want to reference a tile from the entity.
  *
  * @param latticeX The x grid coordinate.
  * @param latticeY The y grid coordinate.
  * @param terrain The terrain to which the tile is linked.
  */
abstract class IsometricPolygonTile protected (
    @deprecatedName('initGridX) @deprecated("Use getGridX() instead.") override val latticeX: Int,
    @deprecatedName('initGridY) @deprecated("Use getGridY() instead.") override val latticeY: Int,
    override val terrain: DefaultTerrain) extends TileLike with OnDemandEntitiesStrategy {

  import world.IsometricPolygonTile._

  require(terrain != null)

  // Add this particular class object to the tile type list if not in list yet
  IsometricPolygonTile.appendToTileTypeList(this.getClass)

  /** Returns the color that is used to represent the isometric tile. */
  def color: Color

  /**
    * The texture facility that the tile is using.
    */
  def textureAtlas: TextureAtlas

  /**
    * Paints all border lines of the tile in the corresponding color.
    * @param x The color to paint the borders.
    */
  def paintBorderLines(x: Color): Unit = for (colorProp <- borderColorProps) colorProp set x

  def clearBorderLines(): Unit = for (colorProp <- borderColorProps) colorProp set null

  // TODO Remove tile properties object from IsometricPolygonTile, too generic
  val tileProperties = new TileProperties

  //<editor-fold desc="Instance variables">

  /**
    * The stroke with which the borders are drawn.
    */
  val borderStroke = new StaticProperty(StandardDrawStroke)

  // All black by default
  val northEastBorderColor = new StaticProperty[Color](null)
  val northWestBorderColor = new StaticProperty[Color](null)
  val southWestBorderColor = new StaticProperty[Color](null)
  val southEastBorderColor = new StaticProperty[Color](null)

  private def borderColorProps = Seq(northEastBorderColor, northWestBorderColor, southWestBorderColor, southEastBorderColor)

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

      // Every entity has to take the attack. The damage system will eventually calculate 0 damage, but the list doesn't need to be filtered
      terrain.world.entities.entityList.foreach {
        case x: AttackContainer => x.takeImmediately(e)
        case _ => takeImmediately(e)
      }

    }
    else {
      filteredEntityList ++= terrain.world.entities.entityList.filter {
        _.tileLocation == this
      }
    }

  }

  //</editor-fold>

  //<editor-fold desc="Directions (north, east, west, south, etc.)">

  private def directionalGet(xdiff: Int, ydiff: Int): Option[TileLike] = {
    if (terrain.isTileValid(latticeX + xdiff, latticeY + ydiff)) Some(terrain.tileAt(latticeX + xdiff, latticeY + ydiff))
    else None
  }

  override def north: Option[TileLike] = directionalGet(-1, 1)

  override def northEast: Option[TileLike] = directionalGet(0, 1)

  override def east: Option[TileLike] = directionalGet(1, 1)

  override def southEast: Option[TileLike] = directionalGet(1, 0)

  override def south: Option[TileLike] = directionalGet(1, -1)

  override def southWest: Option[TileLike] = directionalGet(0, -1)

  override def west: Option[TileLike] = directionalGet(-1, -1)

  override def northWest: Option[TileLike] = directionalGet(-1, 0)

  //</editor-fold>

  //<editor-fold desc="Overrides">

  /** Ditto. */
  override val getGridY = latticeY
  /** Ditto. */
  override val getGridX = latticeX

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent: Component = new IsometricPolygonTileComponent(this)

  //</editor-fold>

}

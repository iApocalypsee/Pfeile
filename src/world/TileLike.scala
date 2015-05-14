package world

import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}

import comp.{Component, DisplayRepresentable, RotationChange}
import general.Main
import general.property.{PropertyBase, StaticProperty}
import gui.AdjustableDrawing
import gui.screen.GameScreen
import newent.pathfinding.Path
import newent.{AttackContainer, EntityLike, MovableEntity}
import player.weapon.arrow.{AbstractArrow, ImpactDrawerHandler}

import scala.collection.{JavaConversions, mutable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Base trait for all tiles.
  *
  * I am knowingly not implementing [[comp.IComponent]] here, because if the
  * Component framework changes, it would be easier to adapt the changes with a base trait
  * that has not implemented any Component interface.
  * @author Josip Palavra
  */
trait TileLike extends AnyRef with DisplayRepresentable with AttackContainer {

  /** The x position in the grid of the world. */
  val latticeX: Int
  /** The y position in the grid of the world. */
  val latticeY: Int
  /** The terrain to which the tile belongs to. */
  val terrain: TerrainLike

  /** The movement points that are required to get on this tile. */
  def requiredMovementPoints: Int

  /** The tile located north of this tile. */
  def north: Option[TileLike]

  /** The tile located northeast of this tile. */
  def northEast: Option[TileLike]

  /** The tile located east of this tile. */
  def east: Option[TileLike]

  /** The tile located southeast of this tile. */
  def southEast: Option[TileLike]

  /** The tile located south of this tile. */
  def south: Option[TileLike]

  /** The tile located southwest of this tile. */
  def southWest: Option[TileLike]

  /** The tile located west of this tile. */
  def west: Option[TileLike]

  /** The tile located northwest of this tile. */
  def northWest: Option[TileLike]

  /** The neighbors the tile has. */
  def neighbors: Seq[TileLike] = Seq(north, northEast, east, southEast, south, southWest, west, northWest).flatten

  /** The entities that are currently on this tile. */
  def entities: Seq[EntityLike]

  /** Java interop method for the entity list. */
  def javaEntities = JavaConversions.seqAsJavaList(entities)

  override def toString = s"(x=$latticeX|y=$latticeY) - ${getClass.getName}"
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
abstract class IsometricPolygonTile protected (override val latticeX: Int,
    override val latticeY: Int,
    override val terrain: DefaultTerrain) extends TileLike with OnDemandEntitiesStrategy {

  import world.IsometricPolygonTile._

  require(terrain != null)

  // Add this particular class object to the tile type list if not in list yet
  IsometricPolygonTile.appendToTileTypeList(this.getClass)

  /** Returns the color that is used to represent the isometric tile. */
  def color: Color

  /**
    * Paints all border lines of the tile in the corresponding color.
    * @param x The color to paint the borders.
    */
  def paintBorderLines(x: Color): Unit = for (colorProp <- borderColorProps) colorProp set x

  def clearBorderLines(): Unit = for (colorProp <- borderColorProps) colorProp set null

  //<editor-fold desc='Instance variables'>

  /**
    * The color with which the tile object gets filled as the mouse points on it.
    */
  val mouseFocusColor = new StaticProperty(DefaultMouseFocusColor)

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

  //<editor-fold desc='Attack impact logic'>

  onImpact += { e =>
    // Every entity that is an attack container and is standing on THIS tile
    // If there is any weapon the entity needs to feel the attack only on this file
    // If it is an arrow, the damageRadius is calculated with the "damageAt" method by RangedWeapon/AbstractArrow. Every Entity has to take the attack.

    val filteredEntityList = mutable.ListBuffer[EntityLike]()

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

  //<editor-fold desc='Directions (north, east, west, south, etc.)'>

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

  //<editor-fold desc='Overrides'>

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
  override protected def startComponent: Component = new IsometricPolygonTileComponent

  //</editor-fold>

  /**
    * The component that is responsible for drawing the isometric shape of the tile.
    * Note that this component is not able to rotate! If you attempt a rotation, the component
    * will throw a UnsupportedOperationException, stating that rotation is not supported.
    */
  class IsometricPolygonTileComponent private[IsometricPolygonTile] extends Component with AdjustableDrawing {

    // Just make sure that the isometric tile is not being rotated.
    onTransformed += {
      case rotation: RotationChange => throw new UnsupportedOperationException("Rotation not supported on isometric tiles")
      case _ =>
    }

    //<editor-fold desc='Visual appearance'>

    /**
      * The shape from which the component is going to retrieve the corner points.
      * The corner points are needed to draw border lines on seperate sides.
      */
    private val cornerPoints = new {

      var north: Point = null
      var east: Point = null
      var south: Point = null
      var west: Point = null

      def use(x: IsometricTileAbsoluteShape) = {
        north = x.north
        east = x.east
        south = x.south
        west = x.west
      }

    }

    /**
      * The shape of the tile.
      *
      * On setting this property, the component is trying to calculate the new corner points of the isometric shape.
      */
    // Lazy because I am using this property currently somewhere before in the code...
    val tileShape = new StaticProperty(IsometricPolygonTile.TileShape) {
      override def staticSetter(x: IsometricPolygonTile.IsometricTileRelativeShape) = {
        cornerRecalculation(x, getX, getY, getWidth, getHeight)
        x
      }
    }

    private def cornerRecalculation(relativeShape: IsometricTileRelativeShape, x: Int, y: Int, width: Int, height: Int): Unit = {

      val absoluteShape = relativeShape.construct(x, y, width, height)
      cornerPoints.use(absoluteShape)

    }

    onTransformed += { _ =>
      cornerRecalculation(tileShape.get, getX, getY, getWidth, getHeight)
    }

    //</editor-fold>

    //<editor-fold desc='Initialization code'>

    setBackingScreen(GameScreen.getInstance())
    setSourceShape(tileShape.get.construct(-TileHalfWidth / 2, -TileHalfHeight / 2, TileWidth, TileHeight).polygon)
    setName((getGridX, getGridY).toString())

    // Translate so that the tile fits into the grid again.
    getTransformation.translate(normalX, normalY)

    //</editor-fold>

    //<editor-fold desc='Visual path prediction'>

    private var targeted = false
    private var predictedPath: Option[Seq[IsometricPolygonTileComponent]] = None

    addMouseListener(new MouseAdapter {
      override def mousePressed(e: MouseEvent) = if (e.getButton == MouseEvent.BUTTON3) {
        onGainedMoveTargetFocus()
      }

      override def mouseReleased(e: MouseEvent) = if (e.getButton == MouseEvent.BUTTON3) {
        onLostMoveTargetFocus()
      }

      override def mouseEntered(e: MouseEvent): Unit = if (e.getButton == MouseEvent.BUTTON3) {
        onGainedMoveTargetFocus()
      }

      override def mouseExited(e: MouseEvent): Unit = if (e.getButton == MouseEvent.BUTTON3) {
        onLostMoveTargetFocus()
      }
    })

    private def onLostMoveTargetFocus() = {
      predictedPath.foreach {
        for (x <- _) {
          x.targeted = false
        }
      }
    }

    /**
      * Executes when the tile is selected by an entity for being its next movement target.
      */
    private def onGainedMoveTargetFocus() = {
      // The entity to calculate the path for
      val entity = Main.getContext.entitySelection.selectedEntity

      /**
        * Actual prediction logic. This code is not written inside the future or match statement
        * on purpose.
        * @param x The path to predict the path to this tile for.
        * @return Ditto.
        */
      def predictWith(x: MovableEntity): Option[Path] = {
        x.pathfinderLogic.findPath(x, latticeX, latticeY)
      }

      def mapStep(step: Path.Step) = terrain.tileAt(step.x, step.y)

      val prediction = Future {
        entity match {
          case entity: MovableEntity => predictWith(entity)
          case _ => throw new RuntimeException("Current entity selection is no [[MovableEntity]]")
        }
      }

      prediction.map { pathOption =>

        val path: Path = pathOption getOrElse ???
        val tileSteps = path.steps.map(x => mapStep(x).component.asInstanceOf[IsometricPolygonTileComponent])

        for (tile <- tileSteps) {
          tile.targeted = true
        }

        predictedPath = Some(tileSteps)

      }
    }

    // Only draw a prediction if the tile is actually being targeted
    handle { g =>
      if (targeted) {
        g.setStroke(MoveTargetStroke)
        g.setColor(MoveTargetColor)
        g.draw(getBounds)
      }
    }

    //</editor-fold>

    //<editor-fold desc='Entity movement'>

    // When the user is clicking on the tile, the active player should move towards it.
    addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent): Unit = if (e.getButton == MouseEvent.BUTTON3) {
        Main.getContext.entitySelection.selectedEntity match {
          case move: MovableEntity => move.moveTowards(latticeX, latticeY)
          case _ => ???
        }
      }
    })

    // When the mouse moves over the tile, it should be marked in a grayish style.
    handle({ g => g.setColor(mouseFocusColor.get); g.fill(getBounds) }, { isMouseFocused })

    //</editor-fold>

    //<editor-fold desc='Border line drawing'>

    private def drawBorders(g: Graphics2D) = {
      val tile: IsometricPolygonTile = IsometricPolygonTile.this

      def drawRoutine(colorProperty: PropertyBase[Color], begin: Point, end: Point) = colorProperty ifdef { color =>
        g.setStroke(tile.borderStroke.get)
        g.setColor(color)
        g.drawLine(begin.x, begin.y, end.x, end.y)
      }

      drawRoutine(tile.northWestBorderColor, cornerPoints.west, cornerPoints.north)
      drawRoutine(tile.northEastBorderColor, cornerPoints.north, cornerPoints.east)
      drawRoutine(tile.southWestBorderColor, cornerPoints.west, cornerPoints.south)
      drawRoutine(tile.southEastBorderColor, cornerPoints.south, cornerPoints.east)
    }

    //</editor-fold>

    /**
      * Calculates the normal x position for this tile, considering that the center of the (0|0) tile
      * is at screen coordinates (0|0).
      * @return The x position of the upper left corner of this tile.
      */
    def normalX = getGridX * TileHalfWidth + getGridY * TileHalfWidth

    /**
      * Calculates the normal y position for this tile, considering that the center of the (0|0) tile
      * is at screen coordinates (0|0).
      * @return The y position of the upper left corner of this tile.
      */
    def normalY = getGridX * TileHalfHeight - getGridY * TileHalfHeight

    /**
      * Sets the position of this tile component to the normal position, to which the given translation is added.
      * @param leftCornerX How much x units to move this tile additionally.
      * @param leftCornerY How much y units to move this tile additionally.
      */
    def setPositionRelativeToMap(leftCornerX: Int, leftCornerY: Int): Unit = {
      setLocation(normalX + leftCornerX, normalY + leftCornerY)
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(Color.black)
      g.draw(getBounds)
      g.setColor(color)
      g.fill(getBounds)
      drawAll(g)
      drawBorders(g)
    }
  }

}

object IsometricPolygonTile {

  import scala.math._

  // Tile type list is used in world generation.
  // Every subclass of IsometricPolygonTile puts a java.lang.Class
  // object of itself into this list.
  //<editor-fold desc='Tile type list'>

  private[this] val _tileTypeList = mutable.MutableList[Class[_ <: IsometricPolygonTile]]()

  private def appendToTileTypeList(t: Class[_ <: IsometricPolygonTile]): Unit = {
    if (!_tileTypeList.contains(t)) _tileTypeList += t
  }

  def tileTypeList = _tileTypeList.toList

  //</editor-fold>

  //<editor-fold desc='Visual appearance'>

  val TileHalfWidth = 18 * 2
  lazy val TileWidth = TileHalfWidth * 2
  val TileHalfHeight = 10 * 2
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt(pow(TileHalfWidth, 2) + pow(TileHalfHeight, 2))

  @deprecated("Use IsometricPolygonTile.TileShape instead")
  lazy val ComponentShape = {
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

class GrassTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = GrassTile.TileColor

  override def requiredMovementPoints = 1
}

object GrassTile {
  val TileColor = new Color(0x1C9618)
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = SeaTile.TileColor

  override def requiredMovementPoints: Int = 10
}

object SeaTile {
  val TileColor = new Color(0x3555DB)
}

class CoastTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX, latticeY, terrain) {

  /** Returns the color that is used to represent the isometric tile. */
  override def color = CoastTile.TileColor

  /** The movement points that are required to get on this tile. */
  override def requiredMovementPoints = 10
}

object CoastTile {
  val TileColor = new Color(0x4865E0)
}

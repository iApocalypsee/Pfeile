package world

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.{BasicStroke, Color, Graphics2D, Polygon}

import comp.{Component, DisplayRepresentable}
import general.Main
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
trait TileLike extends DisplayRepresentable with AttackContainer {

  /** The x position in the grid of the world. */
  val latticeX: Int
  /** The y position in the grid of the world. */
  val latticeY: Int
  /** The terrain to which the tile belongs to. */
  val terrain: TerrainLike

  /** The movement points that are required to get on this tile. */
  def requiredMovementPoints: Int

  /** The tile located north of this tile. */
  def north: TileLike

  /** The tile located northeast of this tile. */
  def northEast: TileLike

  /** The tile located east of this tile. */
  def east: TileLike

  /** The tile located southeast of this tile. */
  def southEast: TileLike

  /** The tile located south of this tile. */
  def south: TileLike

  /** The tile located southwest of this tile. */
  def southWest: TileLike

  /** The tile located west of this tile. */
  def west: TileLike

  /** The tile located northwest of this tile. */
  def northWest: TileLike

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

  IsometricPolygonTile.appendToTileTypeList(this.getClass)

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

    // Splitting for easier use
    filteredEntityList.foreach {
      case x: AttackContainer => x.takeImmediately(e)
      case _ =>
    }
  }

  //</editor-fold>
  
  //<editor-fold desc='Directions (north, east, west, south, etc.)'>

  override def north: TileLike = terrain.tileAt(latticeX - 1, latticeY + 1)

  override def northEast: TileLike = terrain.tileAt(latticeX, latticeY + 1)

  override def east: TileLike = terrain.tileAt(latticeX + 1, latticeY + 1)

  override def southEast: TileLike = terrain.tileAt(latticeX + 1, latticeY)

  override def south: TileLike = terrain.tileAt(latticeX + 1, latticeY - 1)

  override def southWest: TileLike = terrain.tileAt(latticeX, latticeY - 1)

  override def west: TileLike = terrain.tileAt(latticeX - 1, latticeY - 1)

  override def northWest: TileLike = terrain.tileAt(latticeX - 1, latticeY)

  //</editor-fold>

  /** Ditto. */
  override val getGridY = latticeY
  /** Ditto. */
  override val getGridX = latticeX

  /** Returns the color that is used to represent the isometric tile. */
  def color: Color

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent: Component = new IsometricPolygonTileComponent

  private class IsometricPolygonTileComponent private[IsometricPolygonTile] extends Component with AdjustableDrawing {

    //<editor-fold desc='Initialization code'>
    setBackingScreen(GameScreen.getInstance())
    setSourceShape(IsometricPolygonTile.ComponentShape)

    // Translate so that the tile fits into the grid again.
    getTransformation.translate(latticeX * TileHalfWidth + latticeY * TileHalfWidth, latticeX * TileHalfHeight - latticeY * TileHalfHeight)
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
      predictedPath.map {
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
      override def mouseReleased(e: MouseEvent): Unit = if(e.getButton == MouseEvent.BUTTON3) {
        Main.getContext.entitySelection.selectedEntity match {
          case move: MovableEntity => move.moveTowards(latticeX, latticeY)
          case _ => ???
        }
      }
    })

    // When the mouse moves over the tile, it should be marked in a grayish style.
    handle({ g => g.setColor(MouseFocusColor); g.fill(getBounds) }, { isMouseFocused })
    
    //</editor-fold>

    override def draw(g: Graphics2D): Unit = {
      g.setColor(Color.black)
      g.draw(getBounds)
      g.setColor(color)
      g.fill(getBounds)
      drawAll(g)
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

  lazy val TileHalfWidth = 18
  lazy val TileWidth = TileHalfWidth * 2
  lazy val TileHalfHeight = 10
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt(pow(TileHalfWidth, 2) + pow(TileHalfHeight, 2))

  lazy val ComponentShape = {
    val polygon = new Polygon
    polygon.addPoint(-TileHalfWidth, 0)
    polygon.addPoint(0, -TileHalfHeight)
    polygon.addPoint(TileHalfWidth, 0)
    polygon.addPoint(0, TileHalfHeight)
    polygon
  }

  /**
   * The color used when the mouse is pointing at the tile.
   */
  lazy val MouseFocusColor = new Color(175, 175, 175, 50)

  /**
   * The color being used when the tile is on a path predicted by a pathfinder for an entity.
   */
  lazy val MoveTargetColor = new Color(75, 75, 75, 125)

  /**
   * The line stroke used to clarify that the tile is lying on a certain path calculated
   * by the pathfinder.
   */
  lazy val MoveTargetStroke = new BasicStroke(5)

  val StandardDrawStroke = new BasicStroke(7)
  
  //</editor-fold>

}

class GrassTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = GrassTile.TileColor

  override def requiredMovementPoints = 1
}

object GrassTile {

  lazy val TileColor = new Color(0x1C9618)
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = SeaTile.TileColor

  override def requiredMovementPoints: Int = 10
}

object SeaTile {

  lazy val TileColor = new Color(0x3555DB)
}

package world

import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt.{Color, Graphics2D, Polygon}

import comp.{Component, DisplayRepresentable}
import general.Main
import geom.PointDef
import gui.{GameScreen, AdjustableDrawing}
import misc.metadata.OverrideMetadatable
import newent.{AttackContainer, EntityLike}

import scala.collection.{JavaConversions, mutable}

/** Base trait for all tiles.
  *
  * I am knowingly not implementing [[comp.IComponent]] here, because if the
  * Component framework changes, it would be easier to adapt the changes with a base trait
  * that has not implemented any Component interface.
  * @author Josip Palavra
  */
trait TileLike extends DisplayRepresentable with OverrideMetadatable with AttackContainer {

  /** The x position in the grid of the world. */
  val latticeX: Int
  /** The y position in the grid of the world. */
  val latticeY: Int
  /** The terrain to which the tile belongs to. */
  val terrain: TerrainLike

  /** The (geographic) height of the tile. */
  var tileHeight: Int

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
  def javaEntities = JavaConversions.seqAsJavaList( entities )

  override def toString = s"(x=$latticeX|y=$latticeY) - ${getClass.getName}"
}

/** A tile implementation that displays itself isometrically.
  *
  * The entities are not stored locally on the tile, instead they are retrieved from the EntityManager
  * instance. Why? Because I don't want to reference a tile from the entity.
  *
  * @param latticeX The x grid coordinate.
  * @param latticeY The y grid coordinate.
  * @param terrain The terrain to which the tile is linked.
  */
abstract class IsometricPolygonTile protected(override val latticeX: Int,
                                              override val latticeY: Int,
                                              override val terrain: DefaultTerrain) extends TileLike with
                                                                                            OnDemandEntitiesStrategy {

  require( terrain ne null )

  IsometricPolygonTile.appendToTileTypeList( this.getClass )

  onImpact += { e =>
    // Every entity that is an attack container and is standing on THIS tile has to feel the attack...
    terrain.world.entities.entityList.filter {
      _.tileLocation == this
    }.foreach {
      case x: AttackContainer => x.takeImmediately( e )
      case _ =>
    }
  }

  override var tileHeight: Int = 0

  override def north: TileLike = terrain.tileAt( latticeX - 1, latticeY + 1 )

  override def northEast: TileLike = terrain.tileAt( latticeX, latticeY + 1 )

  override def east: TileLike = terrain.tileAt( latticeX + 1, latticeY + 1 )

  override def southEast: TileLike = terrain.tileAt( latticeX + 1, latticeY )

  override def south: TileLike = terrain.tileAt( latticeX + 1, latticeY - 1 )

  override def southWest: TileLike = terrain.tileAt( latticeX, latticeY - 1 )

  override def west: TileLike = terrain.tileAt( latticeX - 1, latticeY - 1 )

  override def northWest: TileLike = terrain.tileAt( latticeX - 1, latticeY )

  /** Ditto. */
  override val getGridY = latticeX
  /** Ditto. */
  override val getGridX = latticeY

  /** Returns the color that is used to represent the isometric tile. */
  def color: Color

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new Component with AdjustableDrawing {

    // Points that describe the corner points of the polygon.
    // It is easier to have 4 point objects instead of one polygon instance
    // from which I have to pull the data every time.
    private val _originalWest = new PointDef( 0, 0 )
    private val _originalSouth = new PointDef( 0, 0 )
    private val _originalEast = new PointDef( 0, 0 )
    private val _originalNorth = new PointDef( 0, 0 )

    // Make the call to the method here, because the initialization of the attributes above
    // has to finish before they get set to another value...
    recalculateOriginalPoints( )

    // When the mouse moves over the tile, it should be marked in a grayish style.
    handle({ g => g.setColor(Color.GRAY); g.fill(getBounds) }, { isMouseFocused })
    setBackingScreen(GameScreen.getInstance())

    // When the user is clicking on the tile, the active player should move towards it.
    addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent): Unit = {
        Main.getContext.activePlayer.moveTowards(latticeX, latticeY)
      }
    })

    /** Recalculates the original points for the isometric tile.
      *
      * The original points can change by moving the geometry of the map, so every time the map geometry
      * changes, this method should be called in order to keep visuals.
      */
    def recalculateOriginalPoints(): Unit = {
      import world.IsometricPolygonTile._

      // Why do I subtract for the y coordinate?
      // Because the screen coordinate system's y axis goes downwards, not upwards...
      _originalWest.setLocation( latticeX * TileHalfWidth + latticeY * TileHalfWidth,
        latticeX * TileHalfHeight - latticeY * TileHalfHeight )

      // Again the y coordinate system axis goes down, not up. In OpenGL it's the other way around...
      _originalNorth.setLocation( _originalWest.getX + TileHalfWidth, _originalWest.getY - TileHalfHeight )
      _originalEast.setLocation( _originalWest.getX + TileWidth, _originalWest.getY )
      _originalSouth.setLocation( _originalWest.getX + TileHalfWidth, _originalWest.getY + TileHalfHeight )

      // Discard the old polygon instance since it is no more...
      val _polygon = new Polygon
      _polygon.addPoint( _originalWest.getX.asInstanceOf[Int], _originalWest.getY.asInstanceOf[Int] )
      _polygon.addPoint( _originalSouth.getX.asInstanceOf[Int], _originalSouth.getY.asInstanceOf[Int] )
      _polygon.addPoint( _originalEast.getX.asInstanceOf[Int], _originalEast.getY.asInstanceOf[Int] )
      _polygon.addPoint( _originalNorth.getX.asInstanceOf[Int], _originalNorth.getY.asInstanceOf[Int] )

      setBounds(_polygon)
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(color)
      g.fill(getBounds)
      drawAll(g)
    }
  }


}

object IsometricPolygonTile {

  import scala.math._

  private[this] val _tileTypeList = mutable.MutableList[Class[_ <: IsometricPolygonTile]]( )

  private def appendToTileTypeList(t: Class[_ <: IsometricPolygonTile]): Unit = {
    if (!_tileTypeList.contains( t )) _tileTypeList += t
  }

  def tileTypeList = _tileTypeList.toList

  lazy val TileHalfWidth = 18
  lazy val TileWidth = TileHalfWidth * 2
  lazy val TileHalfHeight = 10
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt( pow( TileHalfWidth, 2 ) + pow( TileHalfHeight, 2 ) )

}

class GrassTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile( latticeX,
  latticeY, terrain ) {

  override def color = GrassTile.TileColor

  override def requiredMovementPoints = 1
}

object GrassTile {

  lazy val TileColor = new Color( 0x1C9618 )
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile( latticeX,
  latticeY, terrain ) {

  override def color = SeaTile.TileColor

  override def requiredMovementPoints: Int = 10
}

object SeaTile {

  lazy val TileColor = new Color( 0x3555DB )
}

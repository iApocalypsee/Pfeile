package world

import java.awt.{Color, Graphics2D, Polygon}

import geom.PointDef

/** Base trait for all tiles.
  *
  * I am knowingly not implementing [[comp.IComponent]] here, because if the
  * Component framework changes, it would be easier to adapt the changes with a base trait
  * that has not implemented any Component interface.
  * @author Josip Palavra
  */
trait TileLike {

  /** The x position in the grid of the world. */
  val latticeX: Int
  /** The y position in the grid of the world. */
  val latticeY: Int
  /** The terrain to which the tile belongs to. */
  val terrain: TerrainLike[_]

  var tileHeight: Int

  /** The polygon that represents the GUI boundaries of the tile. */
  def polygon: Polygon
  /** The draw function with which the tile is visualized on the display. */
  def drawFunction: (Graphics2D) => Unit

  def requiredMovementPoints: Int

  def north: TileLike
  def northEast: TileLike
  def east: TileLike
  def southEast: TileLike
  def south: TileLike
  def southWest: TileLike
  def west: TileLike
  def northWest: TileLike

}

abstract class IsometricPolygonTile protected(override val latticeX: Int,
                                              override val latticeY: Int,
                                              override val terrain: DefaultTerrain) extends TileLike {



  require( terrain ne null )

  override var tileHeight: Int = 0

  private val _originalWest = new PointDef( 0, 0 )
  private val _originalSouth = new PointDef( 0, 0 )
  private val _originalEast = new PointDef( 0, 0 )
  private val _originalNorth = new PointDef( 0, 0 )

  private var _polygon: Polygon = null
  override def polygon = _polygon

  // Make the call to the method here, because the initialization of the attributes above
  // has to finish before they get set to another value...
  recalculateOriginalPoints()

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
    _polygon = new Polygon
    _polygon.addPoint( _originalWest.getX.asInstanceOf[Int], _originalWest.getY.asInstanceOf[Int] )
    _polygon.addPoint( _originalSouth.getX.asInstanceOf[Int], _originalSouth.getY.asInstanceOf[Int] )
    _polygon.addPoint( _originalEast.getX.asInstanceOf[Int], _originalEast.getY.asInstanceOf[Int] )
    _polygon.addPoint( _originalNorth.getX.asInstanceOf[Int], _originalNorth.getY.asInstanceOf[Int] )
  }

  /** The draw function with which the tile is visualized on the display. */
  override def drawFunction = { g =>
    g.setColor(color)
    g.fillPolygon(polygon)
  }

  override def north: TileLike = terrain.tileAt( latticeX - 1, latticeY + 1 )
  override def northEast: TileLike = terrain.tileAt( latticeX, latticeY + 1 )
  override def east: TileLike = terrain.tileAt( latticeX + 1, latticeY + 1 )
  override def southEast: TileLike = terrain.tileAt( latticeX + 1, latticeY )
  override def south: TileLike = terrain.tileAt( latticeX + 1, latticeY - 1 )
  override def southWest: TileLike = terrain.tileAt( latticeX, latticeY - 1 )
  override def west: TileLike = terrain.tileAt( latticeX - 1, latticeY - 1 )
  override def northWest: TileLike = terrain.tileAt( latticeX - 1, latticeY )

  def color: Color
}

class GrassTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX, latticeY, terrain) {

  override def color = GrassTile.TileColor

  override def requiredMovementPoints = 1
}

object GrassTile {
  val TileColor = Color.GREEN
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX, latticeY, terrain) {

  override def color = Color.BLUE

  override def requiredMovementPoints: Int = 2
}

object IsometricPolygonTile {

  import scala.math._

  lazy val TileHalfWidth = 18
  lazy val TileWidth = TileHalfWidth * 2
  lazy val TileHalfHeight = 10
  lazy val TileHeight = TileHalfHeight * 2
  lazy val TileDiagonalLength = sqrt( pow( TileHalfWidth, 2 ) + pow( TileHalfHeight, 2 ) )

}

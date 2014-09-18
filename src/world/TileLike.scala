package world

import java.awt.Polygon

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
  val terrain: TerrainLike

  def requiredMovementPoints: Int
  def tileHeight: Int

  def north: TileLike
  def northEast: TileLike
  def east: TileLike
  def southEast: TileLike
  def south: TileLike
  def southWest: TileLike
  def west: TileLike
  def northWest: TileLike

}

trait IsometricPolygonTile extends TileLike {

  private var _wp: PointDef = null
  private var _sp: PointDef = null
  private var _ep: PointDef = null
  private var _np: PointDef = null

  protected def recalculateCorners(): Unit = {

  }

  override def north: TileLike = terrain.tileAt(latticeX - 1, latticeY + 1)
  override def northEast: TileLike = terrain.tileAt(latticeX, latticeY + 1)
  override def east: TileLike = terrain.tileAt(latticeX + 1, latticeY + 1)
  override def southEast: TileLike = terrain.tileAt(latticeX + 1, latticeY)
  override def south: TileLike = terrain.tileAt(latticeX + 1, latticeY - 1)
  override def southWest: TileLike = terrain.tileAt(latticeX, latticeY - 1)
  override def west: TileLike = terrain.tileAt(latticeX - 1, latticeY - 1)
  override def northWest: TileLike = terrain.tileAt(latticeX - 1, latticeY)
}

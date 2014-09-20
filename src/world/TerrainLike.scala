package world

import scala.util.Random

/** Base trait for all terrain types.
  *
  * With a need to support extensibility, we need a base "interface" so that we can support terrains
  * that are implemented differently.
  * @author Josip Palavra
  */
trait TerrainLike {

  /** The type of tile that the terrain is managing. */
  type TileType <: TileLike

  /** The width dimension of the terrain. */
  def width: Int
  /** The height dimension of the terrain. */
  def height: Int

  require(width > 0, "Width may not be negative or 0.")
  require(height > 0, "Height may not be negative or 0.")

  /** Returns true if the specified coordinate is valid.
    *
    * @param x The x coordinate.
    * @param y The y coordinate.
    * @return A boolean value.
    */
  def isTileValid(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height

  /** Returns the tile at the given coordinate.
    *
    * It is implementation-specific how terrains are coping with coordinates that are out of bounds.
    * Some may throw an exception, others just return null. Like I said, implementation-specific.
    *
    * @param x The x coordinate.
    * @param y The y coordinate.
    * @return The tile at the specified coordinate.
    */
  def tileAt(x: Int, y: Int): TileType

}

/** The default terrain (for now).
  *
  * The terrain manages isometric tiles and has no references to the [[comp.Component]] class
  * or whatsoever GUI class we have.
  *
  */
class DefaultTerrain extends TerrainLike {

  override type TileType = IsometricPolygonTile

  // Wow. The generation process is so straightforward.
  private val _primitiveRandom = new Random
  // The tiles are managed in a (multidimensional) array, NOT in a scala collection class.
  private val _tiles = Array.tabulate(width, height) { (x, y) =>
    _primitiveRandom.nextInt(2) match {
      case 0 => new GrassTile(x, y, DefaultTerrain.this)
      case 1 => new SeaTile(x, y, DefaultTerrain.this)
      case _ => null
    }
  }

  override def width = 20
  override def height = 20

  @throws[ArrayIndexOutOfBoundsException]("Tile is not specified.")
  override def tileAt(x: Int, y: Int) = {
    require(isTileValid(x, y))
    _tiles(x)(y)
  }
}

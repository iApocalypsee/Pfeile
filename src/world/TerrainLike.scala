package world

import scala.util.Random

/**
 *
 * @author Josip Palavra
 */
trait TerrainLike[A <: TileLike] {

  def width: Int
  def height: Int

  require(width > 0, "Width may not be negative or 0.")
  require(height > 0, "Height may not be negative or 0.")

  def isTileValid(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height
  def tileAt(x: Int, y: Int): A

}

class DefaultTerrain extends TerrainLike[IsometricPolygonTile] {

  private val _primitiveRandom = new Random
  private val _tiles = Array.tabulate(width, height) { (x, y) =>
    _primitiveRandom.nextInt(2) match {
      case 0 => new GrassTile(x, y, DefaultTerrain.this)
      case 1 => new SeaTile(x, y, DefaultTerrain.this)
      case _ => null
    }
  }

  override def width = 50
  override def height = 50

  override def tileAt(x: Int, y: Int) = {
    require(isTileValid(x, y))
    _tiles(x)(y)
  }
}

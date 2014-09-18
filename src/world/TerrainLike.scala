package world

/**
 *
 * @author Josip Palavra
 */
trait TerrainLike {

  def width: Int
  def height: Int

  def isTileValid(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height
  def tileAt(x: Int, y: Int): TileLike

}

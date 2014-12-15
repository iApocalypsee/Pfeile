package world.brush

import world.{TerrainLike, TileLike}

/**
 *
 * @author Josip Palavra
 */
trait BrushLike {

  type LinkedTileType <: TileLike

  private var _radius = 3

  def radius = _radius
  def radius_=(a: Int) = {
    require(a > 0)
    _radius = a
  }

  def applyBrush(t: TerrainLike, x: Int, y: Int): Unit

}

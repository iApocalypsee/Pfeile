package world.brush

import world.{DefaultTerrain, TerrainLike, Tile}

/** Brush for setting the tile type of a position.
  *
  * This class is designed <b>only for the DefaultTerrain and Tile classes</b>,
  * so it is not generically adjustable. It is bound to the constructors of Tile.
  */
class TileTypeBrush extends BrushLike {

  private var _type: Class[_ <: Tile] = null

  def tileType = _type
  def tileType_=(t: Class[_ <: Tile]) = _type = t

  override protected def applySideEffects(terrain: TerrainLike, x: Int, y: Int, centerX: Int, centerY: Int): Unit = {
    val t = terrain.asInstanceOf[DefaultTerrain]
    t.setTileAt(x, y, _type.getConstructors.apply(0).newInstance(new Integer(x), new Integer(y), t).asInstanceOf[t.TileType])
  }

}

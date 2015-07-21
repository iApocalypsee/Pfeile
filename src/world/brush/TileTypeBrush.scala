package world.brush

import world.{DefaultTerrain, IsometricPolygonTile, TerrainLike}

/** Brush for setting the tile type of a position.
  *
  * This class is designed <b>only for the DefaultTerrain and IsometricPolygonTile classes</b>,
  * so it is not generically adjustable. It is bound to the constructors of IsometricPolygonTile.
  */
class TileTypeBrush extends BrushLike {

  private var _type: Class[_ <: IsometricPolygonTile] = null

  def tileType = _type
  def tileType_=(t: Class[_ <: IsometricPolygonTile]) = _type = t

  override protected def applySideEffects(terrain: TerrainLike, x: Int, y: Int, centerX: Int, centerY: Int): Unit = {
    val t = terrain.asInstanceOf[DefaultTerrain]
    t.setTileAt(x, y, _type.getConstructors.apply(0).newInstance(new Integer(x), new Integer(y), t).asInstanceOf[t.TileType])
  }

}

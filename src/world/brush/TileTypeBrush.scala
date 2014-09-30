package world.brush

import comp.Circle
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

  override def applyBrush(terrain: TerrainLike, x: Int, y: Int): Unit = {

    require(terrain.isInstanceOf[DefaultTerrain])
    val t = terrain.asInstanceOf[DefaultTerrain]

    val circle = new Circle
    circle.setRadius(radius)
    circle.setX(x)
    circle.setY(y)

    for(y_tile <- 0 until t.height) {
      for(x_tile <- 0 until t.width) {
        if(circle.contains(x_tile, y_tile)) {
          t.setTileAt(x_tile, y_tile, _type.getConstructors.apply(0).newInstance(new Integer(x_tile), new Integer(y_tile), t).asInstanceOf[t.TileType])
        }
      }
    }

  }

}

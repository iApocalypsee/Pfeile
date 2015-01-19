package world

import geom.Vector2

/**
  * Helper functions for calculating the corner points of the isometric polygon.
  * Work-in-progress yet.
  * @param tile The tile to calculate the corner points from.
  */
class IsometricTileShape private (val tile: IsometricPolygonTile) {

  def upperMiddle: Vector2 = {
    val x = tile.component.getTransformation.translation.x
    val y = tile.component.getY
    Vector2(x, y)
  }

  def lowerMiddle: Vector2 = {
    val x = tile.component.getTransformation.translation.x
    val y = tile.component.getY + tile.component.getSourceShape.getBounds.height
    Vector2(x, y)
  }

  // TODO Left and right corners. Do that when tile heights are implemented.

}

/**
  * Use as a factory for the [[world.IsometricTileShape]] class.
  */
object IsometricTileShape {

  def shapeFor(x: IsometricPolygonTile) = new IsometricTileShape(x)

}

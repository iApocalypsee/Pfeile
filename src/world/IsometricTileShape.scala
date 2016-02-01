package world

import geom.Vector

/**
  * Helper functions for calculating the corner points of the isometric polygon.
  * Work-in-progress yet.
 *
  * @param tile The tile to calculate the corner points from.
  */
class IsometricTileShape private (val tile: Tile) {

  def upperMiddle: Vector = {
    val x = tile.component.getTransformation.translation.getX
    val y = tile.component.getY
    new Vector(x, y)
  }

  def lowerMiddle: Vector = {
    val x = tile.component.getTransformation.translation.getX
    val y = tile.component.getY + tile.component.getSourceShape.getBounds.height
    new Vector(x, y)
  }

  // TODO Left and right corners. Do that when tile heights are implemented.

}

/**
  * Use as a factory for the [[world.IsometricTileShape]] class.
  */
object IsometricTileShape {

  def shapeFor(x: Tile) = new IsometricTileShape(x)

}

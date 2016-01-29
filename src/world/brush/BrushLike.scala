package world.brush

import comp.Circle
import world.Terrain

/**
 *
 * @author Josip Palavra
 */
trait BrushLike {

  private var _radius = 3

  def radius = _radius
  def radius_=(a: Int) = {
    require(a > 0)
    _radius = a
  }

  def applyBrush(t: Terrain, x: Int, y: Int): Unit = {
    val circle = new Circle
    circle.setRadius(radius)
    circle.setX(x)
    circle.setY(y)

    for(y_tile <- 0 until t.height) {
      for(x_tile <- 0 until t.width) {
        if(circle.contains(x_tile, y_tile)) {
          applySideEffects(t, x_tile, y_tile, x, y)
        }
      }
    }
  }

  protected def applySideEffects(t: Terrain, x: Int, y: Int, centerX: Int, centerY: Int): Unit

}

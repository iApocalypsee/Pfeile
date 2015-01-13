package geom.functions

import geom.Vector2

/**
 * Base trait for classes that can be Bezier-like objects.
 */
trait BezierLike {

  def pointWith(t: Float): Vector2

}

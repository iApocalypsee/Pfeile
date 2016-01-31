package geom.functions

import geom.Vector2

/**
 * Base trait for classes that can be Bezier-like objects.
 */
@deprecated("", "2016-01-29")
trait BezierLike {

  def pointWith(t: Float): Vector2

}

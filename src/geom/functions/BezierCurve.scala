package geom.functions

import java.awt.geom.CubicCurve2D

import geom.Vector2

/**
  * Implementation of a cubic Beziér curve.
  *
  * These curves are capable of replacing our (um...)
  * FunctionCollectionEasingTripleQuadraticInOutAndThenAgainOnlyInBlablabla
  * (too complicated for me to express that...) stuff if used correctly.
  * What I want to say with this is that Beziér curves can imitate complicated functions.
  * Use them, you can model Beziér curves in a program like GIMP.
  *
  * <a href="http://devmag.org.za/2011/04/05/bzier-curves-a-tutorial/">A good source for learning how Beziér curves
  * work.</a>
  *
  * @param startPoint The point where the curve starts.
  * @param c1 The first control point.
  * @param c2 The second control point.
  * @param endPoint The point where the curve ends.
  */
@deprecated("", "2016-01-29")
case class BezierCurve(startPoint: Vector2, c1: Vector2, c2: Vector2, endPoint: Vector2) {

  def pointWith(t: Float): Vector2 = {
    val u = 1 - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t

    var p = startPoint * uuu
    p = p.+(c1.*(3 * uu * t))
    p = p.+(c2.*(3 * u * tt))
    p = p.+(endPoint * ttt)
    p
  }

  def pointWith(t: Double): Vector2 = pointWith(t.asInstanceOf[Float])

}

@deprecated("", "2016-01-29")
object BezierCurve {

  implicit def augmentCubicCurve(cubicCurve: CubicCurve2D): BezierCurve = BezierCurve(Vector2(cubicCurve.getP1),
    Vector2(cubicCurve.getCtrlP1), Vector2(cubicCurve.getCtrlP2), Vector2(cubicCurve.getP2))

  def linear(p1: Vector2, p2: Vector2) = BezierCurve(p1, p1, p2, p2)

}

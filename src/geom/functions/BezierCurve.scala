package geom.functions

import java.awt.geom.CubicCurve2D

import geom.Point

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
case class BezierCurve(startPoint: Point, c1: Point, c2: Point, endPoint: Point) {

	//B(t) = (1 - t)^3 P0 + 3 t (1 - t)^2 P1 + 3 t^2 (1 - t) P2 + t^3 P3;   u := P1 - P0; v := P2 - P0;  w := P3 - P0
	//B(t) = (1 - t)^3 P0 + 3 t (1 - t)^2 (u + P0) + 3 t^2 (1 - t) (v + P0) + t^3 (w + P0)
	//B(t) = (1 - t)^3 P0 + 3 t (1 - t)^2 u + 3 t (1 - t)^2 P0 + 3 t^2 (1 - t) v + 3 t^2 (1 - t) P0 + t^3 w + t^3 P0
	//B(t) = P0 ((1 - t)^3 + 3 t (1 - t)^2 + 3 t^2 (1 - t) + t^3) + 3 t (1 - t)^2 u + 3 t^2 (1 - t) v + t^3 w
	//B(t) = P0 + 3 t (1 - t)^2 u + 3 t^2 (1 - t) v + t^3 w
  def pointWith(t: Double): Point = {
		val u = c1 - startPoint
		val v = c2 - startPoint
		val w = endPoint - startPoint
		val a = 3 * t * math.pow(1 - t, 2)
		val b = 3 * math.pow(t, 2) * (1 - t)
		val c = math.pow(t, 3)
		startPoint + u * a + v * b + w * c
	}
}

@deprecated("", "2016-01-29")
object BezierCurve {

  implicit def augmentCubicCurve(cubicCurve: CubicCurve2D): BezierCurve =
    BezierCurve(new Point(cubicCurve.getP1), new Point(cubicCurve.getCtrlP2), new Point(cubicCurve.getCtrlP2),
			new Point(cubicCurve.getP2))

  def linear(p1: Point, p2: Point) = BezierCurve(p1, p1, p2, p2)

}

package geom

/**
 *
 * @author Josip Palavra
 */
case class BezierCurve(c0: Vector2, c1: Vector2, c2: Vector2, c3: Vector2) {

  def pointWith(t: Float): Vector2 = {
    val u = 1 - t
    val tt = t * t
    val uu = u * u
    val uuu = uu * u
    val ttt = tt * t

    var p = c0 * uuu
    p = p.+(c1.*(3 * uu * t))
    p = p.+(c2.*(3 * u * tt))
    p = p.+(c3 * ttt)
    p
  }

}

package geom.functions

/** A nice collection of unconventional functions.
  *
  * A good source for such inconvenient functions is http://gizma.com/easing/#cub2.
  */
object FunctionCollection {

  /** Applies the cubic function to the given parameters.
    *
    * @param t_arg Current time.
    * @param b_arg The start value.
    * @param c_arg Delta.
    * @param d_arg Duration.
    * @return A totally crazy abnormal value which is out of my bounds of thinking.
    */
  override def cubic_easing_inout(t_arg: Double, b_arg: Double, c_arg: Double, d_arg: Double): Double = {
    var t = t_arg
    var b = b_arg
    var c = c_arg
    var d = d_arg
    t /= d/2
    if (t < 1) return c/2*t*t*t + b
    t -= 2
    return c/2*(t*t*t + 2) + b
  }

}



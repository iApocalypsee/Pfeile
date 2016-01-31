package geom.functions

/** A nice collection of unconventional functions.
  *
  * A good source for such inconvenient functions is http://gizma.com/easing/#cub2.
  */
object FunctionCollectionEasing {

  /** Applies the cubic function to the given parameters.
    * <b>acceleration until halfway, then deceleration </b><p>
    *
    * <b>Usage</b><p>
    * Let <code>b = 2.0; c = 2.5; d = 100.0</code>
    * Notice for calculation:
    * {{{
    *   cubic_easing_inout(0, b, c, d) = b
    *   cubic_easing_inout(d, b, c, d) = b + c
    *
    *             if x < 1 --------------> (c/2) * t^3 + b       = (c/2)x^3 + b
    *   cei(x) =
    *             else ------------------> (c/2) * (t^3 + 2) + b = (c/2)x^3 + b + c
    * }}}
    *
    * @param t current x value, as in <code>f(x)</code>.
    * @param b Start value <b>and</b> first summand of the end value.
    * @param c Second summand of the end value. (the change in value)
    * @param d Precision of the graph. (duration of x)
    * @return Not so crazy anymore, just a higher-order function.
    */
  def cubic_easing_inout(t: Double, b: Double, c: Double, d: Double): Double = {
    var _t = t
    //_t /= d / 2
    _t = _t / (d / 2)
    if (_t < 1) return c / 2 * _t * _t * _t + b
    _t -= 2
    return c / 2 * (_t * _t * _t + 2) + b
  }

   /** Applies the quadratic function to the given parameters: acceleration until halfway, then deceleration
     *
     * @param t_current Current time/frame (this is the current x-value and the only value that changes)
     * @param b The start value in y-direction [constant]
     * @param c The change in value in y-direction [constant] (if b=0 --> c is the end value in y-direction)
     * @param d The Duration (the end x-value is the first t_current + d)
     * @return the current Y-value of this curve
     */
   def  quadratic_easing_inOut(t_current: Double, b: Double, c: Double, d: Double): Double = {
      var t = t_current
      t /= d/2
      if (t < 1) return c/2*t*t + b
      t = t-1
      return -c/2 * (t*(t-2) - 1) + b
   }
}

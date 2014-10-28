package geom.functions

/** A nice collection of unconventional functions.
  *
  * A good source for such inconvenient functions is http://gizma.com/easing/#cub2.
  */
object FunctionCollection {

  /** Applies the cubic function to the given parameters. notice: cei(0) = b
		and cei(d) = b + c
    * <b>acceleration until halfway, then deceleration </b>
    *
    * @param t_arg Current time. (the current x-value)
    * @param b The start value. [cnstant] (the start y-value)
    * @param c Delta (change in value) [constant] (the end y-value, depending on the start-y-value; if b=0, then c=endValueOfY
    * @param d Duration. [constant] (the end x-value is the first t_current + d): percusion of calculation
    * @return A totally crazy abnormal value which is out of my bounds of thinking.
    */
  def cubic_easing_inOut(t_arg: Double, b: Double, c: Double, d: Double): Double = {
    var t = t_arg
    t /= d/2
    if (t < 1) return c/2*t*t*t + b
    t -= 2
    return c/2*(t*t*t + 2) + b
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



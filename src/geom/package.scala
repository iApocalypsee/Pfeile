
package object geom {

    val Epsilon = 1e-9

  /**
    * Basically the same as `Epsilon`, just with a fancy identifier.
    */
  val ε = Epsilon

    def isEqual(x: Double, y: Double) = isZero(x - y)
  def isZero(x: Double) = math.abs(x) < ε

  /**
    * Returns the input argument if it cannot be considered as zero, else the literal "zero".
    * @param x The number to "zero" out if too small.
    */
  def zeroed(x: Double) = if(isZero(x)) 0.0 else x

}

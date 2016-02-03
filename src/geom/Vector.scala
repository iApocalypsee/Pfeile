package geom

import geom.functions.FunctionCollection

class Vector(xc: Double, yc: Double) {
  private val x: Double = xc
  private val y: Double = yc

  def getX = x
  def getY = y

  def +(other: Vector) = new Vector(x + other.getX, y + other.getY)
  def +(other: Point) = new Point(x + other.getX, y + other.getY)
  def -(other: Vector) = new Vector(x - other.getX, y - other.getY)
  def *(factor: Double) = new Vector(x * factor, y * factor);
  def /(factor: Double) = new Vector(x / factor, y / factor);
  def *(other: Vector) = x * other.getX + y * other.getY
  def equals(other: Vector) = (this - other).isZero

  def squaredLength = this * this
  def length = math.sqrt(squaredLength)
  def normalized = this / length
  def angle(other: Vector) = math.acos(this * other / (length * other.length))
  def difference(other: Vector) = this - other
  def sum(other: Vector) = this + other

  def isLinearlyDependent(other: Vector) = geom.isZero(x * other.getY
      - y * other.getX)
  def isParallel(other: Vector) = isLinearlyDependent(other)
  def isOrthogonal(other: Vector) = geom.isZero(this * other)
  def isZero = geom.isZero(squaredLength)
  def isUnit = geom.isEqual(squaredLength, 1.0)

  //Returns the area of the parallelogram enclosed by `this` and `other`
  def parallelogramArea(other: Vector) = math.abs(x * other.getY
      - y * other.getX)

  def dot(x: Vector) = this.x * x.x + this.y * x.y
}

object Vector {

  /**
    * Standard linear interpolation between two vectors involving an interpolation factor.
    * @param start The starting vector.
    * @param end The end vector.
    * @param a Interpolation factor.
    * @return Interpolated vector according to input arguments.
    */
  def lerp(start: Vector, end: Vector, a: Double): Vector = start + ((end - start) * a)

  /**
    * Spherical interpolation across two vectors involving an interpolation factor.
    * @param start The starting vector.
    * @param end The end vector.
    * @param a Interpolation factor.
    * @return Interpolated vector according to input parameters.
    */
  def slerp(start: Vector, end: Vector, a: Double): Vector = {
    import scala.math._
    val dot = start dot end
    val clampFac = FunctionCollection.clamp(a, -1, 1)
    val theta = acos(dot) * clampFac
    val relVec = (end - start * dot).normalized
    start * cos(theta) + relVec * sin(theta)
  }

  /**
    * Just like [[geom.Vector#slerp(geom.Vector, geom.Vector, double)]], but with less
    * computational overhead (without sines and cosines).
    * @param start The starting vector.
    * @param end The end vector.
    * @param a Interpolation factor.
    * @return Interpolated vector according to input arguments.
    */
  def nlerp(start: Vector, end: Vector, a: Double): Vector = lerp(start, end, a).normalized

}

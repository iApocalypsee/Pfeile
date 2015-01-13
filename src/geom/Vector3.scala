package geom

import scala.math._

/**
 *
 * @author Josip Palavra
 */
case class Vector3(var x: Float, var y: Float, var z: Float) extends FloatVector {

  override type VecType = Vector3

  def cross(vec: VecType): Vector3 = {
    val _x = y * vec.z - z * vec.y
    val _y = z * vec.x - x * vec.z
    val _z = x * vec.y - y * vec.x
    Vector3( _x, _y, _z )
  }

  def rotate(axis: Vector3, angle: Float): VecType = {
    val sinAngle = sin( -angle ).asInstanceOf[Float]
    val cosAngle = cos( -angle ).asInstanceOf[Float]
    cross( (axis * sinAngle) + (this * cosAngle) + (axis * (this dot (axis * (1f - cosAngle)))) )
  }

  def dot(vec: VecType): Float = x * vec.x + y * vec.y + z * vec.z

  def lerp(dest: VecType, lerpFactor: Float): VecType = {
    ((dest - this) * lerpFactor) + this
  }

  def negated = Vector3(-x, -y, -z)

  def unary_- = negated

  def div(vec: VecType) = /(vec)
  def div(f: Float) = /(f)
  def add(vec: VecType) = this.+(vec)
  def add(f: Float) = this.+(f)
  def sub(vec: VecType) = this.-(vec)
  def sub(f: Float) = this.-(f)
  def mult(vec: VecType) = *(vec)
  def mult(f: Float) = *(f)

  def xy = Vector2( x, y )

  def yz = Vector2( y, z )

  def zx = Vector2( z, x )

  def yx = Vector2( y, x )

  def zy = Vector2( z, y )

  def xz = Vector2( x, z )

  override def asList = List(x, y, z)

  override def unifiedVector(factor: Float) = Vector3(factor, factor, factor)

  override def vectorFrom(x: List[Float]) = {
    require(x.size == dimension)
    Vector3(x(0), x(1), x(2))
  }
}

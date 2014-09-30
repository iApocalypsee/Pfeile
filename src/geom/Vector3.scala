package geom

import scala.math._

/**
 *
 * @author Josip Palavra
 */
case class Vector3(var x: Float, var y: Float, var z: Float) extends VectorLike {

  override type VecType = Vector3

  def this(x: Double, y: Double, z: Double) = this( x.asInstanceOf[Float], y.asInstanceOf[Float],
    z.asInstanceOf[Float] )

  /** The squared length of the vector.
    *
    * Use this to avoid additional square rooting. Square rooting takes additional time to calculate.
    */
  override def lengthSq: Float = (pow( x, 2 ) + pow( y, 2 ) + pow( z, 2 )).asInstanceOf[Float]

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

  override def toString: String = s"Vector3( $x | $y | $z )"

  override def ==(vec: VecType): Boolean = x == vec.x && y == vec.y && z == vec.z

  def equals(obj: VecType) = ==(obj)

  override def /(vec: VecType): VecType = Vector3( x / vec.x, y / vec.y, z / vec.z )


  override def /(f: Float): VecType = Vector3( x / f, y / f, z / f )


  /** The square root of <code>lengthSq</code> */
  override def length: Float = sqrt( lengthSq ).asInstanceOf[Float]

  override def +(vec: VecType): VecType = Vector3( x + vec.x, y + vec.y, z + vec.z )


  override def +(f: Float): VecType = Vector3( x + f, y + f, z + f )


  override def dot(vec: VecType): Float = x * vec.x + y * vec.y + z * vec.z

  override def lerp(dest: VecType, lerpFactor: Float): VecType = {
    ((dest - this) * lerpFactor) + this
  }

  def negated = Vector3(-x, -y, -z)

  def unary_- = negated

  /** Returns a copy of this vector with normalized coordinates.
    *
    * The source vector remains unchanged, a deep copy is instantiated and normalized.
    * @return A normalized copy of this vector.
    */
  override def normalized: VecType = {
    val c = copy( )
    c.normalize( )
    c
  }

  override def abs: VecType = Vector3( math.abs( x ), math.abs( y ), math.abs( z ) )

  override def -(vec: VecType): VecType = Vector3( x - vec.x, y - vec.y, z - vec.z )


  override def -(f: Float): VecType = Vector3( x - f, y - f, z - f )


  /** Normalizes the vector. */
  override def normalize(): Unit = {
    val len = length
    x /= len
    y /= len
    z /= len
  }

  override def *(vec: VecType): VecType = Vector3( x * vec.x, y * vec.y, z * vec.z )

  def div(vec: VecType) = /(vec)
  def div(f: Float) = /(f)
  def add(vec: VecType) = this.+(vec)
  def add(f: Float) = this.+(f)
  def sub(vec: VecType) = this.-(vec)
  def sub(f: Float) = this.-(f)
  def mult(vec: VecType) = *(vec)
  def mult(f: Float) = *(f)

  override def *(f: Float): VecType = Vector3( x * f, y * f, z * f )


  def xy = Vector2( x, y )

  def yz = Vector2( y, z )

  def zx = Vector2( z, x )

  def yx = Vector2( y, x )

  def zy = Vector2( z, y )

  def xz = Vector2( x, z )

  override def asFloatArray: Array[Float] = Array(x, y, z)
}

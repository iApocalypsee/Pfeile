package geom

import java.awt.Point

import scala.math._

/**
 *
 * @author Josip Palavra
 */
case class Vector2(var x: Float, var y: Float) extends VectorLike {

  override type VecType = Vector2

  def this(x: Double, y: Double) = this( x.asInstanceOf[Float], y.asInstanceOf[Float] )

  /** The squared length of the vector.
    *
    * Use this to avoid additional square rooting. Square rooting takes additional time to calculate.
    */
  override def lengthSq = (pow( x, 2 ) + pow( y, 2 )).asInstanceOf[Float]

  /** The square root of <code>lengthSq</code> */
  override def length = sqrt( lengthSq ).asInstanceOf[Float]

  /** Normalizes the vector. */
  override def normalize() = {
    val len = length
    x /= len
    y /= len
  }

  /** Returns a copy of this vector with normalized coordinates.
    *
    * The source vector remains unchanged, a deep copy is instantiated and normalized.
    * @return A normalized copy of this vector.
    */
  override def normalized = {
    val cpy = copy( )
    cpy.normalize( )
    cpy
  }

  override def dot(vec: Vector2): Float = x * vec.x + y * vec.y

  def cross(vec: Vector2): Float = x * vec.y - y * vec.x

  override def +(vec: Vector2) = Vector2( x + vec.x, y + vec.y )

  override def +(f: Float) = Vector2( x + f, y + f )

  override def -(vec: Vector2) = Vector2( x - vec.x, y - vec.y )

  override def -(f: Float) = Vector2( x - f, y - f )

  override def *(vec: Vector2) = Vector2( x * vec.x, y * vec.y )

  override def *(f: Float) = Vector2( x * f, y * f )

  override def /(vec: Vector2) = Vector2( x / vec.x, y / vec.y )

  override def /(f: Float) = Vector2( x / f, y / f )

  override def abs = Vector2( math.abs( x ), math.abs( y ) )

  override def toString: String = s"Vector2( $x | $y )"

  override def ==(vec: Vector2) = x == vec.x && y == vec.y

  override def lerp(dest: Vector2, lerpFactor: Float): Vector2 = {
    ((dest - this) * lerpFactor) + this
  }

  def rotate(angle: Float): Vector2 = {
    val rad = toRadians( angle )
    val cos = math.cos( rad )
    val sin = math.sin( rad )
    Vector2( (x * cos - y * sin).asInstanceOf[Float], (x * sin + y * cos).asInstanceOf[Float] )
  }

  def round = new Point(math.round(x), math.round(y))

  override def asFloatArray: Array[Float] = Array(x, y)

  def div(vec: VecType) = /(vec)
  def div(f: Float) = /(f)
  def add(vec: VecType) = this.+(vec)
  def add(f: Float) = this.+(f)
  def sub(vec: VecType) = this.-(vec)
  def sub(f: Float) = this.-(f)
  def mult(vec: VecType) = *(vec)
  def mult(f: Float) = *(f)

  def negated = Vector2(-x, -y)
}

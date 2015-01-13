package geom

import java.awt.Point

import scala.math._

/**
 * A two-dimensional, floating point vector.
 * @param x The x component.
 * @param y The y component.
 */
case class Vector2(x: Float, y: Float) extends FloatVector {

  override type VecType = Vector2

  def this(p: Point) = this(p.x, p.y)

  def dot(vec: Vector2): Float = x * vec.x + y * vec.y

  def cross(vec: Vector2): Float = x * vec.y - y * vec.x

  override def unifiedVector(factor: Float) = Vector2(factor, factor)

  override def vectorFrom(x: List[Float]) = {
    require(x.size == dimension)
    Vector2(x(0), x(1))
  }

  def lerp(dest: Vector2, lerpFactor: Float): Vector2 = {
    ((dest - this) * lerpFactor) + this
  }

  def rotate(angle: Float): Vector2 = {
    val rad = toRadians( angle )
    val cos = math.cos( rad )
    val sin = math.sin( rad )
    Vector2( (x * cos - y * sin).asInstanceOf[Float], (x * sin + y * cos).asInstanceOf[Float] )
  }

  def round = new Point(math.round(x), math.round(y))

  override def asList = List(x, y)

  def div(vec: Vector2) = /(vec)
  def div(f: Float) = /(f)
  def add(vec: Vector2) = this.+(vec)
  def add(f: Float) = this.+(f)
  def sub(vec: Vector2) = this.-(vec)
  def sub(f: Float) = this.-(f)
  def mult(vec: Vector2) = *(vec)
  def mult(f: Float) = *(f)

  def negated = Vector2(-x, -y)
}


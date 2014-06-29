package geom

import java.awt.geom.Point2D
import java.awt.Point
import misc.metadata.OverrideMetadatable

/**
 * A point default implementation for our program.
 * @param tuple The tuple.
 */
class PointDef(tuple: (Double, Double)) extends Point2D with OverrideMetadatable {

  private var _tuple = (new DoubleRef(tuple._1), new DoubleRef(tuple._2))

  /**
   * An overload making the access to the class more easy from Java-side.
   * @param x The x point.
   * @param y The y point.
   */
  def this(x: Double, y: Double) = this((x, y))

  override def getX = _tuple._1.value
  override def getY = _tuple._2.value
  override def setLocation(x: Double = getX, y: Double = getY) = {
    _tuple = (new DoubleRef(x), new DoubleRef(y))
  }
  def getRefX = _tuple._1
  def getRefY = _tuple._2
  def position = (_tuple._1, _tuple._2)

  def +(other: PointDef): PointDef = {
    return new PointDef((other.getX + getX, other.getY + getY))
  }

  def -(other: PointDef): PointDef = {
    return new PointDef((other.getX - getX, other.getY - getY))
  }

  def ==(other: PointDef): Boolean = {
    if(other eq null) return false
    (_tuple._1 == other.position._1) && (_tuple._2 == other.position._2)
  }

  def !=(other: PointDef) = !(this == other)

  def deepCopy = new PointDef((_tuple._1.value, _tuple._2.value))

}

object PointDef {

  /**
   * Converts a {@link java.awt.Point} to a PointDef object.
   * @param oldPoint The point object to convert.
   * @return The converted point object.
   */
  implicit def toPointDef(oldPoint: Point) = {
    new PointDef((oldPoint.x, oldPoint.y))
  }

}

/**
 * A reference class wrapping a double value.
 * @param value The double value to wrap up.
 */
class DoubleRef(var value: Double) {

  def ==(other: DoubleRef) = this.value == other.value
  def <(other: DoubleRef) = this.value < other.value
  def >(other: DoubleRef) = this.value > other.value
  def <=(other: DoubleRef) = this.value <= other.value
  def >=(other: DoubleRef) = this.value >= other.value
  def !=(other: DoubleRef) = this.value != other.value
  def +(other: DoubleRef) = this.value + other.value
  def -(other: DoubleRef) = this.value - other.value

}

object DoubleRef {
  implicit def DoubleRef2Int(x: DoubleRef): Int = x.value.asInstanceOf[Int]
}

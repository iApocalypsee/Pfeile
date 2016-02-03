package geom

class Point(xc: Double, yc: Double) {
	private var x = xc
	private var y = yc

	def this(pt: java.awt.geom.Point2D) = this(pt.getX, pt.getY)
	def this(pt: java.awt.Point) = this(pt.getX, pt.getY)

	def getX = x
	def getY = y

	def -(other: Point) = new Vector(x - other.getX, y - other.getY)
	def +(v: Vector) = new Point(x + v.getX, y + v.getY)

	def difference(other: Point) = this - other
	def add(vec: Vector) = this + vec

	def isOrigin = geom.isZero(x) && geom.isZero(y)
	def setCoordinate(newX: Double = x, newY: Double = y): Unit = {
		x = newX
		y = newY
  }

  def toVector = new Vector(x, y)
}

object Point {
	def isCollinear(a: Point, b: Point, c: Point) = (b - a) isParallel (c - a)
	def distance(a: Point, b: Point) = (b - a).length
}

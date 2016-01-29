package geom

class Point(x: Double, y: Double) {
    def getX = x
    def getY = y

    def -(other: Point) = new Vector(x - other.x, y - other.y)
    def +(v: Vector) = new Point(x + v.getX, y + v.getY)

    def isOrigin = isZero(x) && isZero(y)
}

isCollinear(a: Point, b: Point, c: Point) = (b - a) isParallel (c - a)

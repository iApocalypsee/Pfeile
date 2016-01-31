package geom

class Vector(xc: Double, yc: Double) {
    private var x: Double = xc
    private var y: Double = yc

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
}

package geom

class Vector(x: Double, y: Double) {
    def getX = x
    def getY = y

    def +(other: Vector) = new Vector(x + other.x, y + other.y)
    def +(other: Point) = new Point(x + other.getX, y + other.getY)
    def -(other: Vector) = new Vector(x - other.x, y - other.y)
    def *(factor: Double) = new Vector(x * factor, y * factor);
    def /(factor: Double) = new Vector(x / factor, y / factor);
    def *(other: Vector) = x * other.x + y * other.y
    def ==(other: Vector) = isZero(this - other)

    def squaredLength = this * this
    def length = math.sqrt(squaredLength)
    def difference(other: Vector) = this - other

    def isLinearlyDependent(other: Double) = geom.isZero(x * other.y - y * other.x)
    def isParallel(other: Double) = isLinearlyDependent(other)
    def isOrthogonal(other: Vector) = geom.isZero(this * other)
    def isZero = geom.isZero(squaredLength)
    def isUnit = geom.isEqual(squaredLength, 1.0)

    //Returns the area of the parallelogram enclosed by `this` and `other`
    def parallelogramArea(other: Double) = math.abs(x * other.y - y * other.x)
}

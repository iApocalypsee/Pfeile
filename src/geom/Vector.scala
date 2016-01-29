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

    def length_squared = x * x + y * y
    def length = sqrt(length_squared)
    def difference(other: Vector) = this - other

    def isLinearlyDependent(other: Double) = isZero(x * other.y - y * other.x)
    def isParallel(other: Double) = isLinearlyDependent(other)

    //Returns the area of the parallelogram enclosed by `this` and `other`
    def parallelogramArea(other: Double) = math.abs(x * other.y - y * other.x)
}

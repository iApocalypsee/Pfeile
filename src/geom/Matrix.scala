package geom

class Matrix(m11: Double, m12: Double, m13: Double,
    m21: Double, m22: Double, m23: Double,
    m31: Double, m32: Double, m33: Double) {

    //Contructor without params ==> Identity matrix
    def this() = this(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)

    //new Matrix(some angle) = rotation matrix
    def this(theta: Double) = new Matrix(math.cos(theta), -math.sin(theta), 0.0,
        math.sin(theta), math.cos(theta), 0.0,
        0.0, 0.0, 1.0)

    //new Matrix(some vector) = translation matrix
    def this(translation: Vector) = new Matrix(1.0, 0.0, translation.getX,
        0.0, 1.0, translation.getY, 0.0, 0.0, 1.0)

    //new Matrix(some x, some y) = translation matrix
    def this(dx: Double, dy: Double) = new Matrix(new Vector(dx, xy))

    def *(other: Matrix) = {
        val r11 = m11 * other.m11 + m12 * other.m21 + m13 * other.m31
        val r12 = m11 * other.m12 + m12 * other.m22 + m13 * other.m32
        val r13 = m11 * other.m13 + m12 * other.m23 + m13 * other.m33
        val r21 = m21 * other.m11 + m22 * other.m21 + m23 * other.m31
        val r22 = m21 * other.m12 + m22 * other.m22 + m23 * other.m32
        val r23 = m21 * other.m13 + m22 * other.m23 + m23 * other.m33
        val r31 = m31 * other.m11 + m32 * other.m21 + m33 * other.m31
        val r32 = m31 * other.m12 + m32 * other.m22 + m33 * other.m32
        val r33 = m31 * other.m13 + m32 * other.m23 + m33 * other.m33
        new Matrix(r11, r12, r13, r21, r22, r23, r31, r32, r33)
    }

    def appendTo(other: Matrix) = this * other
    def append(other: Matrix) = other appendTo this

    def *(other: Vector) {
        val x = m11 * other.getX + m12 * other.getY
        val y = m21 * other.getX + m22 * other.getY
        new Vector(x, y)
    }

    def *(other: Point) {
        val x = m11 * other.getX + m12 * other.getY + m13
        val y = m21 * other.getX + m22 * other.getY + m23
        new Point(x, y)
    }

    def transform(other: Point) = self * other
    def transform(other: Vector) = self * other
}

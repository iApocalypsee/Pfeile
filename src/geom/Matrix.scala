package geom

class Matrix(m11: Double, m12: Double, m21: Double, m22: Double) {

	def apply(row: Int, column: Int) = row match {
		case 1 => column match {
			case 1 => m11
			case 2 => m12
			case _ => 0.0
		}
		case 2 => column match {
			case 1 => m21
			case 2 => m22
			case _ => 0.0
		}
		case _ => 0.0
	}

	def *(other: Matrix): Matrix = new Matrix(
		this(0, 0) * other(0, 0) + this(0, 1) * other(1, 0),
		this(0, 0) * other(0, 1) + this(0, 1) * other(1, 1),
		this(1, 0) * other(0, 0) + this(1, 1) * other(1, 0),
		this(1, 0) * other(0, 1) + this(1, 1) * other(1, 1))
	def after(other: Matrix) = this * other

	def *(other: Vector): Vector = new Vector(
		m11 * other.getX + m12 * other.getY,
		m21 * other.getX + m22 * other.getY)
	def transform(other: Vector) = this * other

	def *(other: Double) = new Matrix(m11 * other, m12 * other, m21 * other, m22 * other)
	def scale(other: Double) = this * other

	def inverse =
		if (determinant != 0)
			new Matrix(m22, -m12, -m21, m11) * (1 / determinant)
		else
			Matrix.newIdentity

	def determinant = m11 * m22 - m12 * m21
}

object Matrix {
    def newIdentity = new Matrix(1.0, 0.0, 0.0, 1.0)
    def newRotation(theta: Double) = {
        val c = math.cos(theta)
        val s = math.sin(theta)
        new Matrix(c, -s, s, c)
    }
    def newScaling(x: Double, y: Double) = new Matrix(x, 0.0, 0.0, y)
}

package geom

class Matrix(m11: Double, m12: Double, m13: Double,
    m21: Double, m22: Double, m23: Double,
    m31: Double, m32: Double, m33: Double) {

    def apply(row: Int, column: Int) = row match {
        case 1 => column match {
            case 1 => m11
            case 2 => m12
            case 3 => m13
            case _ => 0.0
        }
        case 2 => column match {
            case 1 => m21
            case 2 => m22
            case 3 => m23
            case _ => 0.0
        }
        case 3 => column match {
            case 1 => m31
            case 2 => m32
            case 3 => m33
            case _ => 0.0
        }
        case _ => 0.0
    }

    def *(other: Matrix): Matrix = {
        var result = Array.ofDim[Double](3, 3)
        for (i <- 1 to 3; j <- 1 to 3; k <- 1 to 3) {
            result(i - 1)(k - 1) += this(i, j) * other(j, k)
        }
        new Matrix(result(0)(0), result(0)(1), result(0)(2),
            result(1)(0), result(1)(1), result(1)(2),
            result(2)(0), result(2)(1), result(2)(2));
    }

    def appendTo(other: Matrix) = this * other
    def append(other: Matrix) = other appendTo this

    def *(other: Vector): Vector = {
        val x = m11 * other.getX + m12 * other.getY
        val y = m21 * other.getX + m22 * other.getY
        new Vector(x, y)
    }

    def *(other: Point): Point = {
        val x = m11 * other.getX + m12 * other.getY + m13
        val y = m21 * other.getX + m22 * other.getY + m23
        new Point(x, y)
    }

    def transform(other: Point) = this * other
    def transform(other: Vector) = this * other
}

object Matrix {
    def newIdentity = new Matrix(1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0)
    def newRotation(theta: Double) = {
        val c = math.cos(theta)
        val s = math.sin(theta)
        new Matrix(c, -s, 0.0, s, c, 0.0, 0.0, 0.0, 1.0)
    }
    def newTranslation(dx: Double, dy: Double) = new Matrix(1.0, 0.0, dx,
        0.0, 1.0, dy, 0.0, 0.0, 1.0)
    def newScaling(x: Double, y: Double) = new Matrix(x, 0.0, 0.0, 0.0, y, 0.0,
        0.0, 0.0, 1.0)

  import breeze.linalg._

  def identity = DenseMatrix[(Double, Double, Double, Double), Double](
    (1, 0, 0, 0),
    (0, 1, 0, 0),
    (0, 0, 1, 0),
    (0, 0, 0, 1)
  )



  def perspective(top: Double, right: Double, bottom: Double, left: Double, near: Double, far: Double) = DenseMatrix[(Double, Double, Double, Double), Double](
    ((2 * near) / (right - left), 0, (right + left) / (right - left), 0),
    (0, (2 * near) / (top - bottom), (top + bottom) / (top - bottom), 0),
    (0, 0, -(far + near) / far - near, (-2 * far * near) / (far - near)),
    (0, 0, -1, 0)
  )

}

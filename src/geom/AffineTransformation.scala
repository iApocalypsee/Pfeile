package geom

/**
	* Created by philip on 07.02.16.
	*/
class AffineTransformation(m: Matrix, t: Vector) {
	def getMatrix = m
	def getDisplacement = t

	def *(other: Point) = Point.origin + m * (other - Point.origin) + t
	def transform(other: Point) = this * other

	def *(other: AffineTransformation) = new AffineTransformation(m * other.getMatrix, m * other.getDisplacement + t)
	def after(other: AffineTransformation) = this * other

	def *(other: Double) = new AffineTransformation(m * other, t * other)
	def scale(other: Double) = this * other

	def inverse = {
		val inv = m.inverse
		val vec = m.inverse * t * (-1)
		new AffineTransformation(inv, vec)
	}
}

object AffineTransformation {
	def identity = new AffineTransformation(Matrix.newIdentity, new Vector(0.0, 0.0))
	def translation(t: Vector) = new AffineTransformation(Matrix.newIdentity, t)
	def rotation(theta: Double) = new AffineTransformation(Matrix.newRotation(theta), new Vector(0.0, 0.0))
	def rotationAround(theta: Double, o: Point) =
		translation(o - Point.origin) * rotation(theta) * translation(Point.origin - o)       //Translation to origin, rotation around origin, then translation back to o

	def scaling(x: Double, y: Double) = new AffineTransformation(Matrix.newScaling(x, y), new Vector(0.0, 0.0))
	def scalingAround(x: Double, y: Double, o: Point) =
		translation(o - Point.origin) * scaling(x, y) * translation(Point.origin - o)         //Analogous to rotationAround
}

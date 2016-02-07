package geom.primitives

import geom.{Point, primitives}

/**
	* Created by philip on 31.01.16.
	* Implementation of the Triangles primitive which treats the vertex list as flattened list of triangle vertices
	*/
class Triangles[Vertex <: Point](points: Vertex*) extends Primitive[Vertex](points) {
	def render() = {
		val transformationMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.worldMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		val xs = transformedVertices.map(_.getX.asInstanceOf[Int])
		val ys = transformedVertices.map(_.getY.asInstanceOf[Int])

		for (i <- 0 until vertices.size / 3) {
			val shape = new java.awt.Polygon(Array(xs(i * 3), xs(i * 3 + 1), xs(i * 3 + 2)), Array(ys(i * 3), ys(i * 3 + 1),
				ys(i * 3 + 2)), 3)
			primitives.graphics.fill(shape)
		}
	}
}

package geom.primitives

import geom.{primitives, Point}

/**
	* Created by philip on 31.01.16.
	* Implementation of the TriangleStrip primitive which creates a triangle of every group of three adjacent (in the Array) vertices
	*/
class TriangleStrip[Vertex <: Point] extends Primitive[Vertex] {
	def render = {
		val transformationMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.worldMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		val xs = transformedVertices.map(_.getX.asInstanceOf[Int])
		val ys = transformedVertices.map(_.getY.asInstanceOf[Int])

		for (i <- 0 until vertices.size - 2) {
			val shape = new java.awt.Polygon(Array(xs(i), xs(i + 1), xs(i + 2)), Array(ys(i), ys(i + 1), ys(i + 2)), 3)
			primitives.graphics.fill(shape)
		}
	}
}

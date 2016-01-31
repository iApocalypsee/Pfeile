package geom.primitives

import geom.{primitives, Point}

/**
	* Created by philip on 31.01.16.
	* Implementation of the TriangleFan primitive which creates triangles of the first vertex and every group of adjacent (in the Array) vertices
	*/
class TriangleFan[Vertex <: Point] extends Primitive[Vertex] {
	def render = {
		val transformationMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.worldMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		val xs = transformedVertices.map(_.getX.asInstanceOf[Int])
		val ys = transformedVertices.map(_.getY.asInstanceOf[Int])
		for (i <- 1 until vertices.size - 1) {
			val shape = new java.awt.Polygon(Array(xs(0), xs(i), xs(i + 1)), Array(ys(0), ys(i), ys(i + 1)), 3)
			primitives.graphics.fill(shape)
		}
	}
}

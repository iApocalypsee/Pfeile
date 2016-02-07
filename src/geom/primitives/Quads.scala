package geom.primitives

import geom.{primitives, Point}

/**
	* Created by philip on 31.01.16.
	*/
class Quads[Vertex <: Point](points: Vertex*) extends Primitive[Vertex] {
	def render = {
		val transformationMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.projectionMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		val xs = transformedVertices.map(_.getX.asInstanceOf[Int])
		val ys = transformedVertices.map(_.getY.asInstanceOf[Int])
		for (i <- 0 until vertices.size / 4) {
			val shape = new java.awt.Polygon(Array(xs(i * 4), xs(i * 4 + 1), xs(i * 4 + 2), xs(i * 4 + 3)),
				Array(ys(i * 4), ys(i * 4 + 1), ys(i * 4 + 2), ys(i * 4 + 3)), 4)
			primitives.graphics.fill(shape)
		}
	}
}

package geom.primitives

import geom.{Point, primitives}

/**
	* Created by philip on 31.01.16.
	* Implementation of the LineStrip primitive which draws lines from one vertex to the next, creating a line strip
	*/
class LineStrip[Vertex <: Point] extends Primitive[Vertex] {
	def render() = {
		val transformationMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.worldMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		for (i <- 0 until vertices.size - 1) {
			primitives.graphics.drawLine(transformedVertices(i).getX.asInstanceOf[Int], transformedVertices(i).getY.asInstanceOf[Int],
				transformedVertices(i + i).getX.asInstanceOf[Int], transformedVertices(i + 1).getY.asInstanceOf[Int])
		}
	}
}

package geom.primitives

import geom.{primitives, Point}

/**
	* Created by philip on 31.01.16.
	* Implementation of the LineLoop primitive which - like LineStrip - draws lines from one vertex to the next and a line
	* from the first vertex to the last, closing off the polygon
	*/
class LineLoop[Vertex <: Point] extends Primitive[Vertex] {
	def render = {
		val transformationMatrix = primitives.projectionMatrix * primitives.worldMatrix * primitives.cameraMatrix
		val transformedVertices = vertices.map(transformationMatrix * _)
		for (i <- vertices.indices) {
			primitives.graphics.drawLine(transformedVertices(i).getX.asInstanceOf[Int], transformedVertices(i).getY.asInstanceOf[Int],
				transformedVertices((i + 1) % vertices.size).getX.asInstanceOf[Int],
				transformedVertices((i + 1) % vertices.size).getY.asInstanceOf[Int])
		}
	}
}

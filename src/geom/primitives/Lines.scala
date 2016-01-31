package geom.primitives

import geom.{primitives, Point}
/**
	* Created by philip on 31.01.16.
	* Implementation of the Lines primitive, which treats the vertex list as alternating list of starting and ending points of lines
	*/
class Lines[Vertex <: Point] extends Primitive[Vertex] {
	override def render = {
		val transformMatrix = primitives.projectionMatrix * primitives.cameraMatrix * primitives.worldMatrix
		val transformedVertices = vertices.map(transformMatrix * _)
		for (i <- 0 until vertices.size / 2) {
			primitives.graphics.drawLine(transformedVertices(2 * i).getX.asInstanceOf[Int], transformedVertices(2 * i).getY.asInstanceOf[Int],
				transformedVertices(2 * i + 1).getX.asInstanceOf[Int], transformedVertices(2 * i + 1).getY.asInstanceOf[Int])
		}
	}
}

package geom.primitives

import geom._
import scala.collection.mutable.ArrayBuffer

/**
	* Created by philip on 31.01.16.
	* Abstract class for drawing primitives (cf. https://www.opengl.org/wiki/Primitive for further information on primitives)
	*/
abstract class Primitive[Vertex <: Point](points: Vertex*) {
	protected var vertices: ArrayBuffer[Vertex] = {
		var result: ArrayBuffer[Vertex] = new ArrayBuffer[Vertex]()
		points.copyToBuffer(result)
		result
	}

	def render
}

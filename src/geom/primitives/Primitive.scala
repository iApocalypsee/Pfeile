package geom.primitives

import geom._

import scala.collection.mutable.ArrayBuffer

/**
	* Created by philip on 31.01.16.
	* Abstract class for drawing primitives (cf. https://www.opengl.org/wiki/Primitive for further information on primitives)
	*/
abstract class Primitive[Vertex <: Point](points: Seq[Vertex]) {

	protected var vertices: ArrayBuffer[Vertex] = ArrayBuffer(points:_*)

	def render(): Unit
}

package geom.primitives

import java.awt.Polygon

import geom._

import scala.collection.mutable.ArrayBuffer

/**
	* Created by philip on 31.01.16.
	* Abstract class for drawing primitives (cf. https://www.opengl.org/wiki/Primitive for further information on primitives)
	*/
abstract class Primitive[Vertex <: Point](points: Vertex*) {

	private var m_vertices: ArrayBuffer[Vertex] = {
		val result: ArrayBuffer[Vertex] = new ArrayBuffer[Vertex]()
		points.copyToBuffer(result)
		result
	}

  private var m_cachedPolygon = verticesToPolygon(points)

  private def verticesToPolygon(xs: Seq[Vertex]): Polygon = {
    val ret = new Polygon
    xs foreach { vert => ret.addPoint(vert.getX.asInstanceOf[Int], vert.getY.asInstanceOf[Int]) }
    ret
  }

  protected def vertices = m_vertices
  protected def vertices_=(x: Seq[Vertex]): Unit = {
    m_vertices = ArrayBuffer(x:_*)
    m_cachedPolygon = verticesToPolygon(x)
  }

	def render(): Unit
}

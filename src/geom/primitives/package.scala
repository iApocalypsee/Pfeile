package geom

import java.awt.Polygon

import scala.beans.BeanProperty

/**
	* Created by philip on 31.01.16.
	*/
package object primitives {
	@BeanProperty var graphics: java.awt.Graphics2D = null
	@BeanProperty var worldMatrix: AffineTransformation = AffineTransformation.identity           //Describes the transformation from object space to world space (is assigned for each object)
	@BeanProperty var cameraMatrix: AffineTransformation = AffineTransformation.identity          //Describes the transformation from world space to camera space (e.g. view port, translation)
	@BeanProperty var projectionMatrix: AffineTransformation = AffineTransformation.scaling(1, -1)//Describes the transformation from camera space to the screen (e.g. perspective (haha...), scaling, zoom, and so on)

	def renderPrimitive(primitiveType: PrimitiveType, vertices: Point*): Unit = primitiveType match {
		case PrimitiveType.LINES => new Lines[Point](vertices:_*).render()                        //vertices:_* transforms vertices (which is a Seq) to varargs to be passed to the constructor
		case PrimitiveType.LINE_STRIP => new LineStrip[Point](vertices:_*).render()
		case PrimitiveType.LINE_LOOP => new LineLoop[Point](vertices:_*).render()
		case PrimitiveType.TRIANGLES => new Triangles[Point](vertices:_*).render()
		case PrimitiveType.TRIANGLE_STRIP => new TriangleStrip[Point](vertices:_*).render()
		case PrimitiveType.TRIANGLE_FAN => new TriangleFan[Point](vertices:_*).render()
		case PrimitiveType.QUADS => new Quads[Point](vertices:_*).render()
	}

	def renderText(center: Point, text: String): Unit = {
		val transformed = projectionMatrix * cameraMatrix * worldMatrix * center
		val height = graphics.getFontMetrics.getHeight
		val width = graphics.getFontMetrics.stringWidth(text)
		graphics.drawString(text, transformed.getX.asInstanceOf[Int] + height / 2, transformed.getY.asInstanceOf[Int] - width / 2)
	}

	def objectSpaceToProjection(p: Point) = projectionMatrix * cameraMatrix * worldMatrix * p
	def objectSpaceToProjection(v: Vector) = projectionMatrix.getMatrix * cameraMatrix.getMatrix * worldMatrix.getMatrix * v
	def pointsToPolygon(points: Point*) = {
		val transformationMatrix = projectionMatrix * cameraMatrix * worldMatrix
		val transformedValues = points.map(transformationMatrix * _)
		val xs = transformedValues.map(_.getX.asInstanceOf[Int]).toArray
		val ys = transformedValues.map(_.getY.asInstanceOf[Int]).toArray
		new Polygon(xs, ys, points.length)
	}
}

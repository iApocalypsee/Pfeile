package geom

import java.awt.geom.Point2D
import java.util

import geom.interfaces.{Vector, Triangle, VectorChain}

import scala.collection.mutable

/**
 *
 * @author Josip Palavra
 * @version 26.06.2014
 */
class VecChain extends VectorChain {

  /**
   * The point list.
   */
  private val ptList = mutable.Queue[PointDef]()

  override def getVectors: util.List[Vector] = {
    val vec = new util.LinkedList[Vector]()
    for(i <- 1 until ptList.size) vec.add(new VectorDef(ptList(i - 1), ptList(i)))
    vec
  }

  override def countBreaks(): Int = ptList.size - 2

  override def getStartVector: Vector = getVectors.get(0)

  override def getEndVector: Vector = {
    val v = getVectors
    v.get(v.size() - 1)
  }

  override def totalLength(): Double = {
    val v = getVectors
    var x = 0.0
    for(i <- 0 until v.size()) {
      x += v.get(i).straightLength()
    }
    x
  }

  override def append(point: PointRef): Unit = ptList.enqueue(point)

  override def remove(index: Int): Unit = ptList.dequeueFirst(ptList(index) eq _)

  override def getStartY: Double = ptList(0).getX

  override def getEndX: Double = ptList.last.getX

  override def setEndY(y: Double): Unit = ptList.last.setLocation(y = y)

  override def setStartX(x: Double): Unit = ptList.head.setLocation(x = x)

  override def diffY(): Double = ???

  override def triangulate(): Triangle = ???

  override def getStartX: Double = ptList.head.getX

  override def setEndX(x: Double): Unit = ptList.last.setLocation(x = x)

  override def straightLength(): Double = Point2D.distance(ptList(0).getX, ptList(0).getY, ptList.last.getX, ptList.last.getY)

  override def diffX(): Double = ???

  override def getEndY: Double = ptList.last.getY

  override def setStartY(y: Double): Unit = ptList.head.setLocation(y = y)
}

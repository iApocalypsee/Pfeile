package entity

import java.awt.Color
import java.awt.geom.Point2D

import geom.interfaces.VectorChain
import geom.{PointDef, VecChain}
import world.{BaseTile, IBaseTile}

import scala.collection.immutable.Stack
import scala.collection.mutable

/**
 *
 * @author Josip Palavra
 * @version 21.06.2014
 */
package object path {

  /**
   * Applies the A* path search algorithm to a start and end tile.
   * @param from From where it starts.
   * @param to Where it ends.
   * @param f Filter function. Defaults to accepting all tiles.
   * @return The path, if any.
   */
  def a_star(from: IBaseTile, to: IBaseTile, f: IBaseTile => Boolean = _ => true): Option[Path] = {

    // if the target tile does not fulfill the conditions in the function, don't bother calculating
    // a path to the tile
    if(!f(to)) return None

    // Representing a node. Nodes can be linked together by reference.
    class Node(var prev: Node, var that: IBaseTile) {
      // if the prev value equals null, then it should be the start node
      if(prev eq null) require(that eq from)

      /**
       * The movement costs required to get to this node.
       */
      def movementCosts = {
        def recur(n: Node = this, mov: Int = 0): Int = {
          if(n.prev eq null) mov
          else {
            val tempMov = mov + n.that.getRequiredMovementPoints
            recur(n.prev, tempMov)
          }
        }
        recur()
      }

      /**
       * The heuristic value.
       */
      def heuristic = Point2D.distance(that.getGridX, that.getGridY, to.getGridX, to.getGridY)

      def fVal = movementCosts + heuristic

    }

    implicit object NodeOrdering extends Ordering[Node] {
      override def compare(x: Node, y: Node): Int = x.fVal compare y.fVal
    }

    val start = new Node(null, from)
    var end: Node = null
    var pathFound = true

    val closed = mutable.Queue[Node]()
    val open = mutable.Queue(start)

    open.sorted

    def nodeOf(tile: IBaseTile) = open.get(open.indexWhere(_.that eq tile)).get

    def enqueueCircle(center: Node): Unit = {
      val neighbors = world.tile.neighborsOf(center.that).values
      for(tile <- neighbors) {
        // if the neighbor tile has been expanded already, don't enqueue it again
        if(closed.contains(tile)) print()
        else if(open.contains(tile)) {
          val neighNode = nodeOf(tile)
          if(neighNode.movementCosts > center.movementCosts + tile.getRequiredMovementPoints) neighNode.prev = center
        }
        // if the neighbor tile meets the conditions in the given function
        else if(f(tile)) {
          open.enqueue(new Node(center, tile))
        }
      }
      closed.enqueue(center)
      open.sorted
    }

    def loop: Unit = {
      while(open.nonEmpty) {
        val current = open.dequeue()
        if(current.that eq to) {
          end = current
          return
        }
        else enqueueCircle(current)
      }
      pathFound = false
    }

    def nodeToPath(n: Node): Path = {
      def recur(cur: Node, construct: Stack[IBaseTile]): Stack[IBaseTile] = {
        if(cur.prev eq null) construct
        else {
          recur(cur.prev, construct.push(cur.that))
        }
      }
      new Path(recur(n, Stack[IBaseTile]()).toList)
    }

    loop

    if(pathFound) {
      val x = nodeToPath(end)
      for(tile <- x.tiles) {
        tile.asInstanceOf[BaseTile].handle(g => {
          g.setColor(pathColor)
          g.fillPolygon(tile.asInstanceOf[BaseTile].getBounds)
        })
      }
      Some(x)
    } else None

  }

  /**
   * Converts a path object to a vector chain object.
   * @param p The path.
   * @return The vector chain.
   */
  implicit def pathToVector(p: Path): VecChain = {
    val vec = new VecChain
    for(tile <- p.tiles) {
      vec.append(new PointDef((tile.getGridX, tile.getGridY)))
    }
    vec
  }

  /**
   * Converts a vector chain to a path, if it can be converted.
   * This method is not implicit because it can cause a lot of trouble and headache
   * not seeing that a vector chain becomes implicitly an Option[Path].
   * @param vec The vector chain.
   * @return The path, if any.
   */
  def vectorToPath(vec: VectorChain): Option[Path] = ???

  val pathColor = new Color(55, 200, 15, 120)

}

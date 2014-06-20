package entity.path

import scala.collection.mutable

import world.IBaseTile

trait PathFindingAlgorithm {
  def findPath(from: IBaseTile, to: IBaseTile, f: IBaseTile => Boolean): Option[Path]
}

/**
 *
 * @author Josip Palavra
 * @version 20.06.2014
 */
object AStar extends PathFindingAlgorithm {

  private class Node(val tile: IBaseTile) {
    private var _prev: Node = null
    def prev = _prev
    def prev_=(n: Node) = _prev = n
  }

  override def findPath(from: IBaseTile, to: IBaseTile, f: IBaseTile => Boolean = _ => true): Option[Path] = {
    val closed = mutable.Queue[IBaseTile]()
    val open = mutable.Queue(from)

    def nodeWithLeastCosts: Node = ???

    // the recursive looping function
    def recur: Option[Path] = {
      if(open.isEmpty) None
      else {
        recur
      }
    }

    recur
  }
}

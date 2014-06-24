package entity

import geom.interfaces.VectorChain
import world.IBaseTile

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

    class Node(val tile: IBaseTile) {
      private var _prev: Node = null
      def prev = _prev
      def prev_=(n: Node) = _prev = n
    }

    val closed = mutable.Queue[IBaseTile]()
    val open = mutable.Queue(from)

    def nodeWithLeastF = {
      var x: IBaseTile = null
      var least: IBaseTile = null
      for(tile <- open) {
        if(least eq null) {
          least = tile
        } //else if()
      }
    }

    while(!open.isEmpty) {

    }
    ???

  }

  /**
   * Converts a path object to a vector chain object.
   * @param p The path.
   * @return The vector chain.
   */
  implicit def pathToVector(p: Path): VectorChain = ???

  /**
   * Converts a vector chain to a path, if it can be converted.
   * This method is not implicit because it can cause a lot of trouble and headache
   * not seeing that a vector chain becomes implicitly an Option[Path].
   * @param vec The vector chain.
   * @return The path, if any.
   */
  def vectorToPath(vec: VectorChain): Option[Path] = ???

}

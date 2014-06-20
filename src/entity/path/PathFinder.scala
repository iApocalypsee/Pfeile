package entity.path

import world.IBaseTile
import world.tile._

/**
 * The best syntax for using the path finder object is
 * <code>
 * <br><br>
 * <b>find</b> (tile) [<b>without</b> (function describing ignores)]
 * <br><br>
 * </code>
 * where words surrounded by round brackets are parameters and words surrounded
 * by square brackets are optional fragments. Bold words are "keywords"
 * for the path finder language.
 * @author Josip
 * @version 27.05.2014
 */
class PathFinder {

  def find(tile: IBaseTile) = {

    val p = new PathFinder

    /**
     * The first chunk of the path DSL.
     */
    class PathDSLChunk1(to: IBaseTile) extends PathDSLChunk(to) {
      def without(f: IBaseTile => Boolean): Unit = pathEval = f
    }
    new PathDSLChunk1(tile)

  }

}

sealed class PathDSLChunk private[path](to: IBaseTile) {
  val destination = to
  protected var pathEval: (IBaseTile) => Boolean = (_) => true

  /**
   * Checks if the tile is reachable with the conditions given.
   */
  def checkDestinationAvailability = neighborsOf(destination).filter(tuple => pathEval(tuple._2)).isEmpty

  private def foo(t: IBaseTile) = {
    val p = new PathFinder
    p find t without {_ => true}
  }
}

object PathFinder {
  implicit def toPath(langChunk: PathDSLChunk): Path = {
    println("Path algorithm missing!")

    if(!langChunk.checkDestinationAvailability) {
      println("There is no path to the tile.")
    }
    ???
  }
}

sealed class Path private[path](private var _tiles: List[IBaseTile]) {

  def tiles = _tiles

}

package entity.path

import entity.MoveableEntity
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
class PathFinder(val ent: MoveableEntity) {

  def find(tile: IBaseTile) = {
    new PathDSLChunk1(tile, this)

  }

}

sealed class PathDSLChunk private[path](to: IBaseTile, val pf: PathFinder) {
  val destination = to
  protected var pathEval: (IBaseTile) => Boolean = (_) => true

  /**
   * Checks if the tile is reachable with the conditions given.
   */
  def checkDestinationAvailability = neighborsOf(destination).filter(tuple => pathEval(tuple._2)).isEmpty

  def eval = pathEval

  def toPath = a_star(pf.ent.location, destination, eval)
}

/**
 * The first chunk of the path DSL.
 */
sealed class PathDSLChunk1(to: IBaseTile, pf: PathFinder) extends PathDSLChunk(to, pf) {
  def without(f: IBaseTile => Boolean) = {
    pathEval = (t) => !f(t)
    this
  }
}

sealed class Path private[path](val tiles: List[IBaseTile]) {

  def apply(x: Int) = tiles(x)

}

package entity.path

import world.{IBaseTile, TileHelper}

import scala.collection.mutable

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

  def find(tile: IBaseTile) = new PathDSLChunk1(tile)

}

sealed class PathDSLChunk private[entity](to: IBaseTile) {
  val destination = to
  protected val pathEval = new mutable.Queue[(IBaseTile) => Boolean]

  /**
   * Checks if the tile is reachable with the conditions given.
   */
  def checkDestinationAvailability = {
    var x = false
    for(eval <- pathEval) x = TileHelper.neighborsOf(to).filter(eval).isEmpty
    x
  }
}

/**
 * The first chunk of the path DSL.
 * @param to The base tile to which the entit should navigate.
 */
private [entity] class PathDSLChunk1(to: IBaseTile) extends PathDSLChunk(to) {
  private var ignoreCondition: IBaseTile => Boolean = null
  def without(f: IBaseTile => Boolean): Unit = {
    pathEval.dequeueFirst(_ eq ignoreCondition)
    ignoreCondition = f
    pathEval.enqueue(ignoreCondition)
  }
}

object PathFinder {
  implicit def toPath(langChunk: PathDSLChunk): Path = {
    println("Path algorithm missing!")
    ???
  }
}

sealed abstract class Path(lang: PathDSLChunk) {

  private val vec: Nothing

}

package newent.pathfinding

import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import general.{LogFacility, ScalaUtil}
import newent.MovableEntity
import world.Tile

import scala.annotation.tailrec
import scala.collection.mutable
import scala.math._

/**
 *
 * @author Josip Palavra
 */
class AStarPathfinder(val maxSearchDepth: Int, val excludes: Tile => Boolean) extends Pathfinder {

  private val closed = mutable.ArrayBuffer[Node]()
  private val open = new SortedList

  /** Finds a path according to the implemented logic.
    *
    * @param moveable The moveable entity for which to find the path.
    * @param tx The target x position.
    * @param ty The target y position.
    * @return An optional path.
    */
  override def findPath(moveable: MovableEntity, tx: Int, ty: Int): Option[Path] = synchronized {
    import scala.util.control.Breaks.{break, breakable}

    val terrain = moveable.tileLocation.terrain

    val sx = moveable.getGridX
    val sy = moveable.getGridY

    // Multidimensional array of nodes. I need nodes for connecting the dots (literally).
    val nodes = Array.tabulate(terrain.width, terrain.height) { (x, y) => new Node(terrain.tileAt(x, y)) }

    Node.target = nodes(tx)(ty)

    // Clear those lists, I am finding another path...
    closed.clear()
    open.list.clear()

    open.list += nodes(sx)(sy)

    breakable {
      while (open.list.nonEmpty) {

        // Pull out the first node in our open list, this is determined to
        // be the most likely to be the next step based on our heuristic
        val current = open.nodeWithLeastFValue

        if (current == null || current.equals(nodes( tx )( ty ))) break()

        open.list.remove(open.list.indexOf(current))
        closed += current

        // search through all the neighbours of the current node evaluating
        // them as next steps

        for(x <- -1 to 1) {
          for(y <- -1 to 1) {
            // not a neighbor, it's the current tile
            if((x == 0) && (y == 0)) { /* continue; */ }
            else {

              val xp = x + current.x
              val yp = y + current.y

              if(terrain.isTileValid(xp, yp) && excludes(terrain.tileAt(xp, yp))) {

                def function(): Unit = {

                  // the totalMovementCost to get to this node is totalMovementCost the current plus the movement
                  // totalMovementCost to reach this node. Note that the heursitic value is only used
                  // in the sorted open list

                  val neighbor = nodes(xp)(yp)

                  // If the neighbor is on the closed list already, continue...
                  if(closed.contains(neighbor)) return

                  val isDiagonal = {
                    val x_abs = abs(x)
                    val y_abs = abs(y)
                    x_abs == 1 && x_abs == y_abs
                  }

                  // Then calculate the cost for the next step, save some overhead at the closed-list-check
                  val nextStepCost = {
                    var ret = current.totalMovementCost + neighbor.stepMovementCost
                    if(isDiagonal) ret += 0.0001
                    ret
                  }

                  val openListContainsNeighbor = open.list.contains(neighbor)

                  // if the new totalMovementCost we've determined for this node is lower than
                  // it has been previously make sure the node hasn't
                  // determined that there might have been a better path to get to
                  // this node so it needs to be re-evaluated
                  if(openListContainsNeighbor && nextStepCost >= neighbor.totalMovementCost) return

                  // Set the parent to the current one, there's a better path to it.
                  neighbor.parent = current

                  // If the node is not in the open list, add it, since it could be a possible path.
                  if(!openListContainsNeighbor) open.list += neighbor
                }

                // Execute the function above.
                function()

              }

            }
          }
        }

      }
    }

    // since we've run out of search
    // there was no path. Just return null
    if(nodes(tx)(ty).parent eq null) return None

    // At this point we've definitely found a path so we can use the parent
    // references of the nodes to find our way from the target location back
    // to the start recording the nodes on the way.

    val stepList = mutable.MutableList[Path.Step]()
    var target = nodes(tx)(ty)
    while(target ne nodes(sx)(sy)) {
      // FIXME: This line sometimes causes NullPointerExceptions [at moveTowards]
      // Bug hunt: try to prepend the step to the list of needed steps.
      // If null pointer exception, log var map below and rethrow.
      try Path.Step(target.x, target.y, terrain.tileAt(target.x, target.y).requiredMovementPoints) +=: stepList
      catch {
        case ex: NullPointerException =>
          LogFacility.log(ScalaUtil.errorMessage("NullPointerException in AStarPathfinder", ex, Map(
            "tileAt(target.x, target.y)" -> terrain.tileAt(target.x, target.y),
            "target.x" -> target.x,
            "target.y" -> target.y,
            "stepList" -> stepList,
            "target" -> target,
            "sx" -> sx,
            "sy" -> sy
          )))
          throw ex
      }
      target = target.parent
    }

    // Prepend the starting position (with required movement points set as zero).
    Path.Step(sx, sy, 0) +=: stepList

    // That's it.
    Some(Path(stepList.toSeq))
  }

  private class SortedList {

    import scala.collection.mutable

    val list = mutable.ArrayBuffer[Node]()

    def nodeWithLeastFValue: Node = {
      var ret: Node = null
      list foreach { e =>
        if(ret eq null) ret = e
        else if(e.depth >= maxSearchDepth) { /* Do nothing... */ }
        else if(e.f < ret.f) ret = e
      }
      /*
      val ret = list.foldLeft(null.asInstanceOf[Node]) { (p, e) =>
        if(p eq null) e
        else if(e.depth >= maxSearchDepth) p
        else if(e.f < p.f) e
        else p
      }
      */
      ret
    }

    def remove(a: Node): Unit = list.remove(list.indexOf(a))

  }

  private class Node(val tile: Tile) {

    def x = tile.getGridX
    def y = tile.getGridY

    /** The parent node. */
    var parent: Node = null

    /** How deep the node is in the path. */
    def depth: Int = {

      @tailrec
      def rec(loopCount: Int = 0, parentReference: Node = parent): Int = {
        if(parentReference eq null) loopCount
        else {
          rec(loopCount + 1, parentReference.parent)
        }
      }

      // Return the result from the recursive function.
      rec()
    }

    /** The total movement costs to get to this tile. */
    def totalMovementCost: Double = {

      @tailrec
      def rec(count: Int = tile.requiredMovementPoints, parentReference: Node = parent): Int = {
        if(parentReference eq null) count
        else {
          rec(count + tile.requiredMovementPoints, parentReference.parent)
        }
      }

      // Return the result from the recursive function (again)
      rec()
    }

    def stepMovementCost: Double = tile.requiredMovementPoints

    def approximateToTargetSq: Double = pow(Node.target.x - x, 2) + pow(Node.target.y - y, 2)
    def approximateToTarget: Double = sqrt(approximateToTargetSq)

    def f: Double = totalMovementCost + approximateToTargetSq

    override def equals(obj: scala.Any): Boolean = obj match {
      case n: Node => n.x == x && n.y == y
      case _ => false
    }

    def ==(node: Node) = equals(node)
  }

  private object Node {
    /** The target to which to navigate to. */
    var target: Node = null
  }

}

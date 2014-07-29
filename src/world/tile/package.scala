package world

import entity.path.Direction
import entity.path.Direction._

// TODO When implementing any viewport rotations, rewrite the ´´angleX and ´´angleY functions.
/**
 *
 * @author Josip Palavra
 * @version 20.06.2014
 */
package object tile {

  /**
   * Returns the (valid, meaning, not null) neighbors of a tile.
   * @param tile The tile from which the neighbors are drawn.
   * @return The neighbor tiles.
   */
  def neighborsOf(tile: IBaseTile) = {
    val n = (North, tile.north())
    val nw = (Northwest, tile.northwest())
    val w = (West, tile.west())
    val sw = (Southwest, tile.southwest())
    val s = (South, tile.south())
    val se = (Southeast, tile.southeast())
    val e = (East, tile.east())
    val ne = (Northeast, tile.northeast())
    val hashtable = scala.collection.mutable.HashMap[Direction, IBaseTile]()
    if(n._2 ne null) hashtable.update(n._1, n._2)
    if(nw._2 ne null) hashtable.update(nw._1, nw._2)
    if(w._2 ne null) hashtable.update(w._1, w._2)
    if(sw._2 ne null) hashtable.update(sw._1, sw._2)
    if(s._2 ne null) hashtable.update(s._1, s._2)
    if(se._2 ne null) hashtable.update(se._1, se._2)
    if(e._2 ne null) hashtable.update(e._1, e._2)
    if(ne._2 ne null) hashtable.update(ne._1, ne._2)
    hashtable.toList.toMap
  }

  def movementCosts(center: IBaseTile, neighbor: Direction): Option[Int] = ???

  /**
   * Calculates the angle in the x space for the specified tile.
   * Planned to be used as help for the movement system of Entity.
   * @param tile The specified tile.
   * @return Nothing yet.
   */
  def angleX(tile: IBaseTile): Int = ???

  /**
   * Calculates the angle in the y space for the specified tile.
   * Planned to be used as help for the movement system of Entity.
   * @param tile The specified tile.
   * @return Nothing yet.
   */
  def angleY(tile: IBaseTile): Int = ???

}

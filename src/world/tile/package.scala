package world

import entity.path.Direction
import entity.path.Direction._

import scala.collection.immutable.HashMap

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
    HashMap((North, tile.north()), (Northwest, tile.northwest()), (West, tile.west()), (Southwest, tile.southwest()),
      (South, tile.south()), (Southeast, tile.southeast()), (East, tile.east()), (Northeast, tile.northeast()))
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

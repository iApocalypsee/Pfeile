package world

import entity.path.Direction
import entity.path.Direction._

import scala.collection.immutable.HashMap

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

  /**
   * Calculates the difference of the movement costs
   * @param center
   * @param neighbor
   * @return
   */
  def movementCosts(center: IBaseTile, neighbor: Direction): Option[Int] = {
    val neighborTiles = neighborsOf(center)
    None
  }

  def downhill(center: IBaseTile, neighbor: IBaseTile): Boolean = {
    val neighborTiles = neighborsOf(center)
    for(tile <- neighborTiles) {

    }
    ???
  }

}

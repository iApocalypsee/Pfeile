package entity

import world.Tile
import geom.interfaces.VectorChain
import scala.collection.mutable

/**
 *
 * @author Josip
 * @version 27.05.2014
 */
trait PathFinder {

  /**
   * Calculates the shortest way to the destination tile.
   * @param to The destination tile.
   * @param ignores A function describing all tiles that should be ignored.
   *               If a tile should not be permitted, the function should return false.
   *               Default value permits all tiles.
   * @return A vector describing the shortest way to the tile.
   */
  def shortest(to: Tile, ignores: mutable.LinkedList[Tile]): VectorChain

  /**
   * Calculates the fastest way to the destination tile.
   * @param to The destination tile.
   * @param ignores A function describing all tiles that should be ignored.
   *               If a tile should not be permitted, the function should return false.
   *               Default value permits all tiles.
   * @return A vector describing the fastest way to the tile.
   */
  def fastest(to: Tile, ignores: Array[Tile]): VectorChain

}

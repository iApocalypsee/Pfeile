package entity

import world.Tile
import geom.{PointRef, VectorChainDef}
import geom.interfaces.VectorChain

/*
/**
 *
 * @author Josip
 * @version 27.05.2014
 */
trait MoveableEntity extends Entity {

  private object VectorBeginEnd extends Enumeration {
    type VectorBeginEnd = Value
    val Begin, End = Value
  }

  private var _movement = 0
  private var _currentpath: VectorChain = new VectorChainDef(new PointRef(gridX, gridY), new PointRef(gridX, gridY))
  private val _pathfinder: PathFinder = null

  override def turnover: Unit = {

  }

  private def walkpath: Unit = {

  }

  /**
   * Sets the path of the movable entity to the tile.
   * If the tile is the tile on which the entity stands on currently,
   * the method returns.
   * @param tile The tile to move to.
   */
  def move(tile: Tile): Unit = {
    if(!world.isTileValid(tile.getGridX, tile.getGridY)) throw new ArrayIndexOutOfBoundsException
    val path = pathfinder.fastest(tile, Array[Tile]())
    _currentpath = path
  }

  /**
   * Sets the path of the movable entity to the coordinates.
   * If the tile specified is the tile on which the entity stands on currently
   * or the tile is not accessable, the method returns.
   * @param coord The coordinates to move to.
   */
  def move(coord: (Int, Int)): Unit = ???

  def movementPoints = _movement

  def pathfinder = _pathfinder

}

*/

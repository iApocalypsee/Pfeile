package entity

import entity.path.PathFinder
import geom.VecChain
import geom.interfaces.VectorChain
import world.IBaseTile
import world.tile.SeaTile


/**
 *
 * @author Josip
 * @version 27.05.2014
 */
trait MoveableEntity extends Entity with Visioner {

  private var _movement = 1
  private var _crtpath: VectorChain = new VecChain()
  val pathfinder = new PathFinder(this)

  override def turnover: Unit = {
    walkpath
  }

  /**
   * Moves the entity to a specified tile.
   * @param tile The tile.
   */
  def move(tile: IBaseTile): Unit = {
    val p = pathfinder.find(tile).without(_.isInstanceOf[SeaTile]).toPath
    if(p.isDefined) {
      _crtpath = entity.path.pathToVector(p.get)
    } else {
      println("No path defined: (move(x: Int, y: Int))")
    }
  }

  /**
   * Moves the unit relatively to the specified coordinates.
   * @param x The relative x amount.
   * @param y The relative y amount.
   */
  def move(x: Int, y: Int): Unit = move(world.getTileAt(x, y))

  /**
   * Returns the destination tile.
   * @return The destination tile.
   */
  def destination() = currentPath.get.tiles.last

  /**
    * Returns the movement points left with the entity for this turn.
   * @return The movement points left.
   */
  def movementPoints = _movement

  /**
    * Returns the current walking path of the movable entity.
   * @return The path on which the entity walks.
   */
  def currentPath = entity.path.vectorToPath(_crtpath)

  /**
   * Entity walks his path as much as he can.
   */
  private def walkpath(): Unit = {
    def recur(): Unit = {
      val tile = currentPath.get(0)
      if(movementPoints < tile.getRequiredMovementPoints) {
        // the entity has not enough movement points to enter the next tile, so exit the loop
        resetMovement()
        return
      } else {
        // if the entity has movement points left, then walk along the path as much as the entity can
        gridX = tile.getGridX
        gridY = tile.getGridY
        _movement -= tile.getRequiredMovementPoints
        _crtpath.remove(0)
      }
    }
    recur()
  }

  /**
   * Sets (teleports) the entity's x position to the specified one.
   * If specified coordinate is out of bounds, an exception is thrown.
   * @param x The x coordinate.
   */
  override protected def gridX_=(x: Int): Unit = {
    super.gridX_=(x)
    visionObject.updateVisionables()
  }

  /**
   * Sets (teleports) the entity's y position to the specified one.
   * If specified coordinate is out of bounds, an exception is thrown.
   * @param y The y coordinate.
   */
  override protected def gridY_=(y: Int): Unit = {
    super.gridY_=(y)
    visionObject.updateVisionables()
  }

  private def resetMovement() = _movement = 1

}

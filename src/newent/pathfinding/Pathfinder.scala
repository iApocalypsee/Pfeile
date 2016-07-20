package newent.pathfinding

import newent.MovableEntity

trait Pathfinder {

  /**
    * Finds a path according to the implemented logic.
    *
    * @param moveable The moveable entity for which to find the path.
    * @param tx The target x position.
    * @param ty The target y position.
    * @return An optional path, if the pathfinder was able to calculate a path.
    */
  def findPath(moveable: MovableEntity, tx: Int, ty: Int): Option[Path]

}

object DefaultPathfinder extends AStarPathfinder(50, tile => true)

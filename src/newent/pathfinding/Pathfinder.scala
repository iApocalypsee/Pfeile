package newent.pathfinding

import newent.MoveableEntity

/**
 *
 * @author Josip Palavra
 */
trait Pathfinder {

  /** Finds a path according to the implemented logic.
    *
    * @param moveable The moveable entity for which to find the path.
    * @param tx The target x position.
    * @param ty The target y position.
    * @return An optional path.
    */
  def findPath(moveable: MoveableEntity, tx: Int, ty: Int): Option[Path]

}

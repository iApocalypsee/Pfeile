package newent

import newent.event.LocationChangedEvent
import newent.pathfinding.{Path, Pathfinder}

import scala.util.control.Breaks._

/** Represents an entity that can move. */
trait MovableEntity extends Entity with StatisticalEntity {

  /** The pathfinder for the movable entity. */
  val pathfinderLogic: Pathfinder

  /** The default movement points that the entity has. */
  def defaultMovementPoints: Int

  /** The current path on which the entity moves. */
  private var _currentPath: Option[Path] = None
  // Ditto.
  private var _currentMovementPoints = defaultMovementPoints

  /** The current movement points the entity has left in this turn. */
  def currentMovementPoints = _currentMovementPoints

   /** Adding new movementPoints to the currentMovementPoints.
     *  If <code>additionalMovementPoints + currentMovementPoints() < 0</code>, the currentMovementPoints() is set to <code>0</code>.
     *  Only the current number of movement points is changed, so at the end of the turn the number is set to
     *  <code>defaultMovementPoints()</code> again.
     *
     * @param additionalMovementPoints the number with which the <code>currentMovementPoints()</code> should be increased
     */
  def addMovementPoints (additionalMovementPoints: Byte) = {
      if (_currentMovementPoints + additionalMovementPoints < 0)
         _currentMovementPoints = 0
      else
         _currentMovementPoints = _currentMovementPoints + additionalMovementPoints
  }

  /** Moves the entity.
    *
    * @param x The amount of units to go the x direction.
    * @param y The amount of units to go the y direction.
    */
  def move(x: Int = 0, y: Int = 0): Unit = moveTowards(getGridX + x, getGridY + y)

  /** Tells the entity to move towards the specified position.
    *
    * @param x The x position.
    * @param y The y position.
    */
  def moveTowards(x: Int, y: Int): Unit = {
    if (!tileLocation.terrain.isTileValid(x, y)) throw new RuntimeException(s"Tile ($x|$y) is not valid.")
    _currentPath = pathfinderLogic.findPath(this, x, y)
    moveAlong()
  }

  /** Moves the entity along his current path that has been set by the [[move( I n t, I n t )]] method.
    *
    * If no path is set, this method does nothing.
    */
  def moveAlong(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    breakable {
      // Only walk along if the entity has a path associated right now.
      if (_currentPath.isDefined) {
        val p = _currentPath.get
        for (step <- p.steps) {
          if (currentMovementPoints >= step.reqMovementPoints) {

            // Subtract the movement points...
            _currentMovementPoints -= step.reqMovementPoints

            // And fire the event to the location changed delegate
            val prevX = getGridX
            val prevY = getGridY
            setGridPosition(step.x, step.y)
            onLocationChanged.callAsync(LocationChangedEvent(prevX, prevY, getGridX, getGridY, this))
            _currentPath = Some(Path(p.steps.tail))

            // If it is the last step in the path, I have to remove the path, since it is walked already...
            if (step eq p.steps.last) {
              _currentPath = None
            }
          } else break()
        }
      }
    }
  }

  onTurnCycleEnded += { () =>
    moveAlong()
    _currentMovementPoints = defaultMovementPoints
  }

}

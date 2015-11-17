package newent

import geom.functions.FunctionCollection
import newent.event.LocationChangedEvent
import newent.pathfinding.{DefaultPathfinder, Path, Pathfinder}

import scala.util.control.Breaks._

/**
  * An entity which movement is limited by how many movement points it has available.
  */
trait MovableEntity extends Entity with StatisticalEntity {

  /**
    * The pathfinder instance for the entity.
    * Defaults to a standard A-Star pathfinder.
    * Can be overridden for more control over the pathfinding algorithm.
    * @see [[newent.pathfinding.DefaultPathfinder$]]
    */
  def pathfinderLogic: Pathfinder = DefaultPathfinder

  /** The default movement points that the entity has. */
  def defaultMovementPoints: Int

  /** The current path on which the entity moves. */
  private var _currentPath = Option.empty[Path]

  /**
    * Self-evident.
    */
  private var _currentMovementPoints = defaultMovementPoints

  /** The current movement points the entity has left in this turn. */
  def currentMovementPoints = _currentMovementPoints

  /**
    * Sets the current movement points to an absolute value.
    * Any negative values are immediately assumed to be zero.
    * @param x The new movement points.
    */
  def currentMovementPoints_=(x: Int) = _currentMovementPoints = FunctionCollection.clamp(x, 0, Integer.MAX_VALUE)

  /**
    * Adding new movementPoints to the currentMovementPoints.
    *  If <code>additionalMovementPoints + currentMovementPoints() < 0</code>, the currentMovementPoints() is set to <code>0</code>.
    *  Only the current number of movement points is changed, so at the end of the turn the number is set to
    *  <code>defaultMovementPoints()</code> again.
    *
    * @param additionalMovementPoints the number with which the <code>currentMovementPoints()</code> should be increased
    */
  def addMovementPoints(additionalMovementPoints: Int) = {
    _currentMovementPoints = FunctionCollection.clamp(_currentMovementPoints + additionalMovementPoints, 0, Integer.MAX_VALUE)
  }

  /**
    * Moves the entity.
    *
    * @param x The amount of units to go the x direction.
    * @param y The amount of units to go the y direction.
    */
  override def move(x: Int = 0, y: Int = 0): Unit = moveTowards(getGridX + x, getGridY + y)

  /**
    * Tells the entity to move towards the specified position.
    *
    * @param x The x position.
    * @param y The y position.
    */
  def moveTowards(x: Int, y: Int): Unit = {
    if (!tileLocation.terrain.isTileValid(x, y)) throw new RuntimeException(s"Tile ($x|$y) is not valid.")
    _currentPath = pathfinderLogic.findPath(this, x, y)
    moveAlong()
  }

  /**
    * Moves the entity along his current path that has been set by the [[move( I n t, I n t )]] method.
    *
    * If no path is set, this method does nothing.
    */
  def moveAlong(): Unit = {
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
            onLocationChanged(LocationChangedEvent(prevX, prevY, getGridX, getGridY, this))
            _currentPath = Some(Path(p.steps.tail))

            // If it is the last step in the path, I have to remove the path, since it is walked already...
            if (step == p.steps.last) {
              _currentPath = None
            }
          }
          else break()
        }
      }
    }
  }

  onTurnCycleEnded += { () =>
    moveAlong()
    _currentMovementPoints = defaultMovementPoints
  }

}

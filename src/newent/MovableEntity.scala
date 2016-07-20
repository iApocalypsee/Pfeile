package newent

import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import geom.functions.FunctionCollection
import newent.pathfinding.{DefaultPathfinder, Path, Pathfinder}
import world.Tile

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.compat.java8._

/**
  * An entity which movement is limited by how many movement points it has available.
  */
trait MovableEntity extends Entity with StatisticalEntity {

  /**
    * The pathfinder instance for the entity.
    * Defaults to a standard A-Star pathfinder.
    * Can be overridden for more control over the pathfinding algorithm.
    *
    * @see [[newent.pathfinding.DefaultPathfinder$]]
    */
  def pathfinderLogic: Pathfinder = DefaultPathfinder

  /**
    * The default movement points that the entity has.
    */
  def defaultMovementPoints: Int

  /**
    * The current path on which the entity moves.
    */
  private var _currentPath = Option.empty[Path]

  /**
    * Self-evident.
    */
  private var _currentMovementPoints = defaultMovementPoints

  /**
    * Called when this entity has moved some tiles.
    *
    * @param traversedTiles Immutable list of the tiles it has traversed this time.
    */
  protected def onTraverseSteps(traversedTiles: MovableEntity.MovedEvent): Unit = {
  }

  /**
    * The path on which this entity is currently walking along.
    * This option represents only the path this entity needs to walk, not the complete
    * path it has traversed since it moved.
    */
  def currentPath = _currentPath

  /**
    * @see [[newent.MovableEntity#currentPath()]]
    */
  def getCurrentPath = currentPath.asJava

  /**
    * The current movement points the entity has left in this turn.
    */
  def currentMovementPoints = _currentMovementPoints

  /**
    * Sets the current movement points to an absolute value.
    * Any negative values are immediately assumed to be zero.
    *
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
  def addMovementPoints(additionalMovementPoints: Int) = currentMovementPoints += additionalMovementPoints

  /**
    * Moves the entity by specified x and y coordinates.
    * Does not move the entity directly to given tile, but rather calculates a path towards
    * given tile.
    *
    * @param x The amount of units to go the x direction.
    * @param y The amount of units to go the y direction.
    */
  override def move(x: Int, y: Int): Unit = moveTowards(getGridX + x, getGridY + y)

  /**
    * Tells the entity to move towards the specified position.
    *
    * @param x The x position.
    * @param y The y position.
    */
  def moveTowards(x: Int, y: Int): Unit = {
    require(tileLocation.terrain.isTileValid(x, y), s"Tile ($x|$y) is not valid.")
    _currentPath = pathfinderLogic.findPath(this, x, y)
    moveAlong()
  }

  /**
    * Moves the entity along his current path that has been set by the [[move(Int,Int)]] method.
    *
    * If no path is set, this method does nothing.
    */
  def moveAlong(): Unit = synchronized(
    if (_currentPath.isDefined) {
      var path = _currentPath.get.steps.toList

      // Utility variable for indicating if the while loop should terminate
      var moveBreakCondition = false

      // Last step the entity has made during this moveAlong() call.
      // Required for firing the MovedEvent.
      var lastStep: Path.Step = null

      // Actual moving.
      while (!moveBreakCondition) {
        // If no more steps are to be done
        if (path.isEmpty) moveBreakCondition = true
        else {
          val nextStep = path.head
          if (nextStep.reqMovementPoints <= currentMovementPoints) {
            _currentMovementPoints -= nextStep.reqMovementPoints
            setGridPosition(nextStep.x, nextStep.y)
            path = path.tail
            lastStep = nextStep
          }
          else moveBreakCondition = true
        }
      }

      // Call the 'onTraverseSteps' callback if this entity has crossed at least one tile.
      val walked = _currentPath.get.steps diff path
      if (walked.size >= 2) {
        val tilesForEvent = walked.map(step => world.terrain.tileAt(step.x, step.y))
        val fireEvent = MovableEntity.MovedEvent(walked.asJava, tilesForEvent.asJava)
        onTraverseSteps(fireEvent)
      }

      // Reset current path.
      _currentPath =
        if (path.nonEmpty) {
          if(walked.isEmpty) Option(Path(path))
          else Option(Path(Path.Step(lastStep.x, lastStep.y, 0) :: path))
        } else {
          None
        }
    })

  onTurnEnded += { () =>
    // Before resetting movement points, move this entity as far as it can still get
    moveAlong()
    // Reset the movement points
    _currentMovementPoints = defaultMovementPoints
  }

}

object MovableEntity {

  case class MovedEvent(@BeanProperty val steps: IList[Path.Step], @BeanProperty val tiles: IList[Tile])

}

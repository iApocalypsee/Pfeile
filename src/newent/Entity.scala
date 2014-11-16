package newent

import java.awt.Point

import comp.{DisplayRepresentable, RawComponent}
import general.Delegate
import newent.event.LocationChangedEvent
import newent.pathfinding.{Path, Pathfinder}
import player.BoardPositionable
import world.WorldLike

import scala.util.control.Breaks._

sealed trait EntityLike extends BoardPositionable with DisplayRepresentable {

  /** The world in which the entity is living. */
  val world: WorldLike

  /** The tile on which the entity is currently on. */
  def tileLocation = world.terrain.tileAt( getGridX, getGridY )

  /** Called when the location of the entity changes. */
  val onLocationChanged = Delegate.create[LocationChangedEvent]

  /** The name of the entity. Must be unique. */
  val name: String

  /** Delegate for the entity that a player cycle has been completed. */
  val onTurnCycleEnded = Delegate.createZeroArity
}

/** (Another) Base class for all entities. The new-old entity traits used old interfaces
  * so I have to replace them aswell. <p>
  *
  * All implementations for entity are required to be written in Scala, because Java cannot handle
  * the "Stackable trait" pattern offered by Scala.
  *
  * @param n The name of the entity. Defaults to null.
  * @param world The world of the entity. Should not be null.
  * @param spawnPosition The position where the entity spawns.
  */
abstract class Entity(override val world: WorldLike, spawnPosition: (Int, Int), n: String = null) extends EntityLike {

  require( world ne null )

  private var _x = spawnPosition._1
  private var _y = spawnPosition._2

  /** The name of the entity. It is never going to change. */
  lazy val name: String = {
    if (n eq null) hashCode.toString
    else if (world.entities.entityList.filter { e => e.name == n}.isEmpty) n
    else throw new NotUniqueNameException( n )
  }

  /// Ditto.
  def this(world: WorldLike, spawnPoint: Point, n: String) = this( world, (spawnPoint.x, spawnPoint.y), n )

  /** Returns the current x position of the entity. */
  override def getGridX: Int = _x

  /** Returns the current y position of the entity. */
  override def getGridY: Int = _y

  /** Sets the x position of the entity. Should not be called directly from outside classes!
    * @param x The new x position to set to.
    */
  protected def setGridX(x: Int) = setGridPosition(x, _y)

  /** Sets the y position of the entity. Should not be called directly from outside classes!
    * @param y The new y position to set to.
    */
  protected def setGridY(y: Int) = setGridPosition(_x, y)

  protected def setGridPosition(x: Int = _x, y: Int = _y) = {
    _x = x
    _y = y
  }

}

/** Represents an entity that can move. */
trait MoveableEntity extends Entity {

  /** The pathfinder for the moveable entity. */
  val pathfinderLogic: Pathfinder

  /** The default movement points that the entity has. */
  def defaultMovementPoints: Int

  /** The current path on which the entity moves. */
  private var _currentPath: Option[Path] = None
  // Ditto.
  private var _currentMovementPoints = defaultMovementPoints

  /** The current movement points the entity has left in this turn. */
  def currentMovementPoints = _currentMovementPoints

  /** Moves the entity.
    *
    * @param x The amount of units to go the x direction.
    * @param y The amount of units to go the y direction.
    */
  def move(x: Int = 0, y: Int = 0): Unit = moveTowards( getGridX + x, getGridY + y )

  /** Tells the entity to move towards the specified position.
    *
    * @param x The x position.
    * @param y The y position.
    */
  def moveTowards(x: Int, y: Int): Unit = {
    if (!tileLocation.terrain.isTileValid( x, y )) throw new RuntimeException( s"Tile ($x|$y) is not valid." )
    _currentPath = pathfinderLogic.findPath( this, x, y )
    moveAlong( )
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
            onLocationChanged.callAsync( LocationChangedEvent( prevX, prevY, getGridX, getGridY, this ) )
            _currentPath = Some( Path( p.steps.tail ) )

            // If it is the last step in the path, I have to remove the path, since it is walked already...
            if (step eq p.steps.last) {
              _currentPath = None
            }
          } else break( )
        }
      }
    }
  }

  onTurnCycleEnded += { () =>
    moveAlong()
    _currentMovementPoints = defaultMovementPoints
  }

}

/** Represents an entity that can teleport. */
trait TeleportableEntity extends Entity {

  /** Teleports the entity to the given location.
    *
    * @param x The x coordinate to teleport to.
    * @param y The y coordinate to teleport to.
    */
  def teleport(x: Int = getGridX, y: Int = getGridY): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val prevX = getGridX
    val prevY = getGridY
    setGridX( x )
    setGridY( y )
    onLocationChanged.callAsync( LocationChangedEvent( prevX, prevY, getGridX, getGridY, this ) )
  }

}

trait InventoryEntity extends Entity {

  val inventory: InventoryLike = new DefaultInventory

}

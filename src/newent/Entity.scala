package newent

import java.awt.Point

import comp.RawComponent
import general.Delegate
import newent.event.LocationChangedEvent
import player.BoardPositionable
import world.WorldLike

import scala.concurrent.ExecutionContext.Implicits.global

sealed trait EntityLike extends BoardPositionable with RawComponent {

  /** The world in which the entity is living. */
  val world: WorldLike

  /** The tile on which the entity is currently on. */
  def tileLocation = world.terrain.tileAt(getGridX, getGridY)
  
  /** Called when the location of the entity changes. */
  val onLocationChanged = Delegate.create[LocationChangedEvent]

  /** The name of the entity. Must be unique. */
  val name: String

}

/** (Another) Base class for all entities. The new-old entity traits used old interfaces
  * so I have to replace them aswell. <p>
  *
  * All implementations for entity are required to be written in Scala, because Java cannot handle
  * the "Stackable trait" pattern offered by Scala.
  *
  * @param n The name of the entity. Defaults to null.
  * @param world The world of the entity. Should not be null.
  */
abstract class Entity(override val world: WorldLike, spawnPosition: (Int, Int), n: String = null) extends EntityLike {

  require(world ne null)

  private var _x = spawnPosition._1
  private var _y = spawnPosition._2

  /** The name of the entity. It is never going to change. */
  val name: String = {
    if(n eq null) hashCode.toString
    else if(world.entities.entityList.filter { e => e.name == n }.isEmpty) n
    else throw new NotUniqueNameException(n)
  }

  /// Ditto.
  def this(world: WorldLike, spawnPoint: Point, n: String) = this(world, (spawnPoint.x, spawnPoint.y), n)

  override def getGridX: Int = _x
  override def getGridY: Int = _y

  protected def setGridX(x: Int) = _x = x
  protected def setGridY(y: Int) = _y = y

}

/** Represents an entity that can move. */
trait MoveableEntity extends Entity {

  /** Moves the entity.
    *
    * @param x The amount of units to go the x direction.
    * @param y The amount of units to go the y direction.
    */
  def move(x: Int = 0, y: Int = 0): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val prevX = getGridX
    val prevY = getGridY
    setGridX(getGridX + x)
    setGridY(getGridY + y)
    onLocationChanged.callAsync(LocationChangedEvent(prevX, prevY, getGridX, getGridY, this))
  }

}

/** Represents an entity that can teleport. */
trait TeleportableEntity extends Entity {

  def teleport(x: Int = getGridX, y: Int = getGridY): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val prevX = getGridX
    val prevY = getGridY
    setGridX(x)
    setGridY(y)
    onLocationChanged.callAsync(LocationChangedEvent(prevX, prevY, getGridX, getGridY, this))
  }

}

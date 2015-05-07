package newent

import java.awt.Point

import comp.DisplayRepresentable
import general.Delegate
import newent.event.LocationChangedEvent
import player.BoardPositionable
import world.WorldLike

sealed trait EntityLike extends BoardPositionable with DisplayRepresentable {

  /** The world in which the entity is living. */
  val world: WorldLike

  /** The tile on which the entity is currently on. */
  def tileLocation = world.terrain.tileAt(getGridX, getGridY)

  /** Called when the location of the entity changes. */
  val onLocationChanged = Delegate.create[LocationChangedEvent]

  /** The name of the entity. Must be unique. */
  val name: String

  /** Delegate for the entity that a player cycle has been completed. */
  val onTurnCycleEnded = Delegate.createZeroArity
}

/**
  * (Another) Base class for all entities. The new-old entity traits used old interfaces
  * so I have to replace them aswell. <p>
  *
  * All implementations for entity are required to be written in Scala, because Java cannot handle
  * the "Stackable trait" pattern offered by Scala.
  *
  * The important thing about the entity is that an entity in code '''does not have to represent a living object.'''
  * It can be treasures, decoration, loot, walls, whatsoever.
  *
  * @param n The name of the entity. Defaults to null.
  * @param world The world of the entity. Should not be null.
  * @param spawnPosition The position where the entity spawns.
  */
// TODO Implement level systems for entities, if possible.
abstract class Entity(override val world: WorldLike, spawnPosition: (Int, Int), n: String = null) extends EntityLike {

  require(world ne null)

  private var _x = spawnPosition._1
  private var _y = spawnPosition._2

  /** The name of the entity. It is never going to change. */
  val name: String = {
    if (n eq null) hashCode.toString
    else if (world.entities.entityList.filter { e => e.name == n }.isEmpty) n
    else throw new NotUniqueNameException(n)
  }

  /// Ditto.
  def this(world: WorldLike, spawnPoint: Point, n: String) = this(world, (spawnPoint.x, spawnPoint.y), n)

  /** Returns the current x position of the entity. */
  override def getGridX: Int = _x

  /** Returns the current y position of the entity. */
  override def getGridY: Int = _y

  /**
    * Sets the x position of the entity. Should not be called directly from outside classes!
    * @param x The new x position to set to.
    */
  protected def setGridX(x: Int) = setGridPosition(x, _y)

  /**
    * Sets the y position of the entity. Should not be called directly from outside classes!
    * @param y The new y position to set to.
    */
  protected def setGridY(y: Int) = setGridPosition(_x, y)

  protected def setGridPosition(x: Int = _x, y: Int = _y) = {
    val oldX = _x
    val oldY = _y
    _x = x
    _y = y
    onLocationChanged(LocationChangedEvent(oldX, oldY, _x, _y, this))
  }

}

/** Represents an entity that can teleport. */
trait TeleportableEntity extends Entity {

  /**
    * Teleports the entity to the given location.
    *
    * @param x The x coordinate to teleport to.
    * @param y The y coordinate to teleport to.
    */
  def teleport(x: Int = getGridX, y: Int = getGridY): Unit = {
    val prevX = getGridX
    val prevY = getGridY
    setGridX(x)
    setGridY(y)
    onLocationChanged(LocationChangedEvent(prevX, prevY, getGridX, getGridY, this))
  }
}

trait InventoryEntity extends Entity {

  val inventory: InventoryLike = new DefaultInventory

}

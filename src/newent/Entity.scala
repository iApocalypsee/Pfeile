package newent

import java.awt.Point
import java.util.Collections

import com.sun.istack.internal.{NotNull, Nullable}
import comp.DisplayRepresentable
import general.Delegate
import newent.event.LocationChangedEvent
import player.BoardPositionable
import world.{TileLike, WorldLike}

import scala.collection.JavaConverters._
import scala.collection.{JavaConversions, mutable}
import scala.collection.mutable.ArrayBuffer
import scala.{NotNull => DeprecatedScalaNotNull}

sealed trait EntityLike extends BoardPositionable with DisplayRepresentable {

  /**
    * The world in which the entity is living.
    */
  val world: WorldLike

  /**
    * The tile on which the entity is currently on.
    */
  def tileLocation = world.terrain.tileAt(getGridX, getGridY)

  /**
    * The list of coordinates that this entity occupies for itself as well.
    * For a simple unit (let's say a Swordsman), this list will only contain the element `Point(entity.getGridX, entity.getGridY)`
    */
  def boardShape: List[Point]

  /**
    * Similar to [[boardShape]], only that the elements in the list are now tiles instead of plain points.
    */
  def boardShapeTiles = boardShape.map(p => world.terrain.getTileAt(p.x, p.y))

  /**
    * Unmodifiable Java list of coordinates incorporated by this entity.
    */
  def getBoardShape = Collections.unmodifiableList(boardShape.asJava)

  /**
    * Similar to [[getBoardShape]], but with tile objects instead of plain points.
    */
  def getBoardShapeTiles = Collections.unmodifiableList(boardShapeTiles.asJava)

  /**
    * Checks if the specified coordinate is occupied by this entity.
    * @param x The x coordinate of a tile.
    * @param y The y coordinate of a tile.
    */
  def containsCoordinate(x: Int, y: Int) = boardShape.exists(p => p.x == x && p.y == y)

  /**
    * Checks if the specified tile is occupied by this entity.
    * @param t See description.
    */
  def containsTile(t: TileLike) = boardShapeTiles.contains(t)

  /**
    * Called when the location of the entity changes.
    */
  val onLocationChanged = Delegate.create[LocationChangedEvent]

  /**
    * The name of the entity. Must be unique.
    */
  val name: String

  /**
    * Delegate for the entity that a player cycle has been completed.
    */
  val onTurnCycleEnded = Delegate.createZeroArity

}

/**
  * (Another) Base class for all entities. The new-old entity traits used old interfaces
  * so I have to replace them aswell. <p>
  *
  * All implementations for entity are required to be written in Scala, because Java cannot handle
  * the "Stackable trait" pattern offered by Scala.
  *
  * The important thing about this class in code is that it '''does not have to represent a living object.'''
  * It can be treasures, decoration, loot, walls, whatsoever. Real living entities are called to life by
  * mixing in/implementing the LivingEntity trait/interface.
  *
  * @param world The world of the entity.
  * @param initX The x position on which the entity will spawn.
  * @param initY The y position on which the entity will spawn.
  * @param initBoardShape What tiles in the world should be considered part of this entity as well?
  *                       Notice that - in contrast to the regular [[newent.Entity#boardShape()]] method - the board shape coordinates
  *                       given to the constructor are normalized, so the point `(0|0)` is the westernmost point of
  *                       the new entity's shape and __not the tile at global position `(0|0)`!__
  *                       Scala: defaults to `Seq.empty`.
  * @param n The name of the entity. Scala: defaults to `null`.
  */
abstract class Entity(@NotNull override val world: WorldLike, initX: Int, initY: Int, initBoardShape: Seq[Point] = Seq.empty, @Nullable n: String = null) extends EntityLike {

  require(world != null)

  def this(world: WorldLike, x: Int, y: Int, name: String) = {
    this(world, x, y, Seq.empty, name)
  }

  def this(world: WorldLike, x: Int, y: Int, jBoardShape: java.util.List[Point], name: String) = {
    this(world, x, y, JavaConversions.asScalaBuffer(jBoardShape), name)
  }

  def this(world: WorldLike, x: Int, y: Int) = this(world, x, y, null)

  /**
    * The current x position of the entity.
    */
  private var _x = initX

  /**
    * The current y position of the entity.
    */
  private var _y = initY

  /**
    * The current entity's shape.
    */
  private var m_boardShape: ArrayBuffer[Point] = {
    val ptList = if (initBoardShape.isEmpty) Seq(new Point(0, 0)) else initBoardShape
    for (pt <- ptList) {
      pt.x += initX
      pt.y += initY
    }

    for (pt <- ptList) require(world.terrain.isTileValid(pt.x, pt.y))
    require(ptList.nonEmpty, s"Board shape of $this is empty")

    mutable.ArrayBuffer(ptList: _*)
  }

  /**
    * The name of the entity.
    */
  val name: String = {
    if (n == null) hashCode.toString
    else if (!world.entities.entityList.exists { e => e.name == n }) n
    else throw new NotUniqueNameException(n)
  }

  /**
    * The current list of tiles occupied by this entity.
    */
  def boardShape = m_boardShape.toList

  private def moveShape(dx: Int, dy: Int): Unit = {
    for (pt <- m_boardShape) {
      pt.x += dx
      pt.y += dy
      assert(world.terrain.isTileValid(pt.x, pt.y), s"Board shape of $this not in terrain boundaries")
    }
  }

  /**
    * Add a tile to the entity's board shape.
    * @param x The x position of the tile to incorporate.
    * @param y The y position of the tile to incorporate.
    */
  protected def expandBoardShape(x: Int, y: Int): Unit = {
    if (!m_boardShape.exists(p => p.x == x && p.y == y)) m_boardShape += new Point(x, y)
  }

  /**
    * Removes a tile from the entity's board shape.
    * @param x The x position of the tile to cut out of the shape.
    * @param y The y position of the tile to cut out of the shape.
    */
  protected def cutBoardShape(x: Int, y: Int): Unit = {
    m_boardShape.remove(m_boardShape.indexWhere(p => p.x == x && p.y == y))
  }

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

  /**
    * Sets the current grid position of the entity. Should not be called directly from outside classes!
    * @param x The new x position.
    * @param y The new y position.
    */
  protected def setGridPosition(x: Int = _x, y: Int = _y) = {
    val oldX = _x
    val oldY = _y
    _x = x
    _y = y
    val event = LocationChangedEvent(oldX, oldY, _x, _y, this)
    moveShape(event.diffX, event.diffY)
    onLocationChanged(event)
  }

  override def getGridX: Int = _x

  override def getGridY: Int = _y

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
    setGridPosition(x, y)
  }

}

trait InventoryEntity extends Entity {

  val inventory: InventoryLike = new DefaultInventory

}

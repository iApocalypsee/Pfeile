package newent

import java.awt.Point

import player.BoardPositionable
import world.WorldLike

sealed trait EntityLike extends BoardPositionable {

  /** The world in which the entity is living. */
  val world: WorldLike

}

/** (Another) Base class for all entities. The new-old entity traits used old interfaces
  * so I have to replace them aswell.
  *
  * @param n The name of the entity. Defaults to null.
  * @param world The world of the entity. Should not be null.
  */
abstract class Entity(override val world: WorldLike, spawnPosition: (Int, Int), n: String = null) extends EntityLike {

  require(world ne null)

  private var _x = spawnPosition._1
  private var _y = spawnPosition._2

  /** The name of the entity. It is never going to change. */
  val name: String = if(n eq null) hashCode.toString else n

  /// Ditto.
  def this(world: WorldLike, spawnPoint: Point, n: String) = this(world, (spawnPoint.x, spawnPoint.y), n)

  override def getGridX: Int = _x
  override def getGridY: Int = _y

  protected def setGridX(x: Int) = _x = x
  protected def setGridY(y: Int) = _y = y

}

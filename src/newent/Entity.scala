package newent

import com.sun.istack.internal.{NotNull, Nullable}
import world.World

// Not renaming this import leads to name collision.
import scala.{NotNull => DeprecatedScalaNotNull}

/**
  * Base class for all game objects having some sort of living attribute attached to them.
  *
  * @param world The world of the entity.
  * @param initX The x position on which the entity will spawn.
  * @param initY The y position on which the entity will spawn.
  * @param n The name of the entity. Scala: defaults to `null`.
  */
abstract class Entity(@NotNull world: World, initX: Int, initY: Int, @Nullable n: String) extends GameObject(initX, initY, world) {

  require(world != null)

  def this(world: World, x: Int, y: Int) = this(world, x, y, null)

  /**
    * The name of the entity.
    */
  val name: String = if (n == null) this.toString else n

  /**
    * Sets the x position of the entity. Should not be called directly from outside classes!
 *
    * @param x The new x position to set to.
    */
  protected def setGridX(x: Int) = setGridPosition(x, getGridY)

  /**
    * Sets the y position of the entity. Should not be called directly from outside classes!
 *
    * @param y The new y position to set to.
    */
  protected def setGridY(y: Int) = setGridPosition(getGridX, y)

  /**
    * Sets the current grid position of the entity. Should not be called directly from outside classes!
 *
    * @param x The new x position.
    * @param y The new y position.
    */
  protected def setGridPosition(x: Int = getGridX, y: Int = getGridY) = place(x, y)

}

trait InventoryEntity extends Entity {

  val inventory: InventoryLike = new DefaultInventory

}

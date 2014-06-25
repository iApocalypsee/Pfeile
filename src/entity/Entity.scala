package entity

import gui.NewWorldTestScreen
import misc.metadata.OverrideMetadatable
import player.Inventory
import world.IWorld

/**
 *
 * @author Josip
 * @version 27.05.2014
 */
trait Entity extends AttackContainer with OverrideMetadatable with TurnAffected {

  private var _gridX = 0
  private var _gridY = 0
  lazy val world: IWorld = {
    NewWorldTestScreen.world.registerEntity(this)
    NewWorldTestScreen.world
  }
  val inventory = new Inventory(this)

  /**
   * Returns the x grid position of the entity.
   * @return The x grid position of the entity.
   */
  def gridX = _gridX

  /**
   * Sets (teleports) the entity's x position to the specified one.
   * If specified coordinate is out of bounds, an exception is thrown.
   * @param x The x coordinate.
   */
  protected def gridX_=(x: Int) = {
    if(!world.isTileValid(x, gridY)) throw new ArrayIndexOutOfBoundsException
    _gridX = x
  }

  /**
   * Returns the x grid position of the entity.
   * @return The x grid position of the entity.
   */
  def gridY = _gridY

  /**
   * Sets (teleports) the entity's x position to the specified one.
   * If specified coordinate is out of bounds, an exception is thrown.
   * @param x The x coordinate.
   */
  protected def gridY_=(y: Int) = {
    if(!world.isTileValid(gridX, y)) throw new ArrayIndexOutOfBoundsException
    _gridY = y
  }

  def getGridX = gridX
  def getGridY = gridY
  protected final def setGridX(x: Int) = gridX = x
  protected final def setGridY(y: Int) = gridY = y
  def location = world.getTileAt(gridX, gridY)
  def teleport(loc: (Int, Int)) = {
    gridX = loc._1
    gridY = loc._2
  }

}

package entity

import world.IWorld
import player.Inventory
import player.weapon.AttackContainer
import misc.metadata.OverrideMetadatable

/**
 *
 * @author Josip
 * @version 27.05.2014
 */
trait Entity extends AttackContainer with OverrideMetadatable with TurnAffected {

  private var _gridX = 0
  private var _gridY = 0
  val _world: IWorld
  val Inventory = new Inventory(this)

  def world = _world

  def gridX = _gridX
  def gridX_=(x: Int) = {
    if(!world.isTileValid(x, gridY)) throw new ArrayIndexOutOfBoundsException
    _gridX = x
  }

  def gridY = _gridY
  def gridY_=(y: Int) = {
    if(!world.isTileValid(gridX, y)) throw new ArrayIndexOutOfBoundsException
    _gridY = y
  }

}

package world.tile

import java.awt.Color

import player.weapon.AttackEvent
import world.{GridElement, BaseTile}

/**
 *
 * @author Josip Palavra
 * @version 29.06.2014
 */
class DesertTile(g: GridElement) extends BaseTile(g) {


  override def registerAttack(event: AttackEvent) = ???

  override val requiredMovementPoints = 4

  override def getColor = DesertTile.color
}

object DesertTile {
  val color = new Color(0xF8F67D)
}

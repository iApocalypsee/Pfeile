package world.tile

import world.{GridElement, BaseTile}
import java.awt.Color

/**
 *
 * @author Josip Palavra
 * @version 01.06.2014
 */
class SeaTile(gridElem: GridElement) extends BaseTile(gridElem) {

  override val requiredMovementPoints = 1

  override def getColor = SeaTile.color
}

object SeaTile {
  val color = new Color(0x3555DB)
}

package world.tile

import java.awt.Color

import world.{BaseTile, GridElement}

/**
 *
 * @author Josip Palavra
 * @version 01.06.2014
 */
class GrassTile(gridElem: GridElement) extends BaseTile(gridElem) {

  override val requiredMovementPoints = 1
  override def getColor = GrassTile.color

}

object GrassTile {
  val color = new Color(0x1C9618)
}

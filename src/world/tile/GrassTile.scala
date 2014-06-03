package world.tile

import world.{GridElement, BaseTile}
import java.awt.Color

/**
 *
 * @author Josip Palavra
 * @version 01.06.2014
 */
class GrassTile(gridElem: GridElement) extends BaseTile(gridElem) {
  override def getColor = GrassTile.color
}

object GrassTile {
  val color = new Color(0x1C9618);
}

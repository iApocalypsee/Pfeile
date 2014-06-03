package world.tile

import world.{GridElement, BaseTile}
import java.awt.Color

/**
 *
 * @author Josip Palavra
 * @version 02.06.2014
 */
class FakeTile(gridElem: GridElement) extends BaseTile(gridElem) {
  override def getColor: Color = FakeTile.color
}

object FakeTile {
  val color = Color.black
}

package world.tile

import world.{GridElement, BaseTile}
import java.awt.Color

/**
 *
 * @author Josip Palavra
 * @version 02.06.2014
 */
class FakeTile(gridElem: GridElement) extends BaseTile(gridElem) {

  // It should be impossible to get on a fake tile.
  // Fake tiles are sometimes reminiscent of the generation process of a world.
  override val requiredMovementPoints = Int.MaxValue

  override def getColor: Color = FakeTile.color

}

object FakeTile {
  val color = Color.black
}

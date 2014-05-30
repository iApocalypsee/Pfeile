package world.tile

import java.awt.Color

/**
 * Represents a grass tile.
 * @author Josip Palavra
 */
trait GrassTile extends BaseTile {
  override def getColor: Color = GrassTile.defaultColor
}

object GrassTile {
  val defaultColor = new Color(0x1C9618)
}

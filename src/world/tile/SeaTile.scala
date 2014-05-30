package world.tile

import java.awt.Color

/**
 *
 * @author Josip Palavra
 * @version 30.05.2014
 */
trait SeaTile extends BaseTile {
  override def getColor = SeaTile.defaultColor
}

object SeaTile {
  val defaultColor = new Color(0x6D75E8)
}

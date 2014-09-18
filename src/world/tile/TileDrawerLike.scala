package world.tile

import java.awt.Graphics2D

import world.TileLike

/**
 *
 * @author Josip Palavra
 */
sealed trait TileDrawerLike {

  def draw(g: Graphics2D, t: TileLike)

}

class DefaultTileDrawer extends TileDrawerLike {
  override def draw(g: Graphics2D, t: TileLike): Unit = {

  }
}

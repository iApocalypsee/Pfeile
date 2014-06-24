package gui

import java.awt.Graphics2D
import world.{BaseTile, IWorld, World}
import scala.beans.BeanProperty
import java.awt.event.KeyEvent

/**
 *
 * @author Josip
 */
object NewWorldTestScreen extends Screen("New world test", 164) {

  @BeanProperty
  var world: IWorld = null

  var shootButtonPressed = false

  override def keyPressed(e: KeyEvent) {
    if(e.getKeyCode == KeyEvent.VK_RIGHT) world.getViewport.shiftRel(-8, 0)
    if(e.getKeyCode == KeyEvent.VK_LEFT) world.getViewport.shiftRel(8, 0)
    if(e.getKeyCode == KeyEvent.VK_DOWN) world.getViewport.shiftRel(0, -8)
    if(e.getKeyCode == KeyEvent.VK_UP) world.getViewport.shiftRel(0, 8)
    if(e.getKeyCode == KeyEvent.VK_PAGE_UP) world.getViewport.zoomRel(1.1f)
    if(e.getKeyCode == KeyEvent.VK_PAGE_DOWN) world.getViewport.zoomRel(0.9f)
  }

  def bindTileComponents {
    for(x <- 0 until world.getSizeX) {
      for(y <- 0 until world.getSizeY) {
        add(world.getTileAt(x, y).asInstanceOf[BaseTile])
        NewWorldTestScreen.add(world.getTileAt(x, y).asInstanceOf[BaseTile])
        world.getTileAt(x, y).asInstanceOf[BaseTile].updateGUI
      }
    }
  }

  override def draw(g: Graphics2D): Unit = {
    super.draw(g)
    world.draw(g)
  }
}
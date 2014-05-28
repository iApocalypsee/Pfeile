package gui

import java.awt.Graphics2D
import world.World
import scala.beans.BeanProperty
import java.awt.event.KeyEvent

/**
 *
 * @author Josip
 */
object NewWorldTestScreen extends Screen("New world test", 164) {

  @BeanProperty
  var world: World = null

  override def keyPressed(e: KeyEvent) {
    if(e.getKeyCode == KeyEvent.VK_RIGHT) world.getViewport.shiftRel(-3, 0)
    if(e.getKeyCode == KeyEvent.VK_LEFT) world.getViewport.shiftRel(3, 0)
    if(e.getKeyCode == KeyEvent.VK_DOWN) world.getViewport.shiftRel(0, -3)
    if(e.getKeyCode == KeyEvent.VK_UP) world.getViewport.shiftRel(0, 3)
    if(e.getKeyCode == KeyEvent.VK_PAGE_UP) world.getViewport.zoomRel(1.1f)
    if(e.getKeyCode == KeyEvent.VK_PAGE_DOWN) world.getViewport.zoomRel(0.9f)
  }

  override def draw(g: Graphics2D): Unit = {
    super.draw(g)
    world.draw(g)
  }
}

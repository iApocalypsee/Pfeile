package gui

import java.awt.Graphics2D
import java.awt.event.KeyEvent

import comp.GUIUpdater
import world.{BaseTerrain, BaseTile, IWorld}

import scala.beans.BeanProperty
import general.Main
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
    //if(e.getKeyCode == KeyEvent.VK_M) world.getViewport.setRotation((0.1 * Main.delta()).asInstanceOf[Int] + world.getViewport.getRotation)
    //if(e.getKeyCode == KeyEvent.VK_N) world.getViewport.setRotation((-0.1 * Main.delta()).asInstanceOf[Int] + world.getViewport.getRotation)
    if(e.getKeyCode == KeyEvent.VK_B) world.getViewport.setPovAngle((0.5 * Main.delta()).asInstanceOf[Int] + world.getViewport.getPovAngle)
    if(e.getKeyCode == KeyEvent.VK_V) world.getViewport.setPovAngle((-0.5 * Main.delta()).asInstanceOf[Int] + world.getViewport.getPovAngle)
    for(i <- 0 until getComponents.size()) {
      getComponents.get(i) match {
        case updater: GUIUpdater =>
          updater.updateGUI()
        case _ =>
      }
    }
  }

  def bindTileComponents(): Unit = {
    for(x <- 0 until world.getSizeX) {
      for(y <- 0 until world.getSizeY) {
        add(world.getTileAt(x, y).asInstanceOf[BaseTile])
      }
    }
  }

  override def draw(g: Graphics2D): Unit = {
    super.draw(g)
    //world.draw(g)
    world.getTerrain.asInstanceOf[BaseTerrain].drawInfoBox(g)
  }
}

package world

import comp.GUIUpdater
import gui.Drawable
import java.awt.Graphics2D
import java.util
import entity.Entity

/**
 *
 * @author Josip Palavra
 * @version 01.06.2014
 */
class ScaleWorld(x: Int, y: Int) extends IWorld with Drawable with GUIUpdater {

  private val _terrain = new EditableBaseTerrain(x, y, this)
  private val _view = new WorldViewport(this)

  override def getSizeX = _terrain.getSizeX

  override def isTileValid(x: Int, y: Int) = x >= 0 && x < getSizeX && y >= 0 && y < getSizeY

  override def getSizeY = _terrain.getSizeY

  override def getViewport = _view

  override def getNeighborFields: util.List[_ <: IField] = ???

  override def collectEntities(): util.List[_ <: Entity] = ???

  override def collectEntities(clazz: Class[_ <: Entity]): util.List[_ <: Entity] = ???

  override def getTerrain = _terrain

  override def getTileAt(x: Int, y: Int) = _terrain.getTileAt(x, y)

  override def getFieldAt(x: Int, y: Int): IField = ???

  def getGridElementAt(x: Int, y: Int) = _terrain.grid.apply(x).apply(y)

  override def getFields: util.List[_ <: IField] = ???

  override def draw(g: Graphics2D) {
    _terrain.draw(g)
  }

  override def updateGUI(): Unit = {
    val thread = new Thread(new Runnable {
      override def run() = {
         _terrain.updateGUI()
      }
    })
   thread.setPriority(Thread.NORM_PRIORITY - 2)
   thread.setDaemon(true)
   thread.start()
  }
}

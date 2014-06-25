package world

import java.awt.Graphics2D
import java.{util, lang}

import comp.GUIUpdater
import entity.{Player, Entity}
import gui.Drawable

import scala.collection.JavaConversions._
import scala.collection.mutable
import general.Mechanics

/**
 *
 * @author Josip Palavra
 * @version 01.06.2014
 */
class ScaleWorld(x: Int, y: Int) extends IWorld with Drawable with GUIUpdater {

  private val _terrain = new EditableBaseTerrain(x, y, this)
  private val _view = new WorldViewport(this)
  private val entityList = mutable.Queue[Entity]()

  override def getSizeX = _terrain.getSizeX

  override def isTileValid(x: Int, y: Int) = x >= 0 && x < getSizeX && y >= 0 && y < getSizeY

  override def getSizeY = _terrain.getSizeY

  override def getViewport = _view

  override def getNeighborFields: java.lang.Iterable[_ <: IField] = ???

  def getActivePlayer: Player = {
    val it = getPlayers.iterator()
    while(it.hasNext) {
      val x = it.next()
      if(x.getName.equals(Mechanics.getUsername)) return x
    }
    null
  }

  override def collectEntities(): java.lang.Iterable[_ <: Entity] = entityList

  override def registerEntity(e: Entity): Unit = {
    require(e ne null)
    entityList.enqueue(e)
  }

  override def getPlayers: lang.Iterable[Player] = {
    //entityList.filter(_.getClass.eq(Player.getClass)).asInstanceOf[mutable.Queue[Player]]
    val ll = new util.LinkedList[Player]()
    for(x <- 0 until entityList.size) {
      val t = entityList(x)
      if(t.getClass.equals(classOf[Player])) ll.add(t.asInstanceOf[Player])
    }
    ll
  }

  override def collectEntities(clazz: Class[_ <: Entity]): java.lang.Iterable[_ <: Entity] = entityList.filter(p => p.getClass.equals(clazz))

  override def getTerrain = _terrain

  override def getTileAt(x: Int, y: Int) = _terrain.getTileAt(x, y)

  override def getFieldAt(x: Int, y: Int): IField = ???

  def getGridElementAt(x: Int, y: Int) = _terrain.grid.apply(x).apply(y)

  override def getFields: java.lang.Iterable[_ <: IField] = ???

  override def draw(g: Graphics2D) {
    _terrain.draw(g)
  }

  override def updateGUI(): Unit = {
    val thread = new Thread(new Runnable {
      override def run() = {
         _terrain.updateGUI()
      }
    })
   thread.setPriority(Thread.NORM_PRIORITY - 1)
   thread.setDaemon(true)
   thread.start()
  }
}

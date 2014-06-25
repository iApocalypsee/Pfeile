package entity

import player.BoardPositionable
import world.IBaseTile
import world.brush.BrushHelper

import scala.collection.mutable
import gui.NewWorldTestScreen

/**
 * The vision of an entity.
 * @author Josip Palavra
 * @version 24.06.2014
 */
class Vision(val visioner: Visioner) {

  require(visioner ne null)

  private val visionList = mutable.Queue[Vision.VisionPoint]()

  def put(position: BoardPositionable, strength: Int): Unit = {
    val p = new Vision.VisionPoint(position, this)
    p.strength = strength
    visionList.enqueue(p)
  }

  def updateVisionables(): Unit = {
    val h = visioner.visionMap
    for(x <- 0 until NewWorldTestScreen.getWorld.getSizeX) {
      for(y <- 0 until NewWorldTestScreen.getWorld.getSizeY) {
        if(h(x, y).equals(VisionState.Observable)) h.update((x, y), VisionState.Nonvisible)
      }
    }

    for(vp <- visionList) {
      val f = determineVisionables(vp)
      f.foreach(tile => {
        h.update((tile.getGridX, tile.getGridY), VisionState.Observable)
      })
    }

    /*
    visionList.foreach(vp => {
      val f = determineVisionables(vp)
      f.foreach(tile => {
        h.update((tile.getGridX, tile.getGridY), VisionState.Observable)
      })
    })
    */
  }

  private def determineVisionables(p: Vision.VisionPoint): List[IBaseTile] = {
    val l = mutable.Queue[IBaseTile]()
    val w = visioner.asInstanceOf[Entity].world

    val linklist = BrushHelper.determineTiles(w.getTileAt(p.hook.getGridX, p.hook.getGridY), p.strength)
    val llit = linklist.iterator()

    while(llit.hasNext) l.enqueue(llit.next())
    l.toList
  }

}

object Vision {
  protected[Vision] class VisionPoint(val hook: BoardPositionable, val sight: Vision) {
    require(hook ne null)
    require(sight ne null)

    private var _strength = 0

    def strength = _strength
    def strength_=(value: Int) = _strength = value
  }
}

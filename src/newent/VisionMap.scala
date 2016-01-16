package newent

import comp.Circle
import general.Delegate
import newent.VisionMap.{VisionEntry, VisionPromise}
import player.BoardPositionable
import world.TileLike

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Represents the parts of the map that have been revealed, are visible or are not even discovered.
  *
  * @constructor Creates a new vision map.
  * @param entity The entity to tie the vision map to.
  */
class VisionMap(val entity: VisionEntity) {

  /** The vision entries, where everything about vision is saved, inaccessible from the tile classes. */
  private val _entries = {
    val result = mutable.MutableList[VisionEntry]()
    val terrain = entity.tileLocation.terrain
    for (y <- 0 until terrain.height) {
      for (x <- 0 until terrain.width) {
        result += new VisionEntry(terrain.tileAt(x, y))
      }
    }
    result.toList
  }

  private var _grantedVisionObjects = mutable.MutableList[VisionPromise]()

  /** Returns a vision object that can be "released". */
  def grantVision(x: Int, y: Int, radius: Int): VisionPromise = {
    val grantedVision = new VisionPromise(x, y, radius)
    // When the vision is released, I need to keep a way of removing the vision object from the map
    grantedVision._onVisionReleased += { () =>
      // Remove the constructed promise when the vision is going to be released.
      _grantedVisionObjects = _grantedVisionObjects.filterNot(_ == grantedVision)
      // Recalculate the vision map, since the vision objects have changed.
      updateEntries()
    }
    _grantedVisionObjects += grantedVision
    updateEntries()
    grantedVision
  }

  /** Returns true if the specified tile on these coordinates is visible. */
  def isVisible(x: Int, y: Int): Boolean = visionStatusOf(x, y) eq VisionStatus.Visible

  /** Returns the current vision status of any board positionable object. */
  def visionStatusOf(p: BoardPositionable): VisionStatus = visionStatusOf(p.getGridX, p.getGridY)

  /** Returns the current vision status of the tile. */
  def visionStatusOf(x: Int, y: Int): VisionStatus = {
    require(entity.world.terrain.isTileValid(x, y))
    val findResult = _entries.find { e => e.tile.getGridX == x && e.tile.getGridY == y }.get
    findResult.visionStatus
  }

  /**
    * Collects all tiles from this vision map that are visible to the entity.
    */
  def visibleTiles = _entries.filter(_.visionStatus == VisionStatus.Visible).map(_.tile)
  def getVisibleTiles = visibleTiles.asJava

  /** Updates the vision entries based on the vision promises that have been granted. */
  private def updateEntries(): Unit = {
    // Reset every visibility so that no tile has the status "Visible"
    makeNonVisible()
    _grantedVisionObjects foreach { v =>
      _entries foreach { e =>
        Future {
          if (v.circle.contains(e.tile.getGridX, e.tile.getGridY)) {
            e.visionStatus = VisionStatus.Visible
          }
        }
      }
    }
  }

  /** Clears every tile from being "visible" to more respective statuses. */
  private def makeNonVisible(): Unit = {
    _entries foreach { e =>
      // Every tile that has been seen at least once is "revealed"; it is not in the fog of war
      // anymore.
      if (e.visionStatus == VisionStatus.Visible) e.visionStatus = VisionStatus.Revealed
    }
  }

}

object VisionMap {

  /** Internal class for handling vision of tiles. */
  private class VisionEntry(val tile: TileLike) {

    /** The current vision status. */
    var visionStatus = VisionStatus.Hidden

  }

  /** Represents a vision point in the map. Just like a "ward". */
  class VisionPromise private[VisionMap] (x: Int, y: Int, radius: Int) {

    /** Circle for representing the geometry of the vision. */
    private[VisionMap] val circle = {
      val ret = new Circle
      ret.setX(x)
      ret.setY(y)
      ret.setRadius(radius)
      ret
    }

    /** Called when the vision has been released on this promise. */
    private[VisionMap] val _onVisionReleased = Delegate.createZeroArity

    /**
      * Releases the promise from the vision, meaning that the vision point is no longer
      * meaningful for the vision map.
      */
    def releaseVision() = {
      // I know in advance that the VisionMap class registered a callback on the delegate for
      // deleting this promise out of the vision.
      _onVisionReleased.apply()
    }
  }
}

package entity

import gui.NewWorldTestScreen
import player.BoardPositionable

import scala.collection.mutable

/**
 * Represents an object that has some sort of "vision" on the map.
 * @author Josip Palavra
 * @version 24.06.2014
 */
trait Visioner {

  val visionObject = new Vision(this)

  private[entity] lazy val visionMap = {
    val res = mutable.HashMap[(Int, Int), VisionState]()
    for(x <- 0 until NewWorldTestScreen.getWorld.getSizeX) {
      for(y <- 0 until NewWorldTestScreen.getWorld.getSizeY) {
        res.update((x, y), VisionState.Unrevealed)
      }
    }
    res
  }

  /**
   * Returns a boolean value indicating whether a board object can be seen or not by the visioner object.
   * @param boardObject The board object to test with.
   * @return A boolean value.
   */
  def isVisible(boardObject: BoardPositionable) = visibility(boardObject).equals(VisionState.Observable)
  def visibility(boardObject: BoardPositionable) = visionMap((boardObject.getGridX, boardObject.getGridY))

}

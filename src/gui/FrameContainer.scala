package gui

import comp.InternalFrame
import general.Delegate

import java.awt.Graphics2D

import gui.screen.Screen

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/** A screen that can contain an unspecified amount of internal frames.
  *
  * The mess of saving internal frames is just too verbose; a special object for
  * handling internal frames would do it as well.
  */
trait FrameContainer extends Screen {

  /** The object managing the internal frames for the screen. */
  lazy val frameContainer = new FrameContainerObject
  def getFrameContainer = frameContainer

  abstract override def draw(g: Graphics2D): Unit = {
    super.draw(g)
    frameContainer.drawFrames(g)
  }

}

class FrameContainerObject private[gui] {

  private val _frames = mutable.ArrayBuffer[InternalFrame]()

  lazy val onFrameRemoved = Delegate.create[InternalFrame]
  lazy val onFrameAdded = Delegate.create[InternalFrame]

  /** Adds a frame to the container object. */
  def addFrame(f: InternalFrame): Unit = {
    _frames += f
    onFrameAdded callAsync f
  }

  /** Removes the first frame that matches the condition in the function. */
  def removeFrame(f: InternalFrame => Boolean): Unit = {
    val occurrence = _frames find f
    occurrence.map { frame =>
      _frames -= frame
      onFrameRemoved callAsync frame
    }
  }

  /** Removes a specified frame. */
  def removeFrame(f: InternalFrame): Unit = removeFrame { _ == f }

  /** Draws all the frames added to the container with the graphics object. */
  def drawFrames(g: Graphics2D) = frames foreach { _.draw(g) }

  /** All frames that have been added to the container. */
  def frames = _frames.toList

}

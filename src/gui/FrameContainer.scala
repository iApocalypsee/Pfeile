package gui

import java.awt.Graphics2D

import comp.InternalFrame
import general.Delegate
import gui.screen.Screen

import scala.collection.mutable

/**
  * A screen that can contain an unspecified amount of internal frames.
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

  val onFrameRemoved = Delegate.create[InternalFrame]
  val onFrameAdded = Delegate.create[InternalFrame]

  /** Adds a frame to the container object. */
  def addFrame(f: InternalFrame): Unit = {
    _frames += f
    onFrameAdded(f)
  }

  /** Removes the first frame that matches the condition in the function. */
  def removeFrame(f: InternalFrame => Boolean): Unit = {
    val occurrence = _frames find f
    occurrence.foreach { frame =>
      _frames -= frame
      onFrameRemoved(frame)
    }
  }

  /** Removes a specified frame. */
  def removeFrame(f: InternalFrame): Unit = removeFrame { _ == f }

  /**
    * Closes all frames that are currently open.
    * Note that the [[comp.InternalFrame#onClosed]] delegate will be triggered.
    */
  def closeFrames(): Unit = {
    for (frame <- _frames) {
      frame.setVisible(false)
    }
  }

  /**
    * Opens all frames.
    * Be careful with this function. It could clutter the user interface.
    */
  def openFrames(): Unit = {
    for (frame <- _frames) {
      frame.setVisible(true)
    }
  }

  /** Draws all the frames added to the container with the graphics object. */
  def drawFrames(g: Graphics2D) = frames foreach { _.drawChecked(g) }

  /** All frames that have been added to the container. */
  def frames = _frames.toList

}

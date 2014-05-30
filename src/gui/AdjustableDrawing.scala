package gui

import scala.collection.mutable
import java.awt.Graphics2D

/**
 * Represents an object to which individual drawing commands
 * can be appended.
 * @author Josip Palavra
 */
trait AdjustableDrawing {

  // The function definitions
  private val _drawQueue = new mutable.Queue[DrawingEntry]

  /**
   * Draws all individual
   * @param g The graphics object to apply to all function bodies.
   */
  def drawAll(g: Graphics2D) {
    _drawQueue foreach(i => {
      // only if the condition is true, execute the drawing body
      if(i.condition.apply()) {
        i.func.apply(g)
      }
    })
  }

  /**
   * Appends a new drawing function to the object, which executes only when the
   * condition param is returning true.
   * @param drawfunc The drawing function.
   * @param condition The condition. Optional parameter, defaults to true.
   * @return A handle with which the draw function can be removed and the status of the
   *         functions can be seen.
   */
  def handle(drawfunc: (Graphics2D) => Unit, condition: () => Boolean = () => true): AdjustableDrawingHandle = {
    val newEntry = new DrawingEntry(-1, drawfunc, condition)

    // append the new entry
    _drawQueue enqueue newEntry
    newEntry.index = _drawQueue.indexOf(newEntry)

    // refresh all of the index data, because it is invalid now
    _drawQueue foreach(i => i.index = _drawQueue.indexOf(i))
    new AdjustableDrawingHandle(newEntry)
  }

  /**
   * Removes all instances that are equal to the parameter.
   * @param that The handle to remove.
   * @throws NoSuchElementException if no such element exists.
   */
  def remove(that: AdjustableDrawingHandle): Unit = {
    val index = _drawQueue indexOf that
    if(index == -1) throw new NoSuchElementException
    _drawQueue.dequeueAll(i => that.entry equals i)
  }

}

/**
 * Represents a drawing entry in the "individual" drawing queue.
 * @param index The index
 * @param func The function to tie to.
 */
private class DrawingEntry private[gui](var index: Int, val func: (Graphics2D) => Unit,
                                         val condition: () => Boolean)

class AdjustableDrawingHandle private[gui](private[gui] val entry: DrawingEntry) {

  /**
   * Returns the index of the function in the drawing queue of the <code>AdjustableDrawing</code>
   * object.
   * @return The index of the function.
   */
  def index = entry.index

  /**
   * Returns the function used to draw the object individually.
   */
  def function = entry.func

  /**
   * Returns the condition.
   * @return The condition.
   */
  def condition = entry.condition

  /**
   * Apply the graphics object to the function body of this handle.
   * @param g The graphics object.
   */
  def apply(g: Graphics2D) = entry.func.apply(g)

}

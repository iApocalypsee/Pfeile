package gui

import java.awt.Graphics2D
import java.util.function._
import java.util.{Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import scala.collection.mutable
import scala.compat.java8.FunctionConverters._

/**
  * An object to which additional drawing commands may be given to be executed before or after
  * the actual draw method of implementing class.
  *
  * Functionality is exposed through the methods `preDraw` and `postDraw`, which add given draw function
  * to the respective storage buffer for later execution.
  */
trait AdjustableDrawing extends comp.Component {

  // <editor-fold desc="Draw queues">

  private val m_preDraw = new mutable.ArrayBuffer[DrawingEntry]

  private val m_postDraw = new mutable.ArrayBuffer[DrawingEntry]

  /**
    * Generic append function to generate a cancellable drawing handle.
    * @param to The buffer to append the new drawing entry to.
    * @param f The actual draw function.
    * @param condition Condition to be checked before applying given draw function.
    * @return A handle that may be cancelled at a future point in time or may be completely ignored.
    */
  def appendElement(to: mutable.ArrayBuffer[DrawingEntry], f: Graphics2D => Unit, condition: () => Boolean): AdjustableDrawingHandle = {
    val newEntry = new DrawingEntry(-1, f, condition, to)
    to += newEntry
    newEntry.index = to.indexOf(newEntry)

    // Refresh all of the index data, because it is invalid now
    to foreach(i => i.index = to.indexOf(i))

    new AdjustableDrawingHandle(newEntry)
  }

  // </editor-fold>

  abstract override def draw(g: Graphics2D): Unit = {
    m_preDraw foreach { entry  => if(entry.condition()) entry.func(g) }
    super.draw(g)
    m_postDraw foreach { entry => if(entry.condition()) entry.func(g) }
  }

  def preDraw(f: Consumer[Graphics2D], condition: Supplier[Boolean] = () => true) = appendElement(m_preDraw, f.asScala, condition.asScala)

  def postDraw(f: Consumer[Graphics2D], condition: Supplier[Boolean] = () => true) = appendElement(m_postDraw, f.asScala, condition.asScala)

  // <editor-fold desc="Nested classes">

  class AdjustableDrawingHandle private[AdjustableDrawing](private[AdjustableDrawing] val entry: DrawingEntry) {

    /**
      * @return The index of the function in the drawing queue of the AdjustableDrawing
      * object.
      */
    def index = entry.index

    /**
      * @return The function used to draw the object individually.
      */
    def function = entry.func

    /**
      * @return The condition which must be satisfied first for the draw function to get called.
      */
    def condition = entry.condition

    /**
      * Apply the graphics object to the function body of this handle.
      *
      * @param g The graphics object.
      */
    def apply(g: Graphics2D) = entry.func(g)

    /**
      * Removes this handle from the draw queue it has been assigned to.
      */
    def dispose(): Unit = {
      entry.storage -= entry
    }

  }

  /**
    * Represents a drawing entry in the "individual" drawing queue.
    *
    * @param index The index
    * @param func The function to tie to.
    */
  private[AdjustableDrawing] class DrawingEntry private[AdjustableDrawing](var index: Int, val func: (Graphics2D) => Unit,
                                          val condition: () => Boolean, val storage: mutable.ArrayBuffer[DrawingEntry])

  // </editor-fold>

}

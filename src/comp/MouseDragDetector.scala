package comp

import general.Delegate
import geom.{Vector, Point}

import java.awt.event.{MouseAdapter, MouseEvent, MouseMotionAdapter}

/** Components wanting to keep track of being "actively dragged" by the user
  * can inherit this trait as an alternative to coding "active dragging" by yourself.
  */
trait MouseDragDetector extends Component {

  /** Delegate which gets called when a drag has been detected on the component. <p>
    * This delegate differs from the <code>mouseDragged(MouseEvent)</code> method
    * in MouseMotionListener in that the delegate only gets called when the mouse
    * has pressed on the component. <p>
    * Do not try to confuse this with the mouseDragged method.
    */
  val onMouseDragDetected = Delegate.create[Vector]

  /** The previous mouse position that has been captured by the detector. */
  @volatile private var prevMousePosition: Point = null

  // Code that is necessary to update prevMousePosition.
  addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent): Unit = {
      prevMousePosition = new Point(e.getPoint)
    }

    override def mouseReleased(e: MouseEvent): Unit = {
      prevMousePosition = null
    }
  })

  // Execution code for the delegate.
  addMouseMotionListener(new MouseMotionAdapter {
    override def mouseDragged(e: MouseEvent): Unit = {
      if(prevMousePosition ne null) {
        val now = new Point(e.getPoint)
        val diff = now - prevMousePosition
        onMouseDragDetected(diff)
        prevMousePosition = new Point(e.getPoint)
      }
    }
  })

}

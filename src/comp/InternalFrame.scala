package comp

import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}

import general.{Delegate, LogFacility}
import gui.Screen

import scala.collection.mutable

/** An internal frame capable of containing components in order to make a more "cleaner" UI.
  * The coordinates of components are given in absolute coordinates (not relative to the frame's
  * upper left corner), but this is still a WIP.
  *
  * @param x Initial x position of the frame.
  * @param y Initial y position of the frame.
  * @param width Ditto.
  * @param height Ditto.
  * @param backingScreen Ditto.
  */
class InternalFrame(x: Int, y: Int, width: Int, height: Int, backingScreen: Screen) extends Component(x, y, width,
  height, backingScreen) {

  import comp.InternalFrame._

  // Every time the frame is moved, the contained components have to be moved as well.
  onMoved += { vec =>
    // Move every component by the vector
    comps foreach { _.move(vec.x.asInstanceOf[Int], vec.y.asInstanceOf[Int]) }
  }

  /** The components that the frame is managing. */
  private val comps = mutable.MutableList[Component]()

  /** Called when the internal frame's close button has been pressed. */
  val onClosed = Delegate.createZeroArity

  this << ToplineBar

  /** The close button of the frame. */
  private lazy val closeButton = {
    val ret = new Button(0, 0, backingScreen, "")
    val xInBounds = x + width - FrameStyle.CommonInset - FrameStyle.CloseButtonDimension.width
    val yInBounds = y + FrameStyle.CommonInset
    val widthInBounds = FrameStyle.CloseButtonDimension.width
    val heightInBounds = FrameStyle.CloseButtonDimension.height

    // Add the new button to the frame so that it receives (positional) changes.
    this << ret

    // Set the close button up according to the rules specified in [[FrameStyle]].
    ret.setSourceShape(new Rectangle(-widthInBounds / 2, -heightInBounds / 2, widthInBounds, heightInBounds))

    // Add the width to x and height to y because:
    //    The new transform system allows bounds to define custom anchor middle points.
    //    The best way of handling certain transformations is to put the anchor middle point
    //    in the center of the component.
    ret.getTransformation.translate(widthInBounds + xInBounds, heightInBounds + yInBounds)

    ret.getBorder.setInnerColor(FrameStyle.CloseButtonColor_Inner)
    ret.setAdditionalDrawing { (g: Graphics2D) =>
      val buttonBounds = ret.getBounds.getBounds
      val inset = FrameStyle.CommonInset

      g.setColor(Color.white)

      // Save the old stroke for later... I need to reset it back.
      val oldStroke = g.getStroke
      g.setStroke(FrameStyle.CloseButton_DrawStroke)

      // This should resemble an 'X'. A more beautifullier drawn X than I would have done
      // with just typography.
      g.drawLine(buttonBounds.x + inset, buttonBounds.y + inset, buttonBounds.x + buttonBounds.width - inset,
        buttonBounds.y + buttonBounds.height - inset)
      g.drawLine(buttonBounds.x + inset, buttonBounds.y + buttonBounds.height - inset,
        buttonBounds.x + buttonBounds.width -
          inset, buttonBounds.y + inset)

      // Reset the old stroke, I don't want complications with other components
      g.setStroke(oldStroke)
    }
    ret.addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent): Unit = {
        InternalFrame.this.setVisible(v = false)
        onClosed()
      }
    })
    ret
  }

  override def draw(g: Graphics2D) = {
    if (isVisible) {
      g.setColor(FrameStyle.InnerColor)
      g.fillRect(getX, getY, getWidth, getHeight)

      comps.foreach(e => {
        if (getBounds.intersects(e.getBounds.getBounds2D)) {
          e.draw(g)
        }
      })
    }
  }

  /** Adds a component to the internal frame.
    *
    * @param c The component.
    * @return Nothing.
    */
  def <<(c: Component) = {
    comps += c
    c.setParent(this)
    c.onMoved += { _ =>
      if (!c.getBounds.intersects(this.getBounds.getBounds2D)) {
        LogFacility.log("Component \"" + c.getName + "\" is not intersecting bounds of frame \"" + getName + "\" " +
          "anymore. Ignoring component in drawing process...", "Warning")
      }
    }
  }

  /** Ditto. */
  def add(c: Component) = this << c

  /** The containers that are contained by the frame. */
  def containedComponents = comps.toList

  // Sets the visibility of not only itself, but every component that is registered to the frame.
  override def setVisible(v: Boolean): Unit = {
    super.setVisible(v)
    comps foreach {
      _.setVisible(v)
    }
  }

  // Singleton instance object, represents the top bar which "holds" the close button.
  private object ToplineBar extends Component with MouseDragDetector {

    // 0, 0, getWidth, FrameStyle.CommonInset * 2 + closeButton
    //.getHeight, backingScreen

    setSourceShape(new Rectangle(-InternalFrame.this.getWidth / 2, -InternalFrame.this.getHeight / 2, InternalFrame.this.getWidth, FrameStyle.CommonInset * 2 + closeButton.getHeight))
    setParent(InternalFrame.this)
    setBackingScreen(InternalFrame.this.getBackingScreen)
    setVisible(true)

    onMouseDragDetected += { vec =>
      val frame = InternalFrame.this
      frame.setLocation((frame.getX + vec.x).asInstanceOf[Int], (frame.getY + vec.y).asInstanceOf[Int])
    }

    override protected def draw(g: Graphics2D): Unit = {
      //if (isVisible) {
        g.setColor(FrameStyle.TopBarColor)
        g.fill(getBounds)
      //}
    }
  }

}

object InternalFrame {

  /** Style values used by the internal frame. */
  private object FrameStyle {

    /** Grayish color with a little bit of transparency. */
    lazy val NoMouseColor_Outer = new Color(173, 155, 154, 75)

    /** A grayish-turquoise color. Without transparency yet. */
    lazy val MouseColor_Outer = new Color(0x77B595)

    lazy val InnerColor = new Color(45, 45, 45, 75)

    /** Standard red. Nearly standard red. */
    lazy val CloseButtonColor_Inner = new Color(0xFF672B)

    /** The stroke with which the close button is drawn. */
    lazy val CloseButton_DrawStroke = new BasicStroke(2.5f)

    /** Grayish color with more opaque style, specially picked for the top bar. */
    lazy val TopBarColor = new Color(87, 87, 87, 95)

    lazy val CommonInset = 2

    /** The dimensions of the close button.
      * On the basis of this value the height of the top line bar is calculated.
      */
    lazy val CloseButtonDimension = new Dimension(15, 15)

  }

}

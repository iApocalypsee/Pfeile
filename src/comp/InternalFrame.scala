package comp

import java.awt._
import java.awt.event.{MouseAdapter, MouseEvent}
import java.util.concurrent.CopyOnWriteArrayList

import general.property.{DynamicProperty, StaticProperty}
import general.{ClearableDelegate, Delegate, Function0Delegate, LogFacility}
import gui.FrameContainer
import gui.screen.Screen

import scala.collection.JavaConversions

/**
  * An internal frame capable of containing components in order to make a more "cleaner" UI.
  * The coordinates of components are given in absolute coordinates (not relative to the frame's
  * upper left corner), but this is still a WIP.
  *
  * =Visibility of the frame=
  * To open or close the frame, set the setVisible property accordingly. Note that corresponding delegates
  * will be triggered when the property's value is changed.
  *
  * @param x Initial x position of the frame.
  * @param y Initial y position of the frame.
  * @param width Ditto.
  * @param height Ditto.
  * @param backingScreen Ditto.
  */
class InternalFrame(x: Int, y: Int, width: Int, height: Int, name: String, backingScreen: Screen) extends Component(x, y, width,
  height, backingScreen) {

  import comp.InternalFrame._

  /**
    * Called when the internal frame's close button has been pressed.
    */
  val onClosed: Function0Delegate with ClearableDelegate = Delegate.createZeroArity

  /**
    * Called when the internal frame has been opened again.
    */
  val onOpened: Function0Delegate with ClearableDelegate = Delegate.createZeroArity

  // <editor-fold desc="Constructor code">

  // Check new components if their bounds really are inside the frame's bounds
  onChildAdded += { component =>
    component.getTransformation.onTranslated += { _ =>
      if (!this.getBounds.intersects(component.getBounds.getBounds2D)) {
        LogFacility.log("Component \"" + component.getName + "\" is not intersecting bounds of frame \"" + getName + "\" " +
          "anymore. Ignoring component in drawing process...", "Warning")
      }
    }
    comps = new CopyOnWriteArrayList[Component](getChildren.values())
  }

  // Check if the backing screen can actually hold frames (ability supplied by FrameContainer trait)
  backingScreen match {
    case frameContainer: FrameContainer =>
      frameContainer.frameContainer.addFrame(this)
    case _ => LogFacility.log(s"Frame $this cannot be added to screen; screen not a FrameContainer instance", "Warning")
  }

  this.add(ToplineBar)
  this.setVisible(false)
  this.setName("Internal Frame: " + name)

  // </editor-fold>

  /**
    * The color that the top bar is using.
    * Don't put null in there.
    */
  val topBarColor = new DynamicProperty(FrameStyle.DefaultTopBarColor) {
    dynPass (color => require(color != null))
  }

  /**
    * The background used by the frame. Can be transparent, but does not have to be.
    */
  val background = new StaticProperty[ImageLike](new SolidColor(FrameStyle.DefaultBackgroundColor))

  @volatile private var comps = new CopyOnWriteArrayList[Component]

  /**
    * The close button of the frame.
    */
  private lazy val closeButton: Button = {
    val ret = new Button(0, 0, backingScreen, "")
    val xInBounds = x + width - FrameStyle.CommonInset - FrameStyle.CloseButtonDimension.width
    val yInBounds = y + FrameStyle.CommonInset
    val widthInBounds = FrameStyle.CloseButtonDimension.width
    val heightInBounds = FrameStyle.CloseButtonDimension.height

    // Set the close button up according to the rules specified in [[FrameStyle]].
    ret.setSourceShape(new Rectangle(-widthInBounds / 2, -heightInBounds / 2, widthInBounds, heightInBounds))

    // Add the new button to the frame so that it receives (positional) changes.
    this.add(ret)

    ret.setLocation(xInBounds, yInBounds)

    // Add the width to x and height to y because:
    //    The new transform system allows bounds to define custom anchor middle points.
    //    The best way of handling certain transformations is to put the anchor middle point
    //    in the center of the component.
    //ret.getTransformation.translate(widthInBounds + xInBounds, heightInBounds + yInBounds)

    ret.getBorder.setInnerColor(FrameStyle.CloseButtonColor_Inner)
    ret.setAdditionalDrawing { g =>
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
        InternalFrame.this.setVisible(false)
      }
      override def toString: String = "MouseAdapter: InternalFrame#CloseButton"
    })
    ret.setName("frame: closeButton")
    ret.getBackingScreen.putAfter(this, ret)
    ret
  }

  override def draw(g: Graphics2D): Unit = {
    background.get.drawImage(g, getX, getY, getWidth, getHeight)

    val it = comps.iterator()
    while(it.hasNext) {
      val comp = it.next()
      comp.drawChecked(g)
    }

    closeButton.draw(g)
  }

  override def setVisible(vvvvvv: Boolean): Unit = {
    val wasVisible = isVisible
    super.setVisible(vvvvvv)
    if (vvvvvv && !wasVisible) onOpened()
    else if (!vvvvvv && wasVisible) onClosed()
  }

  def open(): Unit = setVisible(true)
  def close(): Unit = setVisible(false)

  /**
    * Adds a component to the internal frame.
    * @param c The component to be added.
    */
  def add(c: Component): Unit = {
    require(c.getBackingScreen == this.getBackingScreen, s"Backing screens not equal for $this and $c")
    c.setParent(this)

    val screen = getBackingScreen
    screen.putAfter(this, c)
  }

  /** The containers that are contained by the frame. */
  def containedComponents = JavaConversions.asScalaBuffer(comps).toList

  // Singleton instance object, represents the top bar which "holds" the close button.
  private object ToplineBar extends Component with MouseDragDetector {

    private val defaultBarHeight = FrameStyle.CommonInset * 2 + closeButton.getHeight
    setSourceShape(new Rectangle(-InternalFrame.this.getWidth / 2, -defaultBarHeight / 2, InternalFrame.this.getWidth, defaultBarHeight))
    setBackingScreen(InternalFrame.this.getBackingScreen)
    setName(InternalFrame.this.getName + ": toplinebar")
    setLocation(0, 0)

    onMouseDragDetected += { vec =>
      val frame = InternalFrame.this
      frame.move(vec.getX.asInstanceOf[Int], vec.getY.asInstanceOf[Int])
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(FrameStyle.DefaultTopBarColor)
      g.fill(getBounds)
      g.setColor(FrameStyle.TEXT_COLOR)
      g.setFont(FrameStyle.FONT)
      g.drawString(name, getBounds.getBounds.x + 9, getBounds.getBounds.y + 16)
      g.setFont(Component.STD_FONT)
    }
  }
}

object InternalFrame {

  /** Style values used by the internal frame. */
  private object FrameStyle {

    /** the Color of the name of the frame, printed on left of the topline bar */
    val TEXT_COLOR: Color = new Color(221, 218, 193)

    /** the font with which the Text in the Baseline is drawn */
    val FONT: Font = Component.STD_FONT.deriveFont(Font.PLAIN, 17)

    /** Grayish color with a little bit of transparency. */
    val NoMouseColor_Outer = new Color(173, 155, 154, 75)

    /** A grayish-turquoise color. Without transparency yet. */
    val MouseColor_Outer = new Color(0x77B595)

    val DefaultBackgroundColor = new Color(45, 45, 45, 75)

    /** Standard red. Nearly standard red. */
    val CloseButtonColor_Inner = new Color(0xFF672B)

    /** The stroke with which the close button is drawn. */
    val CloseButton_DrawStroke = new BasicStroke(2.5f)

    /** Grayish color with more opaque style, specially picked for the top bar. */
    val DefaultTopBarColor = new Color(87, 87, 87, 95)

    val CommonInset = 3

    /**
      * The dimensions of the close button.
      * On the basis of this value the height of the top line bar is calculated.
      */
    val CloseButtonDimension = new Dimension(15, 15)

  }
}

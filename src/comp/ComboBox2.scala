package comp

import java.awt.Graphics2D
import java.awt.event.{MouseAdapter, MouseEvent}
import java.util.function._
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import general.Delegate
import general.JavaInterop._
import gui.screen.Screen

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.compat.java8.FunctionConverters._

/**
  * Second implementation of a combo box, hopefully with fewer bugs and fewer needs to rewrite combo box code.
  *
  * Height of combo box is restricted for now to be [[comp.ComboBox2.Values#SizeCollapseButton]]
  *
  * @constructor Constructs a clean combo box without elements.
  * @param x Self-evident.
  * @param y Self-evident.
  * @param initComboBoxWidth Self-evident.
  * @param backing Self-evident.
  */
class ComboBox2(x: Int, y: Int, initComboBoxWidth: Int, backing: Screen) extends Component {

  /**
    * Called when an element has been selected.
    * Argument will contain the index of the element as well as the element itself.
    */
  val onElementSelected = Delegate.create[(String, Int)]

  private val height = Values.SizeCollapseButton
  private val outer = this

  private var m_elements = mutable.ArrayBuffer[String]()
  private val m_display = new Label(0, 0, backing, Values.EmptyText)

  // <editor-fold desc="Component initialization">

  setSourceShape(Component.originCenteredRectangle(initComboBoxWidth, height))
  setX(x)
  setY(y)
  setWidth(initComboBoxWidth)
  setHeight(height)
  setBackingScreen(backing)

  m_display.setParent(this)
  m_display.setFontColor(java.awt.Color.white)
  m_display.setRelativeX(Values.InsetLeftDisplayLabel)
  m_display.setRelativeY(Values.InsetTopDisplayLabel)

  ComboxBoxList.initialize()
  ExpandCollapseButton.initialize()

  getBackingScreen.forcePullFront(m_display)

  onResize += { vec =>
    recomputePositions()
    recomputeDimensions()
  }

  collapse()

  private def recomputePositions(): Unit = {
    ExpandCollapseButton.setRelativeX(this.getWidth - ExpandCollapseButton.getWidth)
    // Scale changes do not automatically propagate through the chain.
    ComboxBoxList.setRelativeX(0)
    m_display.setRelativeX(Values.InsetLeftDisplayLabel)
  }

  private def recomputeDimensions(): Unit = {
    ComboxBoxList.setWidth(this.getWidth)
  }

  // </editor-fold>

  /**
    * Immutable view of every element in the combo box.
    */
  def elements = m_elements.toSeq

  /**
    * Immutable view of every element in the combo box.
    */
  def getElements = m_elements.asJava.toImmutableList

  /**
    * Is the underlying combo box list visible?
    */
  def isExpanded = ComboxBoxList.isVisible

  /**
    * Is the underlying combo box list not visible?
    */
  def isCollapsed = !isExpanded

  /**
    * Expands or collapses the underlying list, depending on whether it is visible or not.
    */
  def negateListVisibility(): Unit = {
    ExpandCollapseButton.negatePush()
    ComboxBoxList.negateVisibility()
  }

  /**
    * Opens the underlying list of the combo box, if not already open.
    */
  def expand(): Unit = if(isCollapsed) negateListVisibility()

  /**
    * Closes the underlying list of the combo box, if not already closed.
    */
  def collapse(): Unit = if(isExpanded) negateListVisibility()

  /**
    * Selects an element by index.
    * If no element corresponds to given index, the method fails silently.
    *
    * @param idx The element to select, zero-based.
    */
  def select(idx: Int): Unit = {
    m_elements.zipWithIndex.find(elem => elem._2 == idx).foreach { tuple =>
      val (text, _) = tuple
      m_display.setText(text)
      onElementSelected(tuple)
    }
  }

  /**
    * Note to meself: this method can fail sometimes to recognize correct label when label is selected.
    * Especially in MouseAdapter implementation.
    */
  private def newLabel(text: String): Label = {
    val ret = new Label(0, 0, getBackingScreen, text)
    ret.setText(text)
    ret.addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent) = {
        select(m_elements.indexOf(ret.getText))
        negateListVisibility()
      }
    })
    ret
  }

  /**
    * Appends a new combo box element to the end of the selection list.
    */
  def appendElement(x: String): Unit = {
    m_elements += x
    if (m_display.getText == Values.EmptyText) m_display.setText(x)
    // Label's position will get recomputed inside the AbstractList
    ComboxBoxList.appendElement(newLabel(x))
  }

  /**
    * Prepends a new combo box element.
    */
  def prependElement(x: String): Unit = {
    m_elements prepend x
    if (m_display.getText == Values.EmptyText) m_display.setText(x)
    // Label's position will get recomputed inside the AbstractList
    ComboxBoxList.prependElement(newLabel(x))
  }

  /**
    * Removes a specific item by zero-based index.
    */
  def removeElement(idx: Int): Unit = {
    m_elements.remove(idx)
    ComboxBoxList.removeElement(idx)
    postRemove()
  }

  /**
    * Removes a specific element from the combo box.
    * If it does not exist, it silently fails.
    */
  def removeElement(x: String): Unit = removeElement(m_elements.indexOf(x))

  /**
    * Removes all elements that satisfy given predicate.
    */
  def removeElementsWith(x: Predicate[String]): Unit = {
    m_elements = m_elements filterNot x.asScala
    postRemove()
  }

  /**
    * Called after any of the available 'remove'-functions have been called.
    * When this function is called, the combo box cannot guarantee that an element has been truthfully removed.
    */
  private def postRemove(): Unit = {
    if(m_elements.isEmpty) m_display.setText(Values.EmptyText)
  }

  override def draw(g: Graphics2D) = {
    g.setColor(Values.ColorBackground)
    g.fill(getBounds)
  }

  /**
    * The list displaying all the elements the combo box holds.
    */
  private[ComboBox2] object ComboxBoxList extends NormalList with AbstractList.DefaultInsets[Label] {

    setBackingScreen(backing)
    setVisible(true)
    setWidth(outer.getWidth)
    setHeight(outer.getHeight)

    /**
      * @see [[comp.ComboBox2.ExpandCollapseButton#initialize()]]
      */
    def initialize(): Unit = {
      setParent(outer)
      setRelativeLocation(0, height)
      getBackingScreen.putAfter(outer, this)
    }

    def negateVisibility() = setVisible(!isVisible)

    // A quirk in the 'Consolas' font requires the top insets to be a little bit less than normal.
    override def topInset = super.topInset - 4

    override protected def recalculatedDimensions = {
      new java.awt.Dimension(outer.getWidth, super.recalculatedDimensions.height)
    }
  }

  /**
    * Separation of the button used for displaying/hiding the list of elements of the combo box.
    * The button assumes the shape of quad, once constructed.
    *
    * When the documentation refers to the 'triangle', it is considered to be the expand-collapse
    * triangle that changes the direction it is pointing when the button is clicked on. This triangle will be
    * drawn inside the expand-collapse button.
    */
  private[ComboBox2] object ExpandCollapseButton extends Button(0, 0, backing, "") {

    // Convenience renames.
    import java.awt.{BasicStroke, Graphics2D, Point => AwtPoint}

    import geom.{Vector => Vector2}

    /**
      * Interpolation factor for 'original => target'.
      * When in range of [0.0, 0.5[, the triangle should be facing downwards.
      * When in range of ]0.5, 1.0], the triangle should be facing upwards.
      * When 0.5, the triangle should degenerate to a line.
      */
    // Initial value of 0.5 makes sure the triangle's insets are correct when it is displayed
    // to the user for the first time.
    private var m_pushFactor = 0.5

    /**
      * How fast the triangle changes the direction it is pointing to.
      */
    private var m_pushDelta = Values.DeltaInterpolationTriangle
    private var m_newHeightsNeeded = false

    // Middle point of expand-collapse triangle.
    private var m_mid: AwtPoint = null
    // Same for left and right.
    private var m_left: AwtPoint = null
    private var m_right: AwtPoint = null

    // <editor-fold desc="Constructor initialization">

    getTransformation.onTranslated += { e =>
      m_newHeightsNeeded = true
    }

    addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent) = onClick()
    })

    recalculateTrianglePoints(m_pushFactor)

    // </editor-fold>

    // <editor-fold desc="Triangle">

    // Used for defining the draw polygon to AWT
    def xPoints = Array(m_mid.x, m_left.x, m_right.x)
    def yPoints = Array(m_mid.y, m_left.y, m_right.y)

    // Where the individual points of the triangle would be by default, if no input is given.
    //def midOriginal = new geom.Vector(outer.getX + outer.getWidth - this.getWidth / 2, outer.getY + Values.InsetTopBottomTriangle + Values.HeightTriangle)
    //def leftOriginal = new geom.Vector(outer.getX + outer.getWidth - Values.WidthTriangle - Values.InsetLeftRightTriangle, outer.getY + Values.InsetTopBottomTriangle)
    def midOriginal = new Vector2(0, Values.HeightTriangle / 2)
    def leftOriginal = new Vector2(-Values.WidthTriangle / 2, -Values.HeightTriangle / 2)
    def rightOriginal = leftOriginal + new geom.Vector(Values.WidthTriangle, 0)

    // Where the individual points of the triangle should be when the triangle fully unfolds.
    def midTarget = midOriginal + new geom.Vector(0, -Values.HeightTriangle)
    def leftTarget = leftOriginal + new geom.Vector(0, Values.HeightTriangle)
    def rightTarget = rightOriginal + new geom.Vector(0, Values.HeightTriangle)

    /**
      * Changes the push/triangle interpolation factor based on given delta and issues a flag
      * for recalculating the individual points of the triangle, if needed.
      */
    def incrementPush(): Unit = {
      m_pushFactor += m_pushDelta
      m_newHeightsNeeded = m_pushFactor <= 1.0 && m_pushFactor >= 0.0
    }

    /**
      * Reverses the direction the triangle is stretching to.
      */
    def negatePush(): Unit = {
      m_pushDelta *= -1
      // Safety call, bring the push factor into valid range [0.0; 1.0], I fear
      // floating point precision errors.
      incrementPush()
      m_newHeightsNeeded = true
    }

    /**
      * Recalculates the individual points of the triangle based off of the original and target positions
      * and given interpolation factor.
      *
      * @param push The interpolation factor determining the vertices' position.
      */
    def recalculateTrianglePoints(push: Double): Unit = {
      m_mid = Vector2.awtPoint(Vector2.lerp(midOriginal, midTarget, push))
      m_left = Vector2.awtPoint(Vector2.lerp(leftOriginal, leftTarget, push))
      m_right = Vector2.awtPoint(Vector2.lerp(rightOriginal, rightTarget, push))
    }

    // </editor-fold>

    /**
      * Deferred initialization; the effects of the expressions in this function depend on previous calls.
      * I am not sure when the constructor of singletons in Scala classes is called exactly.
      */
    def initialize(): Unit = {
      setParent(outer)
      setSourceShape(Component.originCenteredRectangle(Values.SizeCollapseButton, Values.SizeCollapseButton))
      setRelativeLocation(outer.getWidth - Values.SizeCollapseButton, 0)
      setAdditionalDrawing(drawTriangle)
      getBorder.setStroke(new BasicStroke(0))
      getBackingScreen.putAfter(outer, this)
    }

    def onClick(): Unit = negateListVisibility()

    private def drawTriangle(g: Graphics2D): Unit = {
      // Recalculation of triangle points should be in update loop, if only it existed...
      if (m_newHeightsNeeded) recalculateTrianglePoints(m_pushFactor)
      if (m_newHeightsNeeded) incrementPush()

      val translateX = Values.WidthTriangle / 2 + this.getX + Values.InsetLeftRightTriangle
      val translateY = Values.HeightTriangle / 2 + this.getY + Values.InsetTopBottomTriangle

      g.translate(translateX, translateY)
      g.setPaint(Values.PaintTriangle)
      g.fillPolygon(xPoints, yPoints, 3)
      g.translate(-translateX, -translateY)
    }

  }

  private[ComboBox2] object Values {

    import java.awt.{Color, LinearGradientPaint}

    /**
      * Side length of the expand-collapse button.
      */
    val SizeCollapseButton = 30

    /**
      * Width of the triangle.
      *
      * @see [[comp.ComboBox2.ExpandCollapseButton]]
      */
    val WidthTriangle = 20

    /**
      * Height of the triangle.
      *
      * @see [[comp.ComboBox2.ExpandCollapseButton]]
      */
    val HeightTriangle = 20

    val InsetLeftRightTriangle = (SizeCollapseButton - WidthTriangle) / 2
    val InsetTopBottomTriangle = (SizeCollapseButton - HeightTriangle) / 2

    val InsetTopDisplayLabel = 3
    val InsetLeftDisplayLabel = 5

    /**
      * How fast the triangle changes the direction it is pointing to.
      *
      * Set this value to `0.05` for good results; value may vary slightly based on frames per second
      * (since triangle recalculation is packed into the draw hierarchy).
      *
      * @see [[comp.ComboBox2.ExpandCollapseButton]]
      */
    val DeltaInterpolationTriangle = 0.05

    val ColorBackground = Color.darkGray

    val ColorText = new Color(115, 115, 115)

    /**
      * Default text, displayed when no elements are in the combo box.
      */
    val EmptyText = "-none-"

    /**
      * The stuff with which the triangle gets filled when it is being drawn.
      * See documentation of `LinearGradientPaint` for this.
      */
    //val PaintTriangle = new LinearGradientPaint(-24, -27, 23, 20, Array(0.0f, 0.2f, 1.0f), Array(new Color(0, 0, 55), new Color(0, 155, 155), new Color(255, 255, 50)))
    val PaintTriangle = new LinearGradientPaint(-24, -27, 23, 20, Array(0.0f, 0.2f, 1.0f), Array(new Color(55, 55, 55), new Color(155, 155, 155), new Color(122, 122, 188)))

  }

}

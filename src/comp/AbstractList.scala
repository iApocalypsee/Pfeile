package comp

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.{Color, Dimension, Graphics2D, Point}
import java.util.function._

import general.JavaInterop.JavaPrimitives.JavaInt
import general.JavaInterop._
import general.{Delegate, LogFacility}

import scala.collection.JavaConverters._
import scala.collection.mutable

abstract class AbstractList[A <: Component] extends Component(Component.originCenteredRectangle(2, 2)) {

  /**
    * Called when an element of the list has been selected.
    */
  val onElementSelected = Delegate.create[(A, JavaInt)]

  // Easier access to the AbstractList instance from inner classes
  private val outer = this

  // Listing of all elements.
  private val m_elements = mutable.ArrayBuffer[Element]()

  def elements = m_elements.map(elem => elem.component)
  def getElements = elements.asJava.toImmutableList

  private def select(idx: Int): Unit = {
    val optComponent = elements.zipWithIndex.find({ case (e, i) => idx == i }).map({ case (e, i) => e })
    optComponent.foreach(component => onElementSelected(component, elements.indexOf(component)))
  }

  // <editor-fold desc="Initialization code">

  onTransformed += { evt =>
    m_elements foreach { elem => elem.recalculateSupposedPosition() }
  }

  // </editor-fold>

  // <editor-fold desc="Coloring">

  // The background used to draw the inside of the list.
  protected var background: ImageLike = new SolidColor(new Color(0x565461))

  // </editor-fold>

  //<editor-fold desc="Dimensions of AbstractList itself">

  /**
    * Recalculates the dimensions of this list by taking the extreme values of every component.
    * "Extreme values" are the value of the widest component in the list as well as the value
    * of every component's height added up.
    *
    * This property can be overridden by subclasses to generate a dimension of their own.
    *
    * @return The new, raw dimensions of this list.
    */
  protected def recalculatedDimensions: Dimension = {

    val elementCount = m_elements.size

    val componentHeightsAccumulated = m_elements.foldLeft(0) { (heightSoFar, elem) => heightSoFar + elem.component.getHeight }

    val widestComponentWidth = m_elements.foldLeft(0) { (width, elem) =>
      if (elem.component.getWidth > width) elem.component.getWidth
      else width
    }

    val betweenElementsInsets = if (elementCount == 0) 0 else (elementCount - 1) * elementInset

    val resultingHeight = topInset + bottomInset + betweenElementsInsets + componentHeightsAccumulated
    val resultingWidth = leftInset + rightInset + widestComponentWidth

    new Dimension(resultingWidth, resultingHeight)

  }

  /**
    * Recalculates most of the dimensions and position data of the GUI list as well as of the components
    * governed by this list.
    */
  private def refreshDimensions(): Unit = {
    val newDimensions = this.recalculatedDimensions

    setWidth(newDimensions.width)
    setHeight(newDimensions.height)

    m_elements.foreach { element => element.recalculateSupposedPosition() }
  }

  //</editor-fold>

  //<editor-fold desc="Addition and removal of elements"

  private def preAddCheck(x: A): Unit = {
    require(x != null)
    require(!m_elements.contains(x),
      s"""
         |Component already present in list.
         | - component='$x'
         | - implementingClass='${this.getClass.getName}'
         | - elements='$m_elements'
         |
         |Make sure you don't add the same component twice over, it would
         | a) make no sense and
         | b) make debugging more difficult.
         |If you are appending the same component twice on purpose, overthink your design choice.
       """.stripMargin)
  }

  private def postAdd(x: A): Unit = {
    x.setParent(this)
    x.addMouseListener(new MouseAdapter {
      override def mouseReleased(e: MouseEvent) = {
        select(elements.indexOf(x))
      }
    })
    refreshDimensions()
  }

  /**
    * Adds an element to the end of the list and recalculates the bounds.
    */
  def appendElement(x: A): Unit = {
    preAddCheck(x)
    m_elements += Element(x)
    postAdd(x)
  }

  /**
    * Adds an element to the beginning of the list and recalculates the bounds.
    * I presume you will rarely need this method, but it's there if you need it, so: go nuts.
    */
  def prependElement(x: A): Unit = {
    preAddCheck(x)
    m_elements prepend Element(x)
    postAdd(x)
  }

  /**
    * Applies a function to every element of this GUI list.
    *
    * @param x The function to apply.
    */
  def foreach(x: Consumer[A]): Unit = {
    m_elements.foreach(elem => x.accept(elem.component))
  }

  /**
    * Removes a specific element from the list, and recomputes the bounds and the positions again.
    */
  def removeElement(x: A): Boolean = removeElement((c: A) => c == x)

  /**
    * Removes one specific element by index.
    *
    * @param idx The element referenced by index to remove.
    * @return If the element has been successfully removed, `true`.
    */
  def removeElement(idx: Int): Boolean = removeElement(c => m_elements.indexOf(c) == idx)

  /**
    * Removes a specific element matching with given predicate function 'f'.
    * Recomputes the bounds and the positions on successful removal.
    *
    * @return If at least one element got removed, `true`.
    */
  def removeElement(f: Predicate[A]): Boolean = {
    val satisfyingMatch = m_elements.find(elem => f.test(elem.component))

    satisfyingMatch.foreach { removedElement =>
      onRemovedInternal(removedElement)
      onRemoved(removedElement)
      postRemoved(removedElement)
    }

    satisfyingMatch.isDefined
  }

  /**
    * Removes all elements which satisfy given predicate. Recomputes the bounds on successful removal.
    *
    * Code for this function is essentially a copy-paste of [[comp.AbstractList#removeElement(java.util.function.Predicate)]].
    *
    * @param f The predicate to test all labels against.
    * @return If at least one element got removed, `true`.
    */
  def removeElementsWith(f: Predicate[A]): Boolean = {
    val satisfyingMatch = m_elements.filter(elem => f.test(elem.component))

    satisfyingMatch.foreach { removedElement =>
      onRemovedInternal(removedElement)
      onRemoved(removedElement)
      postRemoved(removedElement)
    }

    satisfyingMatch.nonEmpty
  }

  /**
    * Removes every element of this list.
    */
  def clear(): Unit = {
    removeElementsWith(_ => true)
  }

  /**
    * Internal remove callback for keeping up the AbstractList interest in having the Element
    * disposed properly.
    */
  private def onRemovedInternal(x: Element): Unit = {

    assert(!x.resizeHandler.isValid,
      s"""
         |Resize handler of Element instance not invalidated.
         | - element='$x'
         | - elements='$m_elements'
         |
         |Check class Delegate.Handle if the handler is really invalidated. The handler should
         |not be valid if the component has left the list.
         |The AbstractList is supposed to guarantee invalidation of the handler.
       """.stripMargin)

    assert(!x.transformHandler.isValid,
      s"""
         |Transformation handler of Element instance to component not invalidated.
         | - element='$x'
         | - elements='$m_elements'
         |
         |Check class Delegate.Handle if the handler is really invalidated. The handler should
         |not be valid if the component has left the list.
         |The AbstractList is supposed to guarantee invalidation of the handler.
       """.stripMargin)

  }

  /**
    * Called after the [[comp.AbstractList.onRemoved]] method.
    */
  private def postRemoved(x: Element): Unit = {
    // Require that given element has not been made available to this list again.
    require(!m_elements.contains(x),
      s"""
         |Component determined for removal available to the GUI list again
         | - component='$x'
         | - implementingClass='${this.getClass.getName}'
         | - elements='$m_elements'
         |
         |Given component has been somehow made available to the GUI list once again. Make sure
         |no threads accidentally add it to the list again or that the AbstractList.onRemoved callback
         |does not append it to the list.
       """.stripMargin)

    // It's here where the GUI list can be completely assured the specified component
    // is really gone.
    refreshDimensions()
  }

  /**
    * Callback for subclasses to override when an element has been successfully determined
    * to be removed from the GUI list.
    */
  protected def onRemoved(x: Element): Unit = {}

  //</editor-fold>

  //<editor-fold desc="Insets">

  /**
    * Inset from the first element to the top of the list in pixels.
    */
  def topInset: Int

  /**
    * Inset in between elements in pixels.
    */
  def elementInset: Int

  /**
    * Inset from the last element to the bottom of the list in pixels.
    */
  def bottomInset: Int

  /**
    * Inset from the left, applied to all components in the list.
    */
  def leftInset: Int

  /**
    * Inset from the right, applied to all components in the list.
    */
  def rightInset: Int

  //</editor-fold>

  //<editor-fold desc="Related to a single element">

  /**
    * Wraps specified component as single element in the GUI list.
    */
  private case class Element(component: A) {

    //<editor-fold desc="Event handlers">

    private[AbstractList] val resizeHandler = component.onResize += { vector => refreshDimensions() }

    private[AbstractList] val transformHandler = component.onTransformed += { transform =>

      if(!transform.isInstanceOf[TranslationChange]) {
        LogFacility.logMethodWithMessage(
          s"""
             |$transform invalid. Make sure you don't apply any transformation to a component
             |that has been added to a list component. Otherwise it would break the list layout quite
             |noticeably.
         """.stripMargin)
        throw new NotImplementedError("Implement cancellation of transformation in class TransformationEvent")
      }

    }

    //</editor-fold>

    //<editor-fold desc="Determination of supposed GUI position">

    /**
      * The position where the element is supposed to be drawn.
      * If the component's position varies, I will know.
      */
    private var m_supposedPosition: Point = null

    /**
      * @see m_supposedPosition
      */
    def supposedPosition = m_supposedPosition

    /**
      * Recalculate the position where this element is supposed to be drawn.
      * If the underlying component's position varies, I will know instantly...
      */
    def recalculateSupposedPosition(): Unit = {

      val elementsSoFar = previousElements

      val elementCountSoFar = elementsSoFar.size

      val componentHeightsAccumulatedSoFar = elementsSoFar.foldLeft(0) { (heightSoFar, elem) => heightSoFar + elem.component.getHeight }

      val valueX = outer.getX + leftInset
      val valueY = outer.getY + topInset + elementCountSoFar * elementInset + componentHeightsAccumulatedSoFar

      m_supposedPosition = new Point(valueX, valueY)

      component.setX(valueX)
      component.setY(valueY)

    }

    //</editor-fold>

    /**
      * Returns all elements that come before this element, not including this element.
      */
    def previousElements: mutable.ArrayBuffer[Element] = {
      val splitPosition = m_elements.indexOf(this)
      val (prev, _) = m_elements.splitAt(splitPosition)
      prev
    }

  }

  //</editor-fold>

}

object AbstractList {

  /**
    * Scala trait for mixing in if I ever get too lazy to enter inset values myself again.
    */
  trait DefaultInsets[A <: Component] extends AbstractList[A] {
    override def topInset = 5
    override def leftInset = 5
    override def rightInset = 5
    override def bottomInset = 5
    override def elementInset = 0
  }

}

class NormalList extends AbstractList[Label] {

  /**
    * Inset from the last element to the bottom of the list in pixels.
    */
  override def bottomInset = 10

  /**
    * Inset from the right, applied to all components in the list.
    */
  override def rightInset = 10

  /**
    * Inset in between elements in pixels.
    */
  override def elementInset = 5

  /**
    * Inset from the first element to the top of the list in pixels.
    */
  override def topInset = 10

  /**
    * Inset from the left, applied to all components in the list.
    */
  override def leftInset = 10

  override def draw(g: Graphics2D) = {

    background.drawImage(g, getX, getY, getWidth, getHeight)

    foreach { (label: comp.Label) =>
      label.draw(g)
    }

  }

}

package comp

import java.awt.{Polygon, Graphics2D}
import java.awt.event.{MouseWheelListener, MouseEvent, MouseMotionListener, MouseListener}

import comp.Component.ComponentStatus
import gui.Screen

/**
 * The only thing that is abstract in this trait is the draw(Graphics2D) method
 * from Drawable.
 * The trait is designed with delegation concept. Don't make it overcomplicated...
 * @author Josip Palavra
 * @version 24.07.2014
 */
trait StandardComponent extends IComponent {

  private val c = new Component() {
    override def draw(g: Graphics2D): Unit = StandardComponent.this.draw(g)
  }

  def makeChildrenOf(p: Component) = c.makeChildrenOf(p)
  def defaultMakeChildrenOf(p: Component) = c.defaultMakeChildrenOf(p)
  def chain() = c.chain()
  def unchain() = c.unchain()
  override def getStatus = c.getStatus
  override def setStatus(status: ComponentStatus) = c.setStatus(status)
  override def getBackingScreen = c.getBackingScreen
  override def setBackingScreen(backingScreen: Screen) = c.setBackingScreen(backingScreen)
  override def getMouseListeners = c.getMouseListeners
  override def addMouseListener(m: MouseListener) = c.addMouseListener(m)
  override def removeMouseListener(m: MouseListener) = c.removeMouseListener(m)
  override def getMouseMotionListeners = c.getMouseMotionListeners
  override def addMouseMotionListener(e: MouseMotionListener) = c.addMouseMotionListener(e)
  override def removeMouseMotionListener(m: MouseMotionListener) = c.removeMouseMotionListener(m)
  override def getMouseWheelListeners = c.getMouseWheelListeners
  override def getX = c.getX
  override def setX(x: Int) = c.setX(x)
  override def getY = c.getY
  override def setY(y: Int) = c.setY(y)
  override def getWidth = c.getWidth
  override def setWidth(width: Int) = c.setWidth(width)
  override def getHeight = c.getHeight
  override def setHeight(height: Int) = c.setHeight(height)
  override def scale(x: Double, y: Double) = c.scale(x, y)
  def assumeRect(x: Int, y: Int) = c.assumeRect(x, y)
  override def getBounds = c.getBounds
  def setBounds(bounds: Polygon) = c.setBounds(bounds)
  override def getSimplifiedBounds = c.getSimplifiedBounds
  override def acceptInput() = c.acceptInput()
  override def declineInput() = c.declineInput()
  def virtualClick() = c.virtualClick()
  def remove(c: Component) = c.remove(c)
  override def isVisible = c.isVisible
  override def setVisible(vvvvvv: Boolean) = c.setVisible(vvvvvv)
  def isChained = c.isChained
  def hasParent = c.hasParent
  override def getName = c.getName
  def setName(name: String) = c.setName(name)
  override def isAcceptingInput = c.isAcceptingInput
  def getParent = c.getParent
  def getAbsoluteX = c.getAbsoluteX
  def getAbsoluteY = c.getAbsoluteY
  def setAbsoluteX(x: Int) = c.setAbsoluteX(x)
  def setAbsoluteY(y: Int) = c.setAbsoluteY(y)
  def relativeCoordinates(abs_x: Int, abs_y: Int) = c.relativeCoordinates(abs_x, abs_y)
  def absoluteCoordinates(rel_x: Int, rel_y: Int) = c.absoluteCoordinates(rel_x, rel_y)
  override def getBorder = c.getBorder
  def setBorder(border: Border) = c.setBorder(border)
  def updateGUI() = c.updateGUI()
  override def isMouseFocused = c.isMouseFocused
  override def triggerListeners(event: MouseEvent) = c.triggerListeners(event)
  def center() = c.center()
  override def removeMouseWheelListener(mouseWheelListener: MouseWheelListener) = c.removeMouseWheelListener(mouseWheelListener)
  override def addMouseWheelListener(mouseWheelListener: MouseWheelListener) = c.addMouseWheelListener(mouseWheelListener)
}

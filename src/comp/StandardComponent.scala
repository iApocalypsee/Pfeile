package comp

import java.awt._
import java.awt.event.{MouseWheelListener, MouseMotionListener, MouseListener, MouseEvent}
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import java.util

import comp.Component.ComponentStatus
import general.Main
import gui.Screen

/**
 *
 * @author Josip Palavra
 * @version 24.07.2014
 */
trait StandardComponent extends IComponent {

  private var x: Int = 0
  private var y: Int = 0
  private var width: Int = 0
  private var height: Int = 0
  private var status: Component.ComponentStatus = ComponentStatus.NO_MOUSE
  private var backingScreen: Screen = null
  /**
   * Das umfassende Polygon um die Komponente.
   */
  private var bounds: Polygon = new Polygon
  /**
   * Der Name des Steuerelements. Wird hauptsächlich für {@link Component#children} benötigt.
   */
  private var name: String = null
  /**
   * Zeigt an, ob die Component willig ist, Input zu akzeptieren.
   * Standardmäßig auf true gesetzt.
   * @see Component#acceptInput()
   * @see Component#declineInput()
   */
  private var acceptingInput: Boolean = true
  /**
   * Zeigt an, ob das Steuerelement sichtbar ist.
   * Wenn nicht, akzeptiert es automatisch auch keinen Input.
   */
  private var visible: Boolean = true
  /**
   * Zeigt an, ob die Koordinaten des Steuerelements verändert werden können.
   * Kann mittels {@link #chain()} und {@link #unchain()} gesteuert werden.
   */
  private var chained: Boolean = false
  /**
   * Die Liste der MouseListener.
   */
  private val mouseListeners = new util.LinkedList[MouseListener]()
  /**
   * Die Liste der MouseMotionListener.
   */
  private val mouseMotionListeners = new util.LinkedList[MouseMotionListener]()
  /**
   * Die Liste der MouseWheelListener.
   */
  private val mouseWheelListeners = new java.util.LinkedList[MouseWheelListener]
  /**
   * Die Steuerelemente, die von diesem hier abhängen. Die Koordinatenangaben der
   * untergeordneten Elemente werden relativ zu diesem hier angegeben.
   */
  private val children = new util.Hashtable[String, Component]
  /**
   * Die Farbgebung innen und außen.
   */
  private var border = new Border
  /**
   * Indicates whether the mouse is inside the components' bounds or not.
   */
  private var mouseFocused: Boolean = false
  /**
   * Sagt aus, ob die Polygon-Bounds oder die {@link #getSimplifiedBounds()}
   * als Standard benutzt werden.
   */
  private final val USING_POLYGON: Boolean = true
  /**
   * Die Standardschriftart.
   */
  final val STD_FONT: Font = new Font("Consolas", Font.PLAIN, 13)
  /**
   * Die Anzahl an Punkten, die je in {@link Polygon#xpoints} und {@link Polygon#ypoints}
   * gespeichert werden können sollen.
   */
  final val POLYGON_BUFFER_CAPACITY: Int = 20
  final val STD_INSETS: Insets = new Insets(5, 5, 5, 5)

  /***** CONSTRUCTING CODE *****/

  bounds.xpoints = new Array[Int](POLYGON_BUFFER_CAPACITY)
  bounds.ypoints = new Array[Int](POLYGON_BUFFER_CAPACITY)

  border.setComponent(this)

  assumeRect(0, 0)

  addMouseMotionListener(new MouseMotionListener {
    def mouseDragged(arg0: MouseEvent) {
    }

    def mouseMoved(arg0: MouseEvent) {
      if (status ne ComponentStatus.MOUSE) {
        status = ComponentStatus.MOUSE
      }
    }
  })
  addMouseListener(new MouseListener {
    def mouseClicked(arg0: MouseEvent) {
    }

    def mouseEntered(arg0: MouseEvent) {
      if (status.ne(ComponentStatus.NOT_AVAILABLE) && status.ne(ComponentStatus.MOUSE)) {
        status = ComponentStatus.MOUSE
      }
      mouseFocused = true
    }

    def mouseExited(arg0: MouseEvent) {
      if (status.ne(ComponentStatus.NOT_AVAILABLE) && status.ne(ComponentStatus.MOUSE)) {
        status = ComponentStatus.NO_MOUSE
      }
      mouseFocused = false
    }

    def mousePressed(arg0: MouseEvent) {
      if (status ne ComponentStatus.CLICK) {
        status = ComponentStatus.CLICK
      }
    }

    def mouseReleased(arg0: MouseEvent) {
      if (status ne ComponentStatus.MOUSE) {
        status = ComponentStatus.MOUSE
      }
    }
  })

  status = ComponentStatus.NO_MOUSE
  name = Integer.toString(hashCode())

  /***** END OF CONSTRUCTING CODE *****/


  /**
   * Legt fest, dass die Koordinaten des Steuerelements nicht verändern werden dürfen. Ist für die
   * Parent-Children-Beziehung in Component gedacht, kann aber auch so verwendet werden.
   * Wenn diese Methode auf einem Steuerelement aufgerufen wird, welches ein Parent-Steuerelement
   * hat, dann ändert sich die <b>relative</b> Position zum Parent-Steuerelement nicht.
   * Dafür kann dieses Steuerelement auch nicht individuell verschoben werden, bis
   * {@link #unchain()} aufgerufen wird.
   *
   * @see #unchain()
   */
  def chain {
    if (!chained) {
      chained = true
    }
  }

  /**
   * Legt fest, dass die Koordinaten des Steuerelements sich verändern dürfen. Dieser
   * Zustand kann von {@link #chain()} wieder aufgelöst werden.
   * @see #chain()
   */
  def unchain {
    if (chained) {
      chained = false
    }
  }

  /**
   * @return the status
   */
  def getStatus: Component.ComponentStatus = {
    return status
  }

  /**
   * @param status
	 * the status to set
   */
  def setStatus(status: Component.ComponentStatus) {
    this.status = status
  }

  /**
   * @return the backingScreen
   */
  def getBackingScreen: Screen = {
    return backingScreen
  }

  /**
   * @param backingScreen
	 * the backingScreen to set
   */
  def setBackingScreen(backingScreen: Screen) {
    if (this.backingScreen != null) {
      this.backingScreen.remove(this)
    }
    this.backingScreen = backingScreen
    this.backingScreen.add(this)
  }

  def getMouseListeners = mouseListeners

  def addMouseListener(m: MouseListener) {
    mouseListeners.add(m)
  }

  def removeMouseListener(m: MouseListener) {
    if (mouseListeners.contains(m)) {
      mouseListeners.remove(m)
    }
  }

  def getMouseMotionListeners = mouseMotionListeners

  def addMouseMotionListener(e: MouseMotionListener) {
    mouseMotionListeners.add(e)
  }

  def removeMouseMotionListener(m: MouseMotionListener) {
    if (mouseMotionListeners.contains(m)) {
      mouseMotionListeners.remove(m)
    }
  }

  def getMouseWheelListeners = mouseWheelListeners

  /**
   * @return the x
   */
  def getX: Int = {
    return x
  }

  /**
   * Setzt die x Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
   * <code>true</code> zurückgibt.
   * @param x Die neue x Position des Steuerelements.
   */
  def setX(x: Int) {
    if (!chained) {
      if (USING_POLYGON) {
        bounds.translate(x - this.x, 0)
        bounds.invalidate
      }
      this.x = x
    }
  }

  /**
   * @return the y
   */
  def getY: Int = {
    return y
  }

  /**
   * Setzt die y Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
   * <code>true</code> ist.
   * @param y Die neue y Position des Steuerelements.
   */
  def setY(y: Int) {
    if (!chained) {
      if (USING_POLYGON) {
        bounds.translate(0, y - this.y)
        bounds.invalidate
      }
      this.y = y
    }
  }

  /**
   * @return the width
   */
  def getWidth: Int = {
    return width
  }

  /**
   * @param width
	 * the width to set
   */
  def setWidth(width: Int) {
    if (USING_POLYGON) {
      if (this.width != 0) {
        scale(width.asInstanceOf[Double] / this.width, 0)
      }
      else {
        assumeRect(width, 0)
      }
    }
    this.width = width
  }

  /**
   * @return the height
   */
  def getHeight: Int = {
    return height
  }

  /**
   * @param height
	 * the height to set
   */
  def setHeight(height: Int) {
    if (USING_POLYGON) {
      if (this.height != 0) {
        scale(0, height.asInstanceOf[Double] / this.height)
      }
      else {
        assumeRect(0, height)
      }
    }
    this.height = height
  }

  /**
   * Streckt {@link #getBounds()}, sodass {@link #getBounds()} mit {@link #getWidth()} und
   * {@link #getHeight()} wieder übereinstimmen.
   * @param x Der Streckfaktor in x Richtung
   * @param y Der Streckfaktor in y Richtung
   */
  def scale(xpar: Double, ypar: Double) {
    var x = xpar
    var y = ypar
    if (x == 0) {
      x = 1
    }
    if (y == 0) {
      y = 1
    }
    val orig_translation: Dimension = new Dimension(bounds.getBounds.x, bounds.getBounds.y)
    bounds.translate(-orig_translation.width, -orig_translation.height)
    if (x != 1.0) {
      {
        var i: Int = 0
        while (i < bounds.xpoints.length) {
          {
            if (bounds.xpoints(i) != 0) {
              bounds.xpoints(i) *= x
            }
          }
          {
            i += 1
            i - 1
          }
        }
      }
    }
    if (y != 1.0) {
      {
        var i: Int = 0
        while (i < bounds.ypoints.length) {
          {
            if (bounds.ypoints(i) != 0) {
              bounds.ypoints(i) *= y
            }
          }
          {
            i += 1
            i - 1
          }
        }
      }
    }
    bounds.translate(orig_translation.width, orig_translation.height)
    bounds.invalidate
  }

  /**
   * Diese Methode nimmt an, dass die {@link #bounds} rechteckig gemacht werden sollen.
   * @param x width
   * @param y height
   */
  private[comp] def assumeRect(x: Int, y: Int) {
    if (x != 0) {
      bounds.xpoints = new Array[Int](POLYGON_BUFFER_CAPACITY)
      bounds.xpoints(0) = getX
      bounds.xpoints(1) = getX + x
      bounds.xpoints(2) = getX + x
      bounds.xpoints(3) = getX
    }
    if (y != 0) {
      bounds.ypoints = new Array[Int](POLYGON_BUFFER_CAPACITY)
      bounds.ypoints(0) = getY
      bounds.ypoints(1) = getY
      bounds.ypoints(2) = getY + y
      bounds.ypoints(3) = getY + y
    }
    bounds.invalidate
  }

  /**
   * @return the bounds
   */
  def getBounds = bounds

  /**
   * Setzt die Grenzen des Steuerelements neu. Methode sollte noch nicht verwendet werden, da sie
   * den Mausinput durcheinander bringen kann.
   * @param bounds Das neue Polygonobjekt.
   */
  protected final def setBounds(bounds: Polygon) {
    this.bounds = bounds
    bounds.invalidate
  }

  /**
   * Erstellt eine neue Instanz eines Rechtecks. In diesen werden Position,
   * Breite und Höhe vereinfacht zusammengefasst. Mit jedem Aufruf dieser
   * Methode wird eine neue Instanz eines Rechtecks erstellt.
   *
   * @return Ein neues Rechteck mit der vereinfachten BoundingBox.
   */
  def getSimplifiedBounds = getBounds.getBounds

  /**
   * Berechnet das umgebende Rechteck eines auf dem Display darstellbaren
   * Textes in Pixeln.
   *
   * @param text
	 * Der Text, der benutzt werden soll.
   * @param f
	 * Die Schriftart.
   * @return Das umgebende Rechteck des Texts in Pixel.
   */
  def getTextBounds(text: String, f: Font): Dimension = {
    val affinetransform: AffineTransform = new AffineTransform
    val frc: FontRenderContext = new FontRenderContext(affinetransform, true, true)
    return new Dimension((f.getStringBounds(text, frc)).getWidth.asInstanceOf[Int], (f.getStringBounds(text, frc).getHeight).asInstanceOf[Int])
  }

  /**
   * Veranlasst das Steuerelement, wieder Input zu akzeptieren.
   */
  def acceptInput {
    status = ComponentStatus.NO_MOUSE
    acceptingInput = true
  }

  /**
   * Veranlasst das Steuerelement, keinen Input mehr zu akzeptieren.
   */
  def declineInput {
    status = ComponentStatus.NOT_AVAILABLE
    acceptingInput = false
  }

  /**
   * Funktioniert diese Funktion überhaupt?
   */
  def virtualClick {
    import scala.collection.JavaConversions._
    for (m <- mouseListeners) {
      m.mouseReleased(new MouseEvent(Main.getGameWindow, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis, 0, x + 1, y + y, x + 1, y + 1, 1, false, MouseEvent.BUTTON1))
    }
  }

  def remove(c: Component) {
    if (children.containsKey(c.getName) && children.containsValue(c)) {
      children.remove(c.getName)
    }
  }

  /**
   * Passt den Sichtbarkeitswert zurück.
   * @return
   */
  def isVisible = visible

  /**
   * Setzt die Sichtbarkeit des Steuerelements. Wenn die neue Sichtbarkeit false ist, dann
   * akzeptiert das Steuerelement keinen Input mehr. Wäre auch unlogisch, wenn ein unsichtbares
   * Steuerelement Input akzeptieren würde.
   * @param vvvvvv Der neue Sichtbarkeitswert.
   */
  def setVisible(vvvvvv: Boolean) {
    visible = vvvvvv
    if (vvvvvv) {
      acceptInput
    }
    else {
      declineInput
    }
  }

  /**
   *
   * Zeigt an, ob die Koordinaten des Steuerelements verändert werden können.
   * Kann mittels {@link #chain()} und {@link #unchain()} gesteuert werden.
   *
   * @return Ob die Koordinaten des Steuerelements verändert werden können.
   */
  def isChained = chained

  def getName = name

  /**
   * Setzt den Namen sowohl in der Hashtable der {@link #parent}-Component (sofern vorhanden),
   * als auch in diesem Objekt neu.
   * @param name Der neue Name der Component.
   */
  def setName(name: String) {
    this.name = name
  }

  /**
   * Gibt den Wert zurück, ob das Steuerelement Input akzeptiert.
   * @return
   */
  override def isAcceptingInput = acceptingInput

  def getBorder = border

  def setBorder(border: Border) {
    this.border = border
  }

  def updateGUI {
  }

  /**
   * Returns <code>true</code> if, and only if, the mouse is in the components' bounds.
   * @return <code>true</code> if, and only if, the mouse is in the components' bounds.
   */
  def isMouseFocused = mouseFocused

  /**
   * Triggers all registered listeners to be executed with a specified mouse event.
   * @param event The event to pass to the listeners.
   */
  def triggerListeners(event: MouseEvent) {
    import scala.collection.JavaConversions._
    for (listener <- mouseListeners) {
      listener.mouseReleased(event)
    }
  }

  /**
   * Calculates the center point of the component's bounding box.
   * For now, the simplified bounds will be used for calculation.
   * @return The center point of the component's simplified bounding box.
   */
  def center: Point = {
    val r = getSimplifiedBounds
    new Point(r.x + r.width / 2, r.y + r.height / 2)
  }

  def removeMouseWheelListener(mouseWheelListener: MouseWheelListener) = mouseWheelListeners.remove(mouseWheelListener)

  def addMouseWheelListener(mouseWheelListener: MouseWheelListener) = mouseWheelListeners.add(mouseWheelListener)



}

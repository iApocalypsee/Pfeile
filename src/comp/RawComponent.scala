package comp

import java.awt.{Polygon, Graphics2D}

/**
 *
 * @author Josip Palavra
 */
trait RawComponent {

  def drawFunction: (Graphics2D) => Unit
  def bounds: Polygon

}

class ComponentWrapper(val raw: RawComponent) {

  val component = new Component() {
    setBounds(raw.bounds)
    override def draw(g: Graphics2D): Unit = raw.drawFunction(g)
  }

}

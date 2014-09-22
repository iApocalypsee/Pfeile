package newent

import java.awt.Graphics2D

import comp.{Component, ComponentWrapper}
import gui.GameScreen
import newent.event.LocationChangedEvent

/**
 *
 * @author Josip Palavra
 */
class EntityComponentWrapper(val entity: EntityLike) extends ComponentWrapper(entity) {

  override val component = new Component {

    // This code is the same as the parent class's code...

    private lazy val f = entity.drawFunction
    override def draw(g: Graphics2D) = f(g)

    setBounds(entity.bounds)

    // But this is not the same...
    // Every time the entity changes its location, I need to adjust the GUI position for the entity aswell.
    entity.onLocationChanged += { l =>

      val screen = GameScreen.getInstance()

      // Find the end tile in the component wrappers, I need access to GUI functions.
      val opt = screen.getMap.tiles.find { _.tile eq l.end }

      val opt_guiChange = opt.map { w =>
        val tileRect = w.tile.bounds.getBounds
        setX(tileRect.x)
        setY(tileRect.y)
        1
      }

      if(opt_guiChange.isDefined) opt_guiChange.get
    }

    entity.onLocationChanged.call(LocationChangedEvent(entity.getGridX, entity.getGridY, entity.getGridX, entity.getGridY, entity))
  }
}

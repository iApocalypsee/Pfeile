package newent

import java.awt.Graphics2D

import comp.{Component, ComponentWrapper}
import general.Logger
import gui.GameScreen
import newent.event.LocationChangedEvent
import world.IsometricPolygonTile

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
      if(opt.isDefined) {
        val tile = opt.get
        // TODO Not nice implementation. I don't want the entity component be dependent on the tile bounds.
        setBounds(tile.component.getBounds)
        Logger.logMethodWithMessage(s"Entity bounds of ${l.entity} adjusted.")
      }
    }

    //entity.onLocationChanged.call(LocationChangedEvent(entity.getGridX, entity.getGridY, entity.getGridX, entity.getGridY, entity))
  }
}

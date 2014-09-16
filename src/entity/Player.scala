package entity

import java.awt.{Color, Graphics2D}
import comp.Component
import player.weapon.AttackEvent
import java.awt.Font

/**
 * Another Player class, but written in Scala.
 * @author Josip Palavra
 * @version 21.06.2014
 */
@deprecated
class Player(spawnX: Int, spawnY: Int, name: String) extends Component with MoveableEntity with Combatant {

  //super.gridX_=(spawnX)
  //super.gridY_=(spawnY)
  gridX = spawnX
  gridY = spawnY

  visionObject.put(this, 4)
  visionObject.updateVisionables()

  setName(name)

  override def attack(event: AttackEvent): Unit = {
    require(inventory.contains(event.getWeapon.getClass))

    val target = world.getTileAt(event.getTargetX, event.getTargetY).asInstanceOf[AttackContainer]
    target.registerAttack(event)
    inventory.removeItem(event.getWeapon.getClass)
    event.getWeapon.use()

  }


  override def updateGUI(): Unit = {
    super.updateGUI()
    setBounds(location.asInstanceOf[Component].getBounds)
  }

  override def draw(g: Graphics2D): Unit = {

    val t = location.asInstanceOf[Component].getBounds

    g.setColor(Player.placeholderColor)
    g.fillPolygon(t)
    
    g.setFont(new Font (Component.STD_FONT.getFontName(), Font.BOLD, 20))
    g.setColor(new Color (0, 0, 0, Player.placeholderColor.getAlpha()))
    g.drawString("P", (t.getBounds().x + 0.42 * t.getBounds().width).asInstanceOf[Int], (t.getBounds().y + 0.75 * t.getBounds().height).asInstanceOf[Int])
    g.setFont(Component.STD_FONT)

  }
}

@deprecated
object Player {
  private val placeholderColor = new Color(155, 25, 25, 200)
}

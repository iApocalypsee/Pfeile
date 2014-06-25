package entity

import java.awt.{Color, Graphics2D}

import comp.Component
import player.weapon.AttackEvent
import world.IBaseTile

/**
 * Another Player class, but written in Scala.
 * @author Josip Palavra
 * @version 21.06.2014
 */
class Player(spawnX: Int, spawnY: Int, name: String) extends Component with MoveableEntity with Combatant {

  //super.gridX_=(spawnX)
  //super.gridY_=(spawnY)
  gridX = spawnX
  gridY = spawnY

  visionObject.put(this, 4)
  visionObject.updateVisionables()

  setName(name)

  /**
   * Moves the entity to a specified tile.
   * @param tile The tile.
   */
  override def move(tile: IBaseTile): Unit = ???

  /**
   * Moves the unit relatively to the specified coordinates.
   * @param relx The relative x amount.
   * @param rely The relative y amount.
   */
  override def move(relx: Int, rely: Int): Unit = ???

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
    g.fillRect(t.getBounds.x, t.getBounds.y, t.getBounds.width, t.getBounds.height)

  }
}

object Player {
  private val placeholderColor = new Color(155, 25, 25, 200)
}

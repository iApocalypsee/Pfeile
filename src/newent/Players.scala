package newent

import java.awt.{Color, Point}

import comp.Component
import general.{Delegate, Main}
import newent.event.AttackEvent
import newent.pathfinding.AStarPathfinder
import player.weapon.{Weapon, AbstractArrow}
import world.{GrassTile, WorldLike}
import player.Life

/** Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint, name) with MoveableEntity with TeleportableEntity with
                                   InventoryEntity with LivingEntity with Combatant {

  // Game section.

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 2
  override val pathfinderLogic       = new AStarPathfinder(20, { t => true })
  override val life                  = new Life(100.0, 1.0, 100.0)

  val onTurnGet = Delegate.createZeroArity
  val onTurnEnd = Delegate.createZeroArity

  // GUI section.

  private val drawColor = new Color(255, 0, 0)

  /******* TEST CODE FOR ATTACK MECHANISM *********
  Main.getContext.onTurnEnd += { () =>
    world.terrain.tileAt(5, 5).take(AttackEvent(
    inventory.remove({ _.isInstanceOf[AbstractArrow] }).get.asInstanceOf[Weapon],
    tileLocation, world.terrain.tileAt(5, 5), this, 1.5))
    println("An arrow has been shot.")
  }

  world.terrain.tileAt(5, 5).onImpact += { e =>
    println(s"${e.weapon} has arrived at its destination!")
  }
  */

  // The draw function just draws a rectangle for now, I can add images later. Later!
  override def drawFunction = { g =>
    g.setColor(drawColor)
    g.fillPolygon(tileLocation.bounds)
  }
  override def bounds       = tileLocation.bounds

}

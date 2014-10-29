package newent

import general.Delegate
import general.Main.getGameWindow
import newent.pathfinding.AStarPathfinder
import player.Life
import world.WorldLike

import java.awt.{Color, Point}

/** Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint, name) with MoveableEntity with TeleportableEntity with
                                   InventoryEntity with LivingEntity with VisionEntity with Combatant {

  // Game section.

  private var _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 4)

  private def updateLocalVisionPoint(): Unit = {
    if(_localVisionPoint ne null) {
      _localVisionPoint.releaseVision()
    }
    _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 4)
  }

  override protected def setGridX(x: Int): Unit = {
    super.setGridX( x )
    updateLocalVisionPoint()
  }

  override protected def setGridY(y: Int): Unit = {
    super.setGridY( y )
    updateLocalVisionPoint()
  }

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 4
  override val pathfinderLogic       = new AStarPathfinder(20, { t => true })
  override val life                  = new Life(400.0, 2.2, 400.0)

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
    getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(getGameWindow.getScreenManager.getActiveScreen, gui.GameOverScreen.SCREEN_INDEX)
  }

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

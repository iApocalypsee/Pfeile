package newent

import java.awt.{Color, Graphics2D, Point}

import comp.Component
import general.Main.getGameWindow
import general.{Delegate, PfeileContext, Property}
import newent.pathfinding.AStarPathfinder
import player.Life
import world.WorldLike

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

  private var _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 5)

  private def updateLocalVisionPoint(): Unit = {
    if(_localVisionPoint ne null) {
      _localVisionPoint.releaseVision()
    }
    _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 5)
  }

  override protected def setGridPosition(x: Int, y: Int): Unit = {
    super.setGridPosition( x, y )
    updateLocalVisionPoint()
  }

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 4
  override val pathfinderLogic       = new AStarPathfinder(20, { t => true })
  override lazy val life             = new Life(Player.MAXIMUM_LIFE.get, Player.LIFE_REGENERATION.get, Player.MAXIMUM_LIFE.get)


  /** The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning. */
  override protected def initialTeam = new CommandTeam(this, "Team of " + this.name)

  /** the number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory */
  lazy val arrowNumberFreeSetUsable = Property.apply[java.lang.Integer](PfeileContext.ARROW_NUMBER_FREE_SET.get)

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
     general.Main.getContext.getTimeClock.stop()
     getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(getGameWindow.getScreenManager.getActiveScreen, gui.GameOverScreen.SCREEN_INDEX)
     //animation.SoundPool.stop_allMelodies()
     //animation.SoundPool.start_gameOverMelodie()
  }

  /** Called when turn is assigned to the player. */
  val onTurnGet = Delegate.createZeroArity

  /** Called when the player ended his turn. <p>
    * In terms of UI the equivalent to the delegate would be pressing the "End turn" button,
    * which essentially tells the program that the player has completed all of his actions now.
    */
  val onMovesCompleted = Delegate.createZeroArity

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

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new Component {

    private val drawColor = new Color(255, 0, 0)

    setSourceShape(tileLocation.component.getSourceShape)

    onLocationChanged += { e =>
      val endComponent = e.end.component
      setSourceShape(endComponent.getSourceShape)
      resetPosition()
      setX(endComponent.getX)
      setY(endComponent.getY)
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(drawColor)
      g.fill(getBounds)
    }
  }

}

object Player {

  /** the maximum life, which a player can have. It is initalized by PreWindowScreen (notice, that the value will be -1.0 before it) */
  lazy val MAXIMUM_LIFE = Property.apply[java.lang.Double](-1.0)

  /** the life regeneration of a player. It is initalized by PreWindowScreen (before that the value will be -1.0) */
  lazy val LIFE_REGENERATION = Property.apply[java.lang.Double](-1.0)

}

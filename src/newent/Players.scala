package newent

import java.awt.{Color, Graphics2D, Point}

import animation.SoundPool
import comp.Component
import general.Main.getGameWindow
import general.{Delegate, Main, PfeileContext, Property}
import gui.LifeUI
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{TileLike, WorldLike}

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
  /** The life of the player. Use the getter <code>getLife()</code> (defined in LivingEntity) */
  override protected lazy val life   = new Life(Player.MAXIMUM_LIFE.get, Player.LIFE_REGENERATION.get, Player.MAXIMUM_LIFE.get)
  private lazy val lifeUI            = new LifeUI(Main.getWindowWidth - 200, Main.getWindowHeight - 150, life)
  /** This draws the life (lifeBar and values) to the right-hand corner */
  def drawLifeUI(g: Graphics2D) = {
     lifeUI.draw(g)
  }

  /** The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning. */
  override protected def initialTeam = new CommandTeam(this, "Team of " + this.name)

  /** the number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory */
  lazy val arrowNumberFreeSetUsable = Property.apply[java.lang.Integer](PfeileContext.ARROW_NUMBER_FREE_SET.get)

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
     general.Main.getContext.getTimeClock.stop()
     getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(getGameWindow.getScreenManager.getActiveScreen, gui.GameOverScreen.SCREEN_INDEX)
     SoundPool.playLoop_gameOverMelodie(SoundPool.LOOP_CONTINUOUSLY)
  }

  /** Called when turn is assigned to the player. */
  val onTurnGet = Delegate.createZeroArity

  /** Called when the player ended his turn. <p>
    * In terms of UI the equivalent to the delegate would be pressing the "End turn" button,
    * which essentially tells the program that the player has completed all of his actions now.
    */
  val onMovesCompleted = Delegate.createZeroArity

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new Component {

    private val drawColor = new Color(190, 35, 255)

    setSourceShape(tileLocation.component.getSourceShape)

    onLocationChanged += { e =>
      tightenComponentToTile(e.end)
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(drawColor)
      g.fill(getBounds)
    }
  }

  def tightenComponentToTile(t: TileLike): Unit = {
    val endComponent = t.component
    component.setSourceShape(endComponent.getSourceShape)
    component.resetPosition()
    component.setX(endComponent.getX)
    component.setY(endComponent.getY)
  }

}

object Player {

  /** the maximum life, which a player can have. It is initalized by PreWindowScreen (notice, that the value will be -1.0 before it) */
  lazy val MAXIMUM_LIFE = Property.apply[java.lang.Double](-1.0)

  /** the life regeneration of a player. It is initalized by PreWindowScreen (before that the value will be -1.0) */
  lazy val LIFE_REGENERATION = Property.apply[java.lang.Double](-1.0)

}

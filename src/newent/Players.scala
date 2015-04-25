package newent

import java.awt.{Color, Graphics2D, Point}

import animation.SoundPool
import comp.Component
import general.Main.getGameWindow
import general.{Delegate, Main, PfeileContext, Property}
import gui.LifeUI
import gui.screen.GameOverScreen
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{TileLike, WorldLike}

/**
  * Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint, name) with CombatUnit with MoneyEarner {

  // Game section.

  private var _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 5)

  private def updateLocalVisionPoint(): Unit = {
    if (_localVisionPoint ne null) {
      _localVisionPoint.releaseVision()
    }
    _localVisionPoint = visionMap.grantVision(getGridX, getGridY, 5)
  }

  override protected def setGridPosition(x: Int, y: Int): Unit = {
    super.setGridPosition(x, y)
    updateLocalVisionPoint()
  }

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 4
  override val pathfinderLogic = new AStarPathfinder(20, { t => true })
  /** The life of the player. Use the getter <code>getLife()</code> (defined in LivingEntity) */
  override protected lazy val life = new Life(Player.maximumLife.get, Player.lifeRegeneration.get, Player.maximumLife.get)
  private lazy val lifeUI = new LifeUI(Main.getWindowWidth - 200, Main.getWindowHeight - 150, life)
  /** This draws the life (lifeBar and values) to the right-hand corner */
  def drawLifeUI(g: Graphics2D) = {
    lifeUI.draw(g)
  }

  /**
    * The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning.
    */
  override protected def initialTeam = new CommandTeam(this, "Team of "+this.name)

  /** the number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory */
  lazy val arrowNumberFreeSetUsable = Property.apply[java.lang.Integer](PfeileContext.ARROW_NUMBER_FREE_SET.get)

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
    general.Main.getContext.getTimeClock.stop()
    getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(GameOverScreen.SCREEN_INDEX)
    SoundPool.play_gameOverMelodie(SoundPool.LOOP_CONTINUOUSLY)
  }

  /** Called when turn is assigned to the player. */
  val onTurnGet = Delegate.createZeroArity

  /**
    * Called when the player ended his turn. <p>
    * In terms of UI the equivalent to the delegate would be pressing the "End turn" button,
    * which essentially tells the program that the player has completed all of his actions now.
    */
  val onMovesCompleted = Delegate.createZeroArity

  /**
    * The component that the representable object uses first. Method is called only once.
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

   override def toString: String = "Player: " + name

   /** The initial gold per turn amount that the earner gets. __Must not be below 0__.
     * <code> Defined as MoneyValues.START_MONEY</code>.*/
   override protected def initialMoneyPerTurn: Int = MoneyValues.moneyPerTurn()

   /** The initial amount of gold that the earner gets. __Must not be below 0__.
     * <code> Defined as MoneyValues.START_MONEY</code>.*/
   override protected def initialMoney: Int = MoneyValues.startMoney()
}

object Player {

  /** the maximum life, which a player can have. It is initialized by PreWindowScreen (notice, that the value will be -1.0 before it) */
  val maximumLife = Property(-1.0)

  /** the life regeneration of a player. It is initialized by PreWindowScreen (before that the value will be -1.0) */
  lazy val lifeRegeneration = Property(-1.0)

}

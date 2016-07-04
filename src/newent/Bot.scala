package newent

import java.awt.{Color, Graphics2D, Point}

import comp.Component
import general.property.{DoubleStaticProperty, IntStaticProperty}
import general.{Delegate, Main, PfeileContext}
import gui.LifeUI
import newent.pathfinding.{AStarPathfinder, Pathfinder}
import player.Life
import world.World

/**
  * The KI is not implemented as a intelligent KI. It is just a basic construct, that is similar to the Player class.
  */
class Bot(world: World, spawnPoint: Point, name: String)
    extends Entity(world, spawnPoint.x, spawnPoint.y, name) with CombatUnit with IntelligentArrowSelectionBot with MoneyEarner {

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
 *
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new Component {

    private val drawColor = new Color(255, 255, 0)

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

  /**
    * This draws the LifeBar of the Bot. Well, right now it's the same life bar like a Player, but in the futere,
    * we need a second one for the Bot.
    */
  def drawLifeUI(g: Graphics2D): Unit = {
    lifeUI.draw(g)
  }

  /** the life of the bot is introduced with standard values */
  override lazy val life = new Life(Bot.maximumLife.get, Bot.lifeRegeneration.get, Bot.maximumLife.get)

  private lazy val lifeUI = new LifeUI(Main.getWindowWidth - 200, Main.getWindowHeight - 150, Main.getContext.getActivePlayer.getLife)
  life.onDeath += { () =>
    general.Main.getContext.getTimeClock.stop()
    // TODO: setActiveScreen (GameWonScreen.SCREEN_INDEX)
  }

  /** Called when turn is assigned to the bot. */
  val onTurnGet = Delegate.createZeroArity

  /**
    * Called when the bots ended his turn. The bot has completed all its actions.
    */
  val onMovesCompleted = Delegate.createZeroArity

  /** The default movement points that the entity has. Here it is 4. */
  override def defaultMovementPoints: Int = 4

  /** This is just the same as the player has */
  override val pathfinderLogic: Pathfinder = new AStarPathfinder(20, { t => true })

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

  /** the number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory */
  val arrowNumberFreeSetUsable = new IntStaticProperty(PfeileContext.arrowNumberFreeSet.get)

  /**
    * the power of this KI: <p>
    * <b> Compare with <code>BotStrength</code>
    */
  lazy val Strength: BotStrength = BotStrength.Strength

   override def toString: String = "Bot: " + name

   /** The initial gold per turn amount that the earner gets. __Must not be below 0__.
     * <code> Defined as MoneyValues.START_MONEY</code>.*/
   override protected def initialMoneyPerTurn: Int = MoneyValues.moneyPerTurn()

   /** The initial amount of gold that the earner gets. __Must not be below 0__.
     * <code> Defined as MoneyValues.START_MONEY</code>. */
   override protected def initialMoney: Int = MoneyValues.startMoney()

  override val poison: Poison = new Poison(this, 0.1f)
}

object Bot {
  /** The standard maximum life of a bot */
  val maximumLife = new DoubleStaticProperty

  /** The standard life regeneration a bot has */
  val lifeRegeneration = new DoubleStaticProperty
}

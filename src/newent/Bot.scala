package newent

import java.awt.{Color, Graphics2D, Point}

import comp.Component
import general.{Delegate, Main, PfeileContext, Property}
import gui.LifeUI
import newent.pathfinding.{AStarPathfinder, Pathfinder}
import player.Life
import world.WorldLike

/**
  * The KI is not implemented as a intelligent KI. It is just a basic construct, that is similar to the Player class.
  */
class Bot(world: WorldLike, spawnPoint: Point, name: String)
    extends Entity(world, spawnPoint, name) with MoveableEntity with TeleportableEntity with InventoryEntity with LivingEntity with VisionEntity with Combatant with IntelligentArrowSelectionBot {

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
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
  override protected lazy val life = new Life(Bot.MAXIMUM_LIFE.get, Bot.LIFE_REGENERATION.get, Bot.MAXIMUM_LIFE.get)

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

  /**
    * The initial attribute object with which the entity begins recording its attributes.
    *
    * The method just gets called once to initialize an underlying field.
    *
    * The implementation is the same like in Player
    */
  override protected def initialAttribute = new Attributes {
    override protected def initialCurrent(initObject: Current) = initObject

    override protected def initialLasting(initObject: Lasting) = initObject
  }

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
  val arrowNumberFreeSetUsable = Property.apply[java.lang.Integer](PfeileContext.ARROW_NUMBER_FREE_SET.get)

  /**
    * the power of this KI: <p>
    * <b> Compare with <code>BotStrength</code>
    */
  lazy val Strength: BotStrength = BotStrength.Strength
}

object Bot {
  /** The standard maximum life of a bot */
  lazy val MAXIMUM_LIFE = Property.apply[java.lang.Double](-1.0)

  /** The standard life regeneration a bot has */
  lazy val LIFE_REGENERATION = Property.apply[java.lang.Double](-1.0)
}

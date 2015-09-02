package newent

import java.awt.{Color, Graphics2D, Point}

import animation.SoundPool
import comp.Component
import general.Main.getGameWindow
import general._
import general.property.IntStaticProperty
import gui.LifeUI
import gui.screen.GameOverScreen
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{CoastTile, SeaTile, TileLike, WorldLike}

/**
  * Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint.x, spawnpoint.y, name) with CombatUnit with MoneyEarner {


  //<editor-fold desc="Initialization code">

  hasSelfTracking = true

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
    general.Main.getContext.getTimeClock.stop()
    getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(GameOverScreen.SCREEN_INDEX)
    SoundPool.play_gameOverMelodie(SoundPool.LOOP_CONTINUOUSLY)
  }

  onLocationChanged += { e =>
    tightenComponentToTile(e.end)
  }

  //</editor-fold>

  /** The life of the player. Use the getter <code>getLife()</code> (defined in LivingEntity) */
  override lazy val life = new Life(Player.maximumLife.get, Player.lifeRegeneration.get, Player.maximumLife.get)
  private lazy val lifeUI = new LifeUI(Main.getWindowWidth - 200, Main.getWindowHeight - 150, life)
  /** This draws the life (lifeBar and values) to the right-hand corner */
  def drawLifeUI(g: Graphics2D) = {
    lifeUI.draw(g)
  }

  /** the number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory */
  lazy val arrowNumberFreeSetUsable = new IntStaticProperty(PfeileContext.arrowNumberFreeSet.get)

  val onTurnGet = Delegate.createZeroArity

  class PlayerComponent extends Component {

    private val drawColor = new Color(190, 35, 255)

    tightenComponentToTile(tileLocation)

    def tightenComponentToTile(t: TileLike): Unit = {
      val endComponent = t.component
      setSourceShape(endComponent.getSourceShape)
      setParent(t.component)
      setRelativeLocation(0, 0)
    }

    override def draw(g: Graphics2D): Unit = {
      g.setColor(drawColor)
      g.fill(getBounds)
    }
  }

  def tightenComponentToTile(t: TileLike): Unit = {
    component.asInstanceOf[PlayerComponent].tightenComponentToTile(t)
  }

  //<editor-fold desc="Other overrides">

  override val pathfinderLogic = new AStarPathfinder(20, {
    case sea: SeaTile => false
    case coast: CoastTile => false
    case anythingElse => true
  })

  override def defaultMovementPoints = 4

  override def toString: String = ScalaUtil.stringRepresentation(this, Map(
    "world" -> world,
    "name" -> name,
    "component" -> component,
    "money" -> purse.numericValue,
    "inventory" -> inventory
  ))

  override protected def startComponent = new PlayerComponent

  override protected def initialMoneyPerTurn: Int = MoneyValues.moneyPerTurn()

  override protected def initialMoney: Int = MoneyValues.startMoney()

  override protected def initialTeam = new CommandTeam(this, "Team of "+this.name)

  //</editor-fold>

}

object Player {

  /** the maximum life, which a player can have. It is initialized by PreWindowScreen (notice, that the value will be -1.0 before it) */
  val maximumLife = Property.apply[java.lang.Double]()

  /** the life regeneration of a player. It is initialized by PreWindowScreen (before that the value will be -1.0) */
  val lifeRegeneration = Property.apply[java.lang.Double]()

}

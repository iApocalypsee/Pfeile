package newent

import java.awt.geom.PathIterator
import java.awt.{Color, Graphics2D, Point}
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import animation.SoundPool
import com.sun.javafx.geom.Path2D
import comp.{Component, DisplayRepresentable}
import general._
import general.property.{DoubleStaticProperty, IntStaticProperty}
import gui.LifeUI
import gui.screen.{GameOverScreen, GameScreen}
import newent.MovableEntity.MovedEvent
import newent.pathfinding.AStarPathfinder
import player.Life
import world._

/**
  * Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: World, spawnpoint: Point, name: String) extends Entity(world, spawnpoint.x, spawnpoint.y, name) with CombatUnit with MoneyEarner with DisplayRepresentable {

  val onTurnGet = Delegate.createZeroArity

  // <editor-fold desc="Initialization code">

  hasSelfTracking = true

  // Delegate registration only valid after initialization of the actual life object.
  life.onDeath += { () =>
    Main.getContext.getTimeClock.stop()
    Main.getGameWindow.getScreenManager.getActiveScreen.onLeavingScreen(GameOverScreen.SCREEN_INDEX)
    SoundPool.play_gameOverMelodie(SoundPool.LOOP_CONTINUOUSLY)
  }

  onTurnGet += { () =>
    tightenComponentToTile(tileLocation)
  }

  // </editor-fold>

  // <editor-fold desc="Player loot fortune">

  /** The probability of finding good and more items in loots for this player. The value is set to 0 after each
    * turn. Increase the value by using <code>PotionOfFortune</code>.
    */
  private var fortuneStat = 0

  /**
    * Resets the fortune value to 0. Used at the end of each turn to stop the effects of <code>PotionOfFortune</code>
    */
  def resetFortuneStat(): Unit = {
    fortuneStat = 0
  }

  /** Changes the value (dosn't set!) of the fortune the player has. The higher this value, the more and the better
    * items the player will find. This value is reset after each turn to 0.
    *
    * @param value the value the luck of player should change.
    */
  def changeFortuneStat(value: Int): Unit = {
    fortuneStat += value
  }

  /** Returns the value of the fortune stat for this player. The value is set to 0 after each turn. Use
    * <code>changeFortuneStat(int value)</code> to change the stat.
    *
    * @return the value of the fortune stat
    */
  def getFortuneStat: Int = fortuneStat

  // </editor-fold>

  // <editor-fold desc="Life">

  /** The life of the player. Use the getter <code>getLife()</code> (defined in LivingEntity) */
  override lazy val life = new Life(Player.maximumLife.get, Player.lifeRegeneration.get, Player.maximumLife.get)
  private lazy val lifeUI = new LifeUI(Main.getWindowWidth - 200, Main.getWindowHeight - 150, life)
  /** This draws the life (lifeBar and values) to the right-hand corner */
  def drawLifeUI(g: Graphics2D) = {
    lifeUI.draw(g)
  }

  // </editor-fold>

  /**
    * The number of arrows the player an still use from this his/her selected usable <code> PfeileContext.ARROW_NUMBER_FREE_SET </code> inventory
    */
  lazy val arrowNumberFreeSetUsable = new IntStaticProperty(PfeileContext.arrowNumberFreeSet.get)

  def tightenComponentToTile(t: Tile): Unit = {
    component.tightenComponentToTile(t)
  }

  //<editor-fold desc="Other overrides">

  override val pathfinderLogic = new AStarPathfinder(20, {
    case sea: SeaTile => false
    case coast: CoastTile => false
    case anythingElse => true
  })

  override def defaultMovementPoints = 4

  override def toString: String = name

  override def component = super.component.asInstanceOf[PlayerComponent]

  override def component_=(a: Component) = {
    require(a.isInstanceOf[PlayerComponent])
    super.component_=(a)
  }

  override protected def startComponent = new PlayerComponent()

  override protected def initialMoneyPerTurn: Int = MoneyValues.moneyPerTurn()

  override protected def initialMoney: Int = MoneyValues.startMoney()

  override protected def initialTeam = new CommandTeam(this, "Team of " + this.name)

  override lazy val poison = new Poison(this, 0.1f)

  //</editor-fold>

  override protected def onTraverseSteps(traversedTiles: MovedEvent) = {

    /**
      * A GUI path for testing purposes. This path can be followed by the component at any time.
      */
    def testPath: PathIterator = {
      val path = new Path2D()
      path.moveTo(500, 500)
      path.lineToRel(200, 200)
      path.lineToRel(100, -500)
      path.curveToRel(-500, 100, 100, 100, -125, -150)
      GameObjectComponent.javaFxPathIteratorAsAwtPathIterator(path.getPathIterator(null))
    }

    component.followNewPath(traversedTiles.steps)
    
  }

  class PlayerComponent extends GameObjectComponent(this) {

    private val drawColor = new Color(190, 35, 255)

    setBackingScreen(GameScreen.getInstance)
    setName(Player.this.asInstanceOf[Entity].name)
    GameScreen.getInstance.forcePullFront(this)

    tightenComponentToTile(tileLocation)

    def tightenComponentToTile(t: Tile): Unit = {
      val endComponent = t.component
      setSourceShape(endComponent.getSourceShape)
      setParent(t.component)
      setX(endComponent.getX)
      setY(endComponent.getY)
    }

    override def draw(g: Graphics2D): Unit = {

      g.setColor(drawColor)
      g.fill(getBounds)

      // Hook for making the player image follow the path defined by its MovableEntity.path
      if(isFollowingPath()) keepFollowingPath()
      else tightenComponentToTile(tileLocation)

    }

  }

}

object Player {

  /** the maximum life, which a player can have. It is initialized by PreWindowScreen (notice, that the value will be -1.0 before it) */
  val maximumLife = new DoubleStaticProperty(-1.0)

  /** the life regeneration of a player. It is initialized by PreWindowScreen (before that the value will be -1.0) */
  val lifeRegeneration = new DoubleStaticProperty(-1.0)

}

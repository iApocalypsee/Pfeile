package general

import general.SeqOp._
import gui.screen.GameScreen
import newent.{CommandTeam, Entity, Player}
import player.item.WorldLootList
import player.weapon.AttackingCalculator
import world.WorldLike

/**
  * The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext(val values: PfeileContext.Values) extends Serializable {

  import general.PfeileContext._

  private var _activePlayer: Player = null
  private var _world: WorldLike = null
  private var _stopwatchThread: Thread = null

  private lazy val _lazyTimeObj: TimeClock = {
    val ret = new TimeClock
    _stopwatchThread = new Thread(ret)
    _stopwatchThread.setDaemon(true)
    _stopwatchThread.setPriority(Thread.MIN_PRIORITY + 2)

    onStartRunningTimeClock += { () =>
      _stopwatchThread.start()
    }
    ret
  }

  /**
    * Called when the world attribute has been changed. <p>
    * The argument is given as a tuple of two world objects: <p>
    * The first world is the old world, the second one is the new world.
    */
  private[this] val onWorldSwapped = Delegate.create[(WorldLike, WorldLike)]

  /**
    * Object that takes care of the turns.
    */
  // I know, this line is not readable. Shame on me.
  // What the long statement after the "=>" sign means is that it is collecting
  // every player from the EntityManager instance of the world and makes it available
  // to the TurnSystem instance.
  lazy val turnSystem = {

    LogFacility.log("Beginning initialization of turnSystem...")
    LogFacility.logCurrentStackTrace()

    // TODO Clear the initialization up a bit. Looks ugly.
    val turnSystem = new TurnSystem(() => for (player <- world.entities.entityList.filterType(classOf[Player])) yield player.belongsTo.team)

    // Notifies the entities in the world that a turn has been ended
    turnSystem.onTurnEnded += { team =>
      GameScreen.getInstance().lockUI()

      GameScreen.getInstance().releaseUI()

    }

    LogFacility.log("Appending crucial callback...")
    turnSystem.onTurnGet += {
      case playerTeam: CommandTeam =>
        LogFacility.log(s"Executed crucial callback with team=$playerTeam; player=${playerTeam.head}")
        require(playerTeam.head != null)
        activePlayer = playerTeam.head
        GameScreen.getInstance().getMoneyDisplay.retrieveDataFrom(playerTeam.head)

      case _ => ???
    }
    LogFacility.log("Crucial callback registered")

    LogFacility.log(s"Players in turn system: ${turnSystem.teams()}")

    turnSystem.onGlobalTurnCycleEnded += { () =>

      // looks weird, but with a static method I can't manage the thread
      new AttackingCalculator().arrowsFlying()

      // Notify the tiles first that the turn cycle has been completed.
      // Primarily, this for loop is written to update the arrow queues of the tiles.
      for (y <- 0 until _world.terrain.height) {
        for (x <- 0 until _world.terrain.width) {
          val tile = _world.terrain.tileAt(x, y)
          tile.updateQueues()
        }
      }
      // Then the entities.
      world.entities.entityList.foreach { _.onTurnCycleEnded() }

      values.turnCycleCount += 1
    }

    turnSystem
  }

  def getTurnSystem = turnSystem

  def activePlayer = _activePlayer
  def activePlayerOption = optReturn(activePlayer _)
  def activePlayer_=(p: Player): Unit = {
    _activePlayer = p
    entitySelection.selectedEntity = p
  }

  def getActivePlayer = activePlayer
  def setActivePlayer(p: Player) = activePlayer = p

  def world = _world
  def worldOption = optReturn(world _)
  def world_=(w: WorldLike): Unit = {
    val old = _world
    _world = w
    onWorldSwapped((old, w))
  }

  def getWorld = world
  def setWorld(w: WorldLike) = world = w

  def getTimeClock = _lazyTimeObj

  // need instance WorldLootList after TurnSystem initializing.
  private lazy val _worldLootList: WorldLootList = new WorldLootList(this)

  /**
    * It's the list of every loot, which is placed somewhere in the world. Use it to draw all loots, or to get a Loot.
    * @return the <code>WorldLootList</code> for the whole world.
    */
  def getWorldLootList = _worldLootList

  /** it is called, when TimeClock needs to start to run, this means at leaving LoadingWorldScreen */
  val onStartRunningTimeClock = Delegate.createZeroArity

  /**
    * Access to the current selection of entities.
    */
  lazy val entitySelection = EntitySelection

  object EntitySelection {

    private var _selectedEntity: Entity = activePlayer

    def selectedEntity = _selectedEntity
    def selectedEntity_=(x: Entity): Unit = {
      if (x == null) _selectedEntity = activePlayer
      else _selectedEntity = x
      //LogFacility.log(s"$selectedEntity selected", "Debug")
    }

    def resetSelection(): Unit = {
      selectedEntity = null
    }

  }

}

/**
  * It some values, that are very important for the whole game, not just for a single class. It is partly similar to
  * previous <code>general.Mechanics</code>.
  * Further Values are defined in MoneyEarner.scala: <code>MoneyValues</code>, <code>Player</code> or <code>Bot</code>.
  */
object PfeileContext {

  /**
   * Default width of the world.
   */
  val DefaultWorldSizeX = 28

  /**
   * Default height of the world.
   */
  val DefaultWorldSizeY = 25

  // TODO Not good, static variables which are not exchangeable.
  // It is better to have these variables in a special "Values" class, which we can
  // swap out to provide different values for different games.

  /**
    * this is the number of players (Humans and Bots) in the games.
    * Right now, its always 2.
    */
  val playerNumber = Property.apply[java.lang.Integer](2)

  /**
    * this is the damage multiplier. Every damage will be multiplied with this value.
    * It is set by PreWindowScreen.
    */
  val damageMultiplicator = Property.apply[java.lang.Float]()

  /** the number of arrows, which need to be set before the start of a round. Don't mix it up with <code>arrowNumberFreeSet</code>.*/
  val arrowNumberPreSet = Property.apply[java.lang.Integer]()

  /**
    * this is the number of arrow, which can be selected during the round. That's why they can be chosen for a
    * special purpose. Don't mix it up with <code>arrowNumberPreSet</code>
    */
  val arrowNumberFreeSet = Property.apply[java.lang.Integer]()

  /** this is the total number of arrows. It's <code>arrowNumberFreeSet + arrowNumberPreSet</code>*/
  def arrowNumberTotal = arrowNumberFreeSet.get + arrowNumberPreSet.get

  /**
    * The number of turns per round. A turn of a player ends when the user presses the endTurn-Button;
    * A turnCycle ends, when every players'/bots' turn is done; The round is over, then this value (turnsPerRound) is reached.
    * A new round allows the player to chose from some (3?) possible rewards and the player is able to select new
    * arrowsPreSet. Moreover, the player is allowed to use all arrowsFreeSet again (after selecting, of course).
    */
  val turnsPerRound = Property.apply[java.lang.Integer]()

  /**
    * How much the player is handicapped.
    * The value is percentage of support/deterioration.
    * Compare to <code>handicapAI</code>
    */
  val handicapPlayer = Property.apply[java.lang.Integer](0)

  /**
    * How much the bot is handicapped.
    * Basically, it is the percentage of support/deterioration.
    * Compare to <code>handicapPlayer</code>
    */
  val handicapAI = Property.apply[java.lang.Integer](0)

  /**
    * the size of the world in x direction, calculated in Tiles.
    */
  val worldSizeX = Property.apply[java.lang.Integer](DefaultWorldSizeX)

  /**
    * the size of the world in y-direction; in Tiles
    */
  val worldSizeY = Property.apply[java.lang.Integer](DefaultWorldSizeY)

  /**
    * Class holding all value information about the game. <p>
    * These values exclude e.g. the world, the active player, the turn player,... such things. </p>
    * The values for the turn can be found in TimeClock (<code>Main.getContext().getTimeClock()</code>)
    */
  // TODO Add XML document for default/recommended values.
  final class Values extends Serializable {

    private var _turnCycleCount = 0

    /** Describes how many turn cycles have been completed. */
    def turnCycleCount = _turnCycleCount
    private[PfeileContext] def turnCycleCount_=(a: Int) = _turnCycleCount = a
    /** Describes how many turn cycles have been completed. */
    def getTurnCycleCount = turnCycleCount
  }

  // Returns an option instead of the direct reference value: None instead of null, Some(obj) instead of obj
  private def optReturn[A <: AnyRef](f: () => A): Option[A] = {
    val f_result = f()
    if (f_result eq null) None
    else Some(f_result)
  }

}

package general

import general.property.{FloatStaticProperty, IntStaticProperty}
import gui.screen.GameScreen
import newent.{CommandTeam, Entity, Player}
import player.item.WorldLootList
import world.World

/**
  * The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext(val values: PfeileContext.Values) extends Serializable {

  //<editor-fold desc="Events">

  /**
    * Called when the world attribute has been changed.
    */
  val onWorldSwapped = Delegate.create[SwapEvent[World]]

  /**
    * Called, when TimeClock needs to start to run; this means at leaving LoadingWorldScreen.
    * TODO Should be just a callback and not a separate event (see above: "...this means at leaving LoadingWorldScreen")
    */
  val onStartRunningTimeClock = Delegate.createZeroArity

  /**
    * Called when the active player changes.
    */
  val onActivePlayerChanged = Delegate.create[SwapEvent[Player]]

  //</editor-fold>

  //<editor-fold desc="Game data">

  /**
    * The player that is currently in control of the computer running this game.
    */
  private var _activePlayer: Player = null

  /**
    * The world which is currently loaded in.
    */
  private var _world: World = null

  //</editor-fold>

  // TODO: Thread belongs to stopwatch, should be moved to class Stopwatch/Timeclock/whathaveyou
  private var _stopwatchThread: Thread = null
  private lazy val _lazyTimeObj: TimeClock = {
    val ret = new TimeClock(this)
    _stopwatchThread = new Thread(ret)
    _stopwatchThread.setDaemon(true)
    _stopwatchThread.setPriority(Thread.MIN_PRIORITY + 2)

    onStartRunningTimeClock += { () =>
      _stopwatchThread.start()
    }
    ret
  }

  /**
    * Object that takes care of the turns.
    */
  lazy val turnSystem = {

    val turnSystemTeamList = () => {
      val players = world.entities.entityList.collect({ case p: Player => p })
      players.map(player => player.belongsTo.team)
    }

    val turnSystem = new TurnSystem(turnSystemTeamList)

    turnSystem.onTurnGet += { team =>
      team match {
        case playerTeam: CommandTeam =>
          require(playerTeam.head != null)
          activePlayer = playerTeam.head
          GameScreen.getInstance().getMoneyDisplay.retrieveDataFrom(playerTeam.head)
        case _ => ???
      }
    }

    turnSystem.onGlobalTurnCycleEnded += { () =>
      // Notify the tiles first that the turn cycle has been completed.
      // Primarily, this for loop is written to update the arrow queues of the tiles.
      for(tile <- world.terrain.tiles) {
        tile.updateQueues()
      }

      // Then the entities.
      for(entity <- world.entities.entityList) {
        entity.onTurnCycleEnded()
      }

      values.turnCycleCount += 1
    }

    turnSystem
  }

  def getTurnSystem = turnSystem

  def activePlayer = _activePlayer
  def activePlayerOption = Option(world)
  def activePlayer_=(p: Player): Unit = {
    _activePlayer = p
    entitySelection.selectedEntity = p
  }

  def getActivePlayer = activePlayer
  def setActivePlayer(p: Player) = activePlayer = p

  def world = _world
  def worldOption = Option(world)
  def world_=(w: World): Unit = {
    val old = _world
    _world = w
    onWorldSwapped(SwapEvent(old, w))
  }

  def getWorld = world
  def setWorld(w: World) = world = w

  def getTimeClock = _lazyTimeObj

  // need instance WorldLootList after TurnSystem initializing.
  private lazy val _worldLootList = new WorldLootList(this)

  /**
    * It's the list of every loot, which is placed somewhere in the world. Use it to draw all loots, or to get a Loot.
 *
    * @return the <code>WorldLootList</code> for the whole world.
    */
  def getWorldLootList = _worldLootList


  /**
    * Access to the current selection of entities.
    */
  def entitySelection = EntitySelection

  object EntitySelection {

    private var _selectedEntity: Entity = activePlayer

    def selectedEntity = _selectedEntity
    def selectedEntity_=(x: Entity): Unit = {
      if (x == null) _selectedEntity = activePlayer
      else _selectedEntity = x
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
  val playerNumber = new IntStaticProperty(2)

  /**
    * this is the damage multiplier. Every damage will be multiplied with this value.
    * It is set by PreWindowScreen.
    */
  val damageMultiplicator = new FloatStaticProperty

  /** the number of arrows, which need to be set before the start of a round. Don't mix it up with <code>arrowNumberFreeSet</code>.*/
  val arrowNumberPreSet = new IntStaticProperty

  /**
    * this is the number of arrow, which can be selected during the round. That's why they can be chosen for a
    * special purpose. Don't mix it up with <code>arrowNumberPreSet</code>
    */
  val arrowNumberFreeSet = new IntStaticProperty

  /** this is the total number of arrows. It's <code>arrowNumberFreeSet + arrowNumberPreSet</code>*/
  def arrowNumberTotal = arrowNumberFreeSet.get + arrowNumberPreSet.get

  /**
    * The number of turns per round. A turn of a player ends when the user presses the endTurn-Button;
    * A turnCycle ends, when every players'/bots' turn is done; The round is over, then this value (turnsPerRound) is reached.
    * A new round allows the player to chose from some (3?) possible rewards and the player is able to select new
    * arrowsPreSet. Moreover, the player is allowed to use all arrowsFreeSet again (after selecting, of course).
    */
  val turnsPerRound = new IntStaticProperty

  /**
    * How much the player is handicapped.
    * The value is percentage of support/deterioration.
    * Compare to <code>handicapAI</code>
    */
  val handicapPlayer = new IntStaticProperty(0)

  /**
    * How much the bot is handicapped.
    * Basically, it is the percentage of support/deterioration.
    * Compare to <code>handicapPlayer</code>
    */
  val handicapAI = new IntStaticProperty(0)

  /**
    * the size of the world in x direction, calculated in Tiles.
    */
  val worldSizeX = new IntStaticProperty(DefaultWorldSizeX)

  /**
    * the size of the world in y-direction; in Tiles
    */
  val worldSizeY = new IntStaticProperty(DefaultWorldSizeY)

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

}

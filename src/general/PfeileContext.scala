package general

import general.SeqOp._
import gui.GameScreen
import newent.Player
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
      LogFacility.log("Stopwatch thread has been started.", "Debug")
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

    // TODO Clear the initialization up a bit. Looks ugly.
    val turnSystem = new TurnSystem(() => world.entities.entityList.filterType[Player])

    // Notifies the entities in the world that a turn has been ended
    turnSystem.onTurnEnded += { _ =>
      GameScreen.getInstance().lockUI()

      // looks weird, but with a static method I can't manage the thread
      new AttackingCalculator().arrowsFlying()

      GameScreen.getInstance().releaseUI()
    }

    turnSystem.onTurnGet += { p =>
      activePlayer = p
    }

    LogFacility.log(s"Players in turn system: ${turnSystem.playerList.apply()}")

    turnSystem.onGlobalTurnCycleEnded += { () =>
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
  }

  def getActivePlayer = _activePlayer
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

  /** it is called, when TimeClock needs to start to run, this means at leaving LoadingWorldScreen */
  val onStartRunningTimeClock = Delegate.createZeroArity
}

/**
  * It some values, that are very important for the whole game, not just for a single class. It is partly similar to
  * previous <code>general.Mechanics</code>
  */
object PfeileContext {

  // TODO Not good, static variables which are not exchangeable.
  // It is better to have these variables in a special "Values" class, which we can
  // swap out to provide different values for different games.

  /**
    * this is the number of players (Humans and Bots) in the games.
    * Right now, its always 2.
    */
  val PLAYER_NUMBER = Property.apply[java.lang.Byte](2 toByte)

  /**
    * this is the damage multiplier. Every damage will be multiplied with this value.
    * It is set by PreWindowScreen.
    */
  val DAMAGE_MULTI = Property.apply[java.lang.Float](-1f)

  /** the number of arrows, which need to be set before the start of a round. Don't mix it up with <code>ARROW_NUMBER_FREE_SET</code>.*/
  val ARROW_NUMBER_PRE_SET = Property.apply[java.lang.Integer](-1)

  /**
    * this is the number of arrow, which can be selected during the round. That's why they can be chosen for a
    * special purpose. Don't mix it up with <code>ARROW_NUMBER_PRE_SET</code>
    */
  val ARROW_NUMBER_FREE_SET = Property.apply[java.lang.Integer](-1)

  /** this is the total number of arrows. It's <code>ARROW_NUMBER_FREE_SET + ARROW_NUMBER_PRE_SET </code>*/
  def ARROW_NUMBER_TOTAL = ARROW_NUMBER_FREE_SET.get + ARROW_NUMBER_PRE_SET.get

  /**
    * The number of turns per round. A turn of a player ends when the user presses the endTurn-Button;
    * A turnCycle ends, when every players'/bots' turn is done; The round is over, then this value (turnsPerRound) is reached.
    * A new round allows the player to chose from some (3?) possible rewards and the player is able to select new
    * arrowsPreSet. Moreover, the player is allowed to use all arrowsFreeSet again (after selecting, of course).
    */
  val TURNS_PER_ROUND = Property.apply[java.lang.Integer](-1)

  /**
    * the handicap of the player.
    * The value is percentage of support/deterioration saved as java Byte, because their is no use in wasting free memory.
    * Compare to <code>HANDICAP_KI</code>
    */
  val HANDICAP_PLAYER = Property.apply[java.lang.Byte](0 toByte)

  /**
    * the handicap value of the bot.
    * Basically, it is the percentage of support/deterioration saved as java Byte, because their is no use in wasting free memory.
    * Compare to <code>HANDICAP_PLAYER</code>
    */
  val HANDICAP_KI = Property.apply[java.lang.Byte](0 toByte)

  /**
    * the size of the world in x direction, calculated in Tiles.
    * The default value is 28.
    */
  val WORLD_SIZE_X = Property.apply[java.lang.Integer](28)

  /**
    * the size of the world in y-direction; in Tiles
    * the default value is 25.
    */
  val WORLD_SIZE_Y = Property.apply[java.lang.Integer](25)

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

package general

import gui.GameScreen
import newent.{EntityLike, Player}
import player.weapon.AttackingCalculator
import world.WorldLike


/** The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext(val values: PfeileContext.Values) extends Serializable {

  import PfeileContext._

  private var _activePlayer   : Player    = null
  private var _world          : WorldLike = null
  private var _timeObj        : TimeClock = null
  private var _stopwatchThread: Thread    = null

  /** Called when the world attribute has been changed. <p>
    * The argument is given as a tuple of two world objects: <p>
    * The first world is the old world, the second one is the new world.
    */
  private[this] val onWorldSwapped = Delegate.create[(WorldLike, WorldLike)]

  val playerList = PlayerList

  /** Called when the turn has been ended. */
  val onTurnEnd = Delegate.createZeroArity

  // Notifies the entities in the world that a turn has been ended
  onTurnEnd += { () =>
     GameScreen.getInstance().lockUI()

     val attacks = new AttackingCalculator
     attacks.arrowsFlying()

     PlayerList.++()

     GameScreen.getInstance().releaseUI()
  }

  PlayerList.onTurnCycleGlobalEnded += { () =>
    // Notify the tiles first that the turn cycle has been completed.
    for(y <- 0 until _world.terrain.height) {
      for(x <- 0 until _world.terrain.width) {
        val tile = _world.terrain.tileAt(x, y)
        tile.updateQueues()
      }
    }
    // Then the entities.
    world.entities.entityList.foreach { _.onTurnCycleEnded() }
  }

  def activePlayer = _activePlayer
  def activePlayerOption = optReturn(activePlayer _)
  def activePlayer_=(p: Player): Unit = {
    _activePlayer = p
    // Time object resetting is done when the active player is notified about
    // that he is assigned the turn.
    // Only at these moments the time should reset/start.
    _activePlayer.onTurnGet += { () =>
      _timeObj.reset()
      _timeObj.start()
    }
    _activePlayer.onMovesCompleted += { () =>
      _timeObj.stop()
    }
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

  def getTimeClock = _timeObj

  /**
   * Initialiert die TimeClock
   */
  def initTimeClock () : Unit = {
     _timeObj = new TimeClock()
     _stopwatchThread = new Thread(_timeObj)
     _stopwatchThread.setDaemon(true)
     _stopwatchThread.setPriority(Thread.MIN_PRIORITY + 1)

     val sm = Main.getGameWindow.getScreenManager
     _timeObj.onTimeOver.register { () =>

        if (sm.getActiveScreenIndex == gui.GameScreen.SCREEN_INDEX) {
           onTurnEnd.call()
        } else if (sm.getActiveScreenIndex == gui.ArrowSelectionScreen.SCREEN_INDEX) {
           sm.setActiveScreen(gui.GameScreen.SCREEN_INDEX)
           onTurnEnd.call()
        } else if (sm.getActiveScreenIndex == gui.AimSelectionScreen.SCREEN_INDEX) {
           sm.setActiveScreen(gui.GameScreen.SCREEN_INDEX)
           onTurnEnd.call()
        } else
           throw new java.lang.RuntimeException ("Time is out. Getting to GameScreen of the activePlayer, the active Screen is neither GameScreen nor Aim- or ArrowSelectionScreen. ActiveScreen: " + sm.getActiveScreen.getName)
     }

     GameScreen.getInstance().onScreenEnter += { () =>
        if (!_stopwatchThread.isAlive)
           _stopwatchThread.start()
     }
  }

  /** Manages the list of players and the turn sequence of the players.
    *
    */
  object PlayerList {

    import scala.collection.mutable

    /** All players, collected in this buffer. */
    private var playerBuffer: mutable.ArrayBuffer[Player] = null
    private var _turnPlayer: Player = null

    /** Called when every player in a cycle has drawn its moves.
      * So essentially it is called when the last player in the player list
      * ends his turn. At that moment it is clear that every player in a cycle
      * has moved at least somehow. <p>
      * Do not confuse this delegate with the [[EntityLike.onTurnCycleEnded]] delegate in the [[EntityLike]]
      * trait.
      */
    val onTurnCycleGlobalEnded = Delegate.createZeroArity
    onTurnCycleGlobalEnded += { () =>
      // Every entity is going to be notified about that a turn cycle has been completed.
      // This has to be done, so that entities have a chance to pull/calculate new data.
      // Example would be the moving mechanism of the entity.
      world.entities.entityList foreach { e => e.onTurnCycleEnded() }
      values.turnCycleCount += 1
      LogFacility.log(s"Turn cycle no.${values.turnCycleCount} completed.")
    }

    /** Function for the entity managers. <p>
      *
      * The function recognizes player objects and adds them to the player list.
      * This function is injected into the entity manager of the world.
      */
    private val playerRecognitionRoutine = { (e: EntityLike) =>
      e match {
        case player: Player =>
          LogFacility.log(s"Recognized player: $player", "Debug")
          playerBuffer += player
        case _ =>
      }
    }

    /** The routine that is going to be executed every time the world attribute
      * changes in the PfeileContext outer class.
      */
    private val worldSwapRoutine = { (tuple: (WorldLike, WorldLike)) =>

      // Inject the player recognition routine to the entity manager first.
      val (oldWorld, newWorld) = tuple
      // There is a possibility that the old world is equal to null, so just to prevent it.
      if(oldWorld ne null) oldWorld.entities.onEntityRegistered -= playerRecognitionRoutine
      newWorld.entities.onEntityRegistered += playerRecognitionRoutine

      // Collect all players from the entity list and pack them into the buffer, so that
      // I can easily adjust the turn numbers.
      playerBuffer = mutable.ArrayBuffer[Player]()
      playerBuffer ++= entityManagerPlayers
      if(playerBuffer.nonEmpty) turnPlayer = playerBuffer(0)

    }

    onWorldSwapped += worldSwapRoutine

    private def entityManagerPlayers = world.entities.entityList.filter({ _.isInstanceOf[Player] }).asInstanceOf[Seq[Player]]

    def turnPlayer = _turnPlayer
    def turnPlayer_=(p: Player) = _turnPlayer = p
    def getTurnPlayer = _turnPlayer
    def setTurnPlayer(p: Player) = _turnPlayer = p

    def players = playerBuffer.toList

    /** Increments the turn to the next one. <p>
      * Essentially with this call, the player list gives the turn to the next one in the list.
      * If the player is the last one in the list, the turn is given back to the head of the list
      * and the [[onTurnCycleGlobalEnded]] delegate is called.
      */
    def ++(): Unit = {
      assume(playerBuffer ne null, "Player buffer is null.")
      assume(turnPlayer ne null, "Turn player is null.")

      val index = playerBuffer.indexOf(turnPlayer)
      // Still I have to notify the old turn player about that he ended his turn.
      turnPlayer.onMovesCompleted()
      // Assign the turn to the next player according to the rules in the documentation.
      turnPlayer = if(index + 1 >= playerBuffer.size) {
        // Call the "complete cycle" delegate since everyone in the cycle has performed some action.
        onTurnCycleGlobalEnded()
        // Well, I have to start from the beginning, don't I?
        playerBuffer(0)
      } else playerBuffer(index + 1)
      // Notify the player about that turn has been assigned to him.
      turnPlayer.onTurnGet()
    }

    /** Does the same as [[PlayerList.++()]] */
    def handTurnToNext(): Unit = this.++( )

  }
}

object PfeileContext {

  /** Class holding all value information about the game. <p>
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
    if(f_result eq null) None
    else Some(f_result)
  }
}

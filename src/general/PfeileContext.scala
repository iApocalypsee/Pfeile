package general

import gui.GameScreen
import newent.{CommandTeam, Team, EntityLike, Player}
import player.weapon.AttackingCalculator
import world.WorldLike


/** The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext(val values: PfeileContext.Values) extends Serializable {

  import general.PfeileContext._

  private var _activePlayer: Player = null
  private var _world: WorldLike = null
  private var _timeObj: TimeClock = null
  private var _stopwatchThread: Thread = null

  // Extract the call to other locations, does not fit inside here.
  // TimeClock is a GUI component, there is nothing to do here for TimeClock.
  initTimeClock()

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

     // looks weird, but with a static method I can't manage the thread
     new AttackingCalculator().arrowsFlying()

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

  onWorldSwapped += { _ =>
    println("World has been swapped.")
  }

  def getWorld = world
  def setWorld(w: WorldLike) = world = w

  def getTimeClock = _timeObj

  /** it is called, when TimeClock needs to start to run, this means at leaving LoadingWorldScreen */
  lazy val onStartRunningTimeClock = Delegate.createZeroArityOneCall

  /**
   * Initialiert die TimeClock
   */
  def initTimeClock(): Unit = {
    _timeObj = new TimeClock()
    _stopwatchThread = new Thread(_timeObj)
    _stopwatchThread.setDaemon(true)
    _stopwatchThread.setPriority(Thread.MIN_PRIORITY + 2)

     onStartRunningTimeClock += { () =>
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
      LogFacility.log(s"Turn cycle no.${values.turnCycleCount } completed.")
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
          // TODO This implementation is somewhat not acceptable, the Team object is saved nowhere.
          player.join(new CommandTeam(player, "Team of " + player.name))
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

/** It some values, that are very important for the whole game, not just for a single class. It is partly similar to
  * previous <code>general.Mechanics</code>*/
object PfeileContext {

  // TODO Not good, static variables which are not exchangeable.
  // It is better to have these variables in a special "Values" class, which we can
  // swap out to provide different values for different games.

   /** this is the number of players (Humans and Bots) in the games.
     * Right now, its always 2.
     */
   val PLAYER_NUMBER = Property.apply[java.lang.Byte](2 toByte)

   /** this is the damage multiplier. Every damage will be multiplied with this value.
     * It is set by PreWindowScreen.  */
   val DAMAGE_MULTI = Property.apply[java.lang.Float](-1f)

   /** the number of arrows, which need to be set before the start of a round. Don't mix it up with <code>ARROW_NUMBER_FREE_SET</code>.*/
   val ARROW_NUMBER_PRE_SET = Property.apply[java.lang.Integer](-1)

   /** this is the number of arrow, which can be selected during the round. That's why they can be chosen for a
     * special purpose. Don't mix it up with <code>ARROW_NUMBER_PRE_SET</code> */
   val ARROW_NUMBER_FREE_SET = Property.apply[java.lang.Integer](-1)

   /** this is the total number of arrows. It's <code>ARROW_NUMBER_FREE_SET + ARROW_NUMBER_PRE_SET </code>*/
   def ARROW_NUMBER_TOTAL = ARROW_NUMBER_FREE_SET.get + ARROW_NUMBER_PRE_SET.get

   /** The number of turns per round. A turn of a player ends when the user presses the endTurn-Button;
     * A turnCycle ends, when every players'/bots' turn is done; The round is over, then this value (turnsPerRound) is reached.
     * A new round allows the player to chose from some (3?) possible rewards and the player is able to select new
     * arrowsPreSet. Moreover, the player is allowed to use all arrowsFreeSet again (after selecting, of course).*/
   val TURNS_PER_ROUND = Property.apply[java.lang.Integer](-1)

   /** the handicap of the player.
     * The value is percentage of support/deterioration saved as java Byte, because their is no use in wasting free memory.
     * Compare to <code>HANDICAP_KI</code>
     */
   val HANDICAP_PLAYER = Property.apply[java.lang.Byte](0 toByte)

   /** the handicap value of the bot.
    * Basically, it is the percentage of support/deterioration saved as java Byte, because their is no use in wasting free memory.
    * Compare to <code>HANDICAP_PLAYER</code>
    * */
   val HANDICAP_KI = Property.apply[java.lang.Byte](0 toByte)

   /** the size of the world in x direction, calculated in Tiles.
     * The default value is 28. */
   val WORLD_SIZE_X = Property.apply[java.lang.Integer](28)

   /** the size of the world in y-direction; in Tiles
     * the default value is 25. */
   val WORLD_SIZE_Y = Property.apply[java.lang.Integer](25)

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

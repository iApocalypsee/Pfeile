package general

import newent.Player
import world.WorldLike

import scala.concurrent.duration._

/** The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext(val values: PfeileContext.Values) extends Serializable {

  import PfeileContext._

  private var _activePlayer   : Player    = null
  private var _turnPlayer     : Player    = null
  private var _world          : WorldLike = null
  private var _timeObj: TimeClock = null
  private var _stopwatchThread: Thread    = null


  /** Called when the turn has been ended. */
  val onTurnEnd = Delegate.createZeroArity

  // Notifies the entities in the world that a turn has been ended
  onTurnEnd += { () =>
    import scala.concurrent.ExecutionContext.Implicits.global

    _turnPlayer.onTurnEnd.call()

    _world.entities.entityList.foreach { _.turnover() }

    // Notifying the next player about that it is his turn now.
    val playerList = players
    val indexOfCurrent = playerList.indexOf(_activePlayer)
    val nextIndex = if(indexOfCurrent + 1 >= playerList.size) 0 else indexOfCurrent + 1
    _turnPlayer = playerList(nextIndex)
    _turnPlayer.onTurnGet.callAsync()
  }

  def activePlayer = _activePlayer
  def activePlayerOption = optReturn(activePlayer _)
  def activePlayer_=(p: Player): Unit = {
      _activePlayer = p
      _activePlayer.onTurnGet += { () =>
      _timeObj.reset()
      _timeObj.start()
    }
    _activePlayer.onTurnEnd += { () =>
      _timeObj.stop()
    }
  }

  def getActivePlayer = _activePlayer
  def setActivePlayer(p: Player) = activePlayer = p

  def turnPlayer = _turnPlayer
  def turnPlayerOption = optReturn(turnPlayer _)
  def turnPlayer_=(a: Player) = _turnPlayer = a

  def getTurnPlayer = turnPlayer
  def setTurnPlayer(a: Player) = turnPlayer = a

  def world = _world
  def worldOption = optReturn(world _)
  def world_=(w: WorldLike): Unit = _world = w

  def getWorld = world
  def setWorld(w: WorldLike) = world = w

  def getTimeClock = _timeObj

  /**
   * Initialiert die TimeClock
   */
  def initTimeClock () : Unit = {
     _timeObj = new TimeClock
     _stopwatchThread = new Thread(_timeObj)
     _stopwatchThread.setDaemon(true)
     _stopwatchThread.setPriority(Thread.MIN_PRIORITY + 1)
     _stopwatchThread.start()
  }

  private def players = world.entities.entityList.filter({ _.isInstanceOf[Player] }).asInstanceOf[Seq[Player]]
}

object PfeileContext {

  /** Class holding all value information about the game. <p>
    * These values exclude e.g. the world, the active player, the turn player,... such things. </p>
    * The values for the turn can be found in TimeClock (<code>Main.getContext().getTimeClock()</code>)
    */
  // TODO Add XML document for default/recommended values.
  class Values extends Serializable {

  }

  // Returns an option instead of the direct reference value: None instead of null, Some(obj) instead of obj
  private def optReturn[A <: AnyRef](f: () => A): Option[A] = {
    val f_result = f()
    if(f_result eq null) None
    else Some(f_result)
  }
}

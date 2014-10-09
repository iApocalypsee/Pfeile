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

  private var _activePlayer : Player    = null
  private var _turnPlayer   : Player    = null
  private var _world        : WorldLike = null

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
      Main.timeObj.reset()
      Main.timeObj.start()
    }
    _activePlayer.onTurnEnd += { () =>
      Main.timeObj.stop()
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

  private def players = world.entities.entityList.filter({ _.isInstanceOf[Player] }).asInstanceOf[Seq[Player]]
}

object PfeileContext {

  /** Class holding all value information about the game. <p>
    * These values exclude e.g. the world, the active player, the turn player,... such things.
    *
    * @param _turnTime The amount of time that the turn player has to make his moves.
    *                  No time per move, time per turn.
    */
  // TODO Add XML document for default/recommended values.
  class Values(private var _turnTime: FiniteDuration) extends Serializable {

    /** Returns the time in which a player is allowed to make moves. <p>
      *
      * If the underlying turn time variable is null, this method returns Duration.Inf,
      * otherwise it returns the underlying turn time variable directly. <p>
      *
      * For direct time calculation, use time conversion methods provided with the Duration object:
      * <code>toMillis, toNanos, toMinutes, toSeconds</code>
      */
    def turnTime: Duration = {
      if(_turnTime eq null) Duration.Inf
      else _turnTime
    }

    /** Sets the new turn time.
      *
      * The new value may be <code>null</code>. In case of <code>null</code> the turn time
      * defaults to infinite time.
      */
    def turnTime_=(a: FiniteDuration) = _turnTime = a

    /** Returns true if the turn time is infinite. */
    def isTurnTimeInfinite = _turnTime eq null

  }

  // Returns an option instead of the direct reference value: None instead of null, Some(obj) instead of obj
  private def optReturn[A <: AnyRef](f: () => A): Option[A] = {
    val f_result = f()
    if(f_result eq null) None
    else Some(f_result)
  }

}

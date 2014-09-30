package general

import newent.Player
import world.WorldLike

/** The game mechanics of "Pfeile" in its own class. <p>
  *
  * The best place to keep all data is in a central class which is not polluting any other classes.
  */
class PfeileContext extends Serializable {

  private var _activePlayer : Option[Player]    = None
  private var _world        : Option[WorldLike] = None

  /** The object in which all at least half-way important values are stored. */
  val values = new Values

  /** The active player. */
  def activePlayer = _activePlayer

  /** Sets the active player to a new instance. <p>
    *
    * Very useful in co-op or hot-seat games to change the active player. <p>
    *
    * If the option itself or the underlying opt.get is null, the method assumes that the caller wants to set
    * the active player to [[scala.None]] instead of null.
    *
    * @param opt The active player to set to. May be null, but not recommended.
    */
  def activePlayer_=(opt: Option[Player]): Unit = {
    if(opt eq null) _activePlayer = None
    val null_opt = opt map { a => a eq null }
    if(null_opt.isDefined && null_opt.get) _activePlayer = None
    else _activePlayer = opt
  }

  /** Sets the active player to a new player. <p>
    *
    * If the active player is null, it assumes that the new active player option should be set to [[scala.None]]
    *
    * @param p The new player to set to. May be null, but not recommended.
    */
  def activePlayer_=(p: Player): Unit = activePlayer_=(Some(p))

  // Ditto.
  def getActivePlayer = _activePlayer
  // Ditto.
  def setActivePlayer(p: Player) = activePlayer_=(p)

  def world = _world

  def world_=(opt: Option[WorldLike]): Unit = {
    if(opt eq null) _world = None
    val null_opt = opt map { a => a eq null }
    if(null_opt.isDefined && null_opt.get) _world = None
    else _world = opt
  }

  def world_=(w: WorldLike): Unit = world_=(Some(w))

  def getWorld = world
  def setWorld(w: WorldLike) = world_=(w)

  /** Values that have to be saved in the game. */
  // TODO Move only relevant values from mechanics to here. Don't move junk in here.
  class Values private[PfeileContext] extends Serializable {
  }

}

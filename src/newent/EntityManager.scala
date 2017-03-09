package newent

import java.util.function._
import java.util.{Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, _}

import general.JavaInterop._
import general.{Delegate, LogFacility, Main}
import player.shop.trader.Trader
import world.Tile

import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._

// Self-evident.
class EntityManager {

  private val m_entityList = new ArrayList[GameObject]

  // Self-evident actually.
  val onEntityRegistered = Delegate.create[GameObject]
  val onEntityUnlogged = Delegate.create[GameObject]

  if(Main.isDebug) {
    onEntityRegistered += { entity => LogFacility.log(s"Entity registered: $entity") }
    onEntityUnlogged += { entity => LogFacility.log(s"Entity unlogged: $entity") }
  }

  def register(e: GameObject): Unit = {
    m_entityList.add(e)
    onEntityRegistered(e)
  }

  // local save of the player and the opponent. Provides an much faster access, once armies are registered.
  // Frankly, the coding style isn't brilliant, though. A better implementation would for example over the constructor,
  // but that would lead to problems during the initialization process (EntityManager is created in World).
  private var player: Player = null
  private var opponent: Player = null

  /** Returns the player for faster access. <b>This method will return null</b> before method
    * <code>definePlayer(Player player)</code> has been called.
    */
  def getPlayer: Player = player

  /** Returns the opponent for faster access. It is the player, which is defined to be the opponent at ContextCreator.
    * This method however, will <b>return null before defineOpponent(Player opponent) hasn't been called</b>.
    */
  def getOpponent: Player = opponent

  /** Defines the player to be the player (whereas the other player will be the opponent. The implementation allows
    * only one player to be defined as player, but before this method is called <code>getPlayer()</code> will return null.
    * ContextCreator uses this method in the PopulatorStage.
    *
    * @param player an player already registered to entity manager
    * @return true, if the player has been successfully defined as player.
    * @throws NullPointerException if the player is null
    */
  def definePlayer(player: Player): Boolean = {
    if (player == null)
      throw new IllegalArgumentException("EntityManager cannot register null as a player.")

    if (this.player != null) {
      LogFacility.log("A player is already registered, cannot register another player, unlog the old one: " +
        "existing Player: " + this.player + "; new player: " + player, LogFacility.LoggingLevel.Warning)
      return false
    }
    if (!m_entityList.contains(player)) {
      LogFacility.log("EntityManager cannot define an entity as player, if it isn't registered. Register the player first: "
        + player, LogFacility.LoggingLevel.Warning)
      return false
    }
    this.player = player
    LogFacility.log("Registered the player as the acting player: " + player, LogFacility.LoggingLevel.Info)
    return true
  }

  /** Defines the opponent player, just like <code>definePlayer(Player player)</code> does for the player. Used in
    * ContextCreator and allows (only after the PopulatorStage of ContextCreator!) a faster access to the  opponent
    *
    * @param opponent an Player, registered to EntityManager not equal to null
    * @return true if there is no other player defined as opponent
    * @throws NullPointerException if opponent is null
    */
  def defineOpponent(opponent: Player): Boolean = {
    if (opponent == null)
      throw new IllegalArgumentException("EntityManager cannot register null as the opponent player.")

    if (this.opponent != null) {
      LogFacility.log("An opponent player is already registered, cannot register another opponent, unlog the old one: " +
        "existing Opponent: " + this.opponent + "; new player: " + opponent, LogFacility.LoggingLevel.Warning)
      return false
    }
    if (!m_entityList.contains(opponent)) {
      LogFacility.log("EntityManager cannot define an entity as opponent player, if it isn't registered. Register the " +
        "Opponent first: " + opponent, LogFacility.LoggingLevel.Warning)
      return false
    }
    this.opponent = opponent
    LogFacility.log("Registered the player as the opponent: " + opponent, LogFacility.LoggingLevel.Info)
    return true
  }

  def unlog(e: GameObject): Unit = {
    val prev = entityList
    sortOut { _ == e }
    if(prev.diff(entityList).nonEmpty)
      onEntityUnlogged(e)
  }

  /**
    * The listing of all game objects currently registered in the manager.
    */
  @volatile
  def entityList: Seq[GameObject] = m_entityList.asScala

  def getEntityList: IList[GameObject] = m_entityList.toImmutableList

  /**
    * Removes all entities that satisfy given predicate.
    *
    * @param f The filter function. If the function returns `true` for
    *          a given game object, that game object is going to be removed.
    */
  def sortOut(f: Predicate[GameObject]): Unit = m_entityList removeIf f

  /**
    * Returns the helper object for this entity manager, containing helper methods for easier entity handling.
    */
  def helper = Helper
  def getHelper = helper

  /**
    * Any helper functions related to make finding certain entities easier can go in here.
    */
  object Helper {

    /**
      * Collects exclusively the game objects whose position matches given position or whose shape contains
      * given coordinate.
      */
    def getEntitiesAt(x: Int, y: Int): IList[GameObject] = getEntityList.parallelStream.filter(entity => entity.getGridX == x && entity.getGridY == y).toImmutableList

    def getEntitiesAt(t: Tile): IList[GameObject] = {
      require(t != null)
      getEntityList.stream.filter(_.tileLocation == t).toImmutableList
    }

    def getPlayers: IList[Player] = entityList.collect { case p: Player => p }.asJava

    def getPlayerByName(name: String): Optional[Player] = {
      val playerList = getPlayers.asScala
      val maybePlayer = playerList.filter(_.name == name)

      assert(maybePlayer.size <= 1, s"Multiple players registered with name '$name'")

      maybePlayer.headOption.asJava
    }

    def getAllAttackContainers: IList[AttackContainer] = entityList.collect({ case x: AttackContainer => x }).map(_.asInstanceOf[AttackContainer]).asJava

    def getAllTraders: IList[Trader] = entityList.collect { case x: Trader => x }.asJava

  }

}

/** An exception indicating that given name is not unique to the manager.
  *
  * @param name The ambiguous name.
  */
class NotUniqueNameException(name: String) extends Exception

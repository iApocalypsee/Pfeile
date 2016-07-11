package newent

import java.util.function._
import java.util.{Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, _}

import general.JavaInterop._
import general.{Delegate, LogFacility, Main}
import player.shop.Trader
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
    m_entityList add e
    onEntityRegistered(e)
  }

  def unlog(e: GameObject): Unit = {
    val prev = entityList
    sortOut { _ == e }
    if(prev.diff(entityList).nonEmpty) onEntityUnlogged(e)
  }

  /**
    * The listing of all game objects currently registered in the manager.
    */
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

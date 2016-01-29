package newent

import general.{Delegate, LogFacility, Main}
import world.Tile

import scala.collection.{JavaConversions, mutable}

/** Base trait for all entity managers.
  *
  * I expect many changes inside of derived EntityManager classes, so
  * let's make an abstract base trait for it directly.
  *
  * @author Josip Palavra
  */
class EntityManager {

  private var _entityList = mutable.MutableList[GameObject]()

  /** Called when an entity has been registered. */
  val onEntityRegistered = Delegate.create[GameObject]
  val onEntityUnlogged = Delegate.create[GameObject]

  if(Main.isDebug) {
    onEntityRegistered += { entity => LogFacility.log(s"Entity registered: $entity") }
    onEntityUnlogged += { entity => LogFacility.log(s"Entity unlogged: $entity") }
  }

  /** Registers an entity to the manager.
    *
    * @param e The entity to add.
    */
  def +=(e: GameObject): Unit = {
    _entityList += e
    onEntityRegistered(e)
  }

  // Ditto.
  def register(e: GameObject) = +=(e)

  /**
    * Removes the specified game object from the manager.
    *
    * @param e The game object to remove.
    */
  def -=(e: GameObject): Unit = {
    val prev = entityList
    sortOut { _ ne e }
    if(prev.diff(entityList).nonEmpty) onEntityUnlogged(e)
  }

  /**
    * Does the same as `-=`
    *
    * @param e Ditto.
    */
  def unlog(e: GameObject) = -=(e)

  /**
    * The listing of all game objects currently registered in the manager.
    */
  def entityList: Seq[GameObject] = _entityList.toSeq

  /**
    * Java interop method for easier access to the game object list.
    */
  def javaEntityList = JavaConversions.seqAsJavaList(entityList)

  /**
    * Removes all entities that satisfy given predicate.
    *
    * @param f The filter function. If the function returns <code>false</code> for
    *          a given entity, that entity is going to be removed.
    */
  def sortOut(f: GameObject => Boolean): Unit = _entityList = _entityList filter f

  /**
    * Removes all entities that do not satisfy given predicate.
    *
    * In contrast to the [[newent.EntityManager#sortOut(scala.Function1)]] method, this one removes the entity
    * if it '''satisfies''' a predicate, in other words, if the entity returns <code>false</code>, it is being
    * kept in the manager.
    *
    * @param f The filter function.
    */
  def sortOutNot(f: GameObject => Boolean): Unit = sortOut(f andThen { b => !b })

  /**
    * Returns the helper object for this entity manager, containing helper methods for easier entity handling.
    */
  def helper = Helper

  /**
    * Any helper functions can go in here.
    */
  object Helper {

    def getEntitiesAt(x: Int, y: Int) = entityList.filter(entity => entity.getGridX == x && entity.getGridY == y)
    def getEntitiesAt(t: Tile): Seq[GameObject] = {
      require(t != null)
      entityList.filter(_.tileLocation == t)
    }

    def getPlayers = entityList.collect { case p: Player => p }
    def getPlayerByName(name: String) = this.getPlayers.find(_.name == name)



  }

}

/** An exception indicating that given name is not unique to the manager.
  *
  * @param name The ambiguous name.
  */
class NotUniqueNameException(name: String) extends Exception

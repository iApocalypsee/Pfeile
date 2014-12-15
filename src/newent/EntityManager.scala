package newent

import general.Delegate

import scala.collection.{JavaConversions, mutable}

/** Base trait for all entity managers.
  *
  * I expect many changes inside of derived EntityManager classes, so
  * let's make an abstract base trait for it directly.
  * @author Josip Palavra
  */
sealed trait EntityManagerLike {

  /** Called when an entity has been registered. */
  val onEntityRegistered = Delegate.create[EntityLike]
  val onEntityUnlogged = Delegate.create[EntityLike]

  /** Registers an entity to the manager.
    *
    * @param e The entity to add.
    */
  def +=(e: EntityLike): Unit

  // Ditto.
  def register(e: EntityLike) = +=(e)

  /** Unregisters the specified entity from the manager.
    *
    * @param e The entity to remove.
    */
  def -=(e: EntityLike): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val prev = entityList
    sortOut { _ ne e }
    if(prev.diff(entityList).nonEmpty) onEntityUnlogged callAsync e
  }

  // Ditto.
  def unlog(e: EntityLike) = -=(e)

  /** The listing of all entities that are currently registered to the manager. */
  def entityList: scala.collection.Seq[EntityLike]
  /** Java interop method to entityList(). */
  def javaEntityList = JavaConversions.seqAsJavaList(entityList)

  /** Filters all entities out that do not satisfy a predicate, and saves the changes!!!
    *
    * @param f The filter function. If the function returns <code>false</code> for
    *          a given entity, that entity is going to be removed.
    */
  def sortOut(f: (EntityLike) => Boolean): Unit

  /** Filters all entities out that satisfy a predicate, and saves the changes!!!
    *
    * In contrast to the [[sortOut()]] method, this one removes the entity if it <b>satisfies</b> a predicate,
    * in other words, if the entity returns <code>false</code>, it is being kept in the manager.
    * @param f The filter function.
    */
  def sortOutNot(f: (EntityLike) => Boolean): Unit = sortOut(f andThen { b => !b })

}

/** The default entity manager.
  *
  * This implementation is not parallel, but I am thinking about a parallel implementation soon.
  * @author Josip Palavra
  */
class DefaultEntityManager extends EntityManagerLike {

  private var _entityList = mutable.MutableList[EntityLike]()

  override def +=(e: EntityLike): Unit = {
    _entityList += e
    onEntityRegistered(e)
  }

  override def sortOut(f: (EntityLike) => Boolean): Unit = _entityList = _entityList filter f

  override def entityList: Seq[EntityLike] = _entityList.toList
}

/** An exception indicating that given name is not unique to the manager.
  *
  * @param name The ambiguous name.
  */
class NotUniqueNameException(name: String) extends Exception

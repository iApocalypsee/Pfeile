package newent

import scala.collection.{mutable, JavaConversions}

/** Base trait for all entity managers.
  *
  * I expect many changes inside of derived EntityManager classes, so
  * let's make an abstract base trait for it directly.
  * @author Josip Palavra
  */
sealed trait EntityManagerLike {

  /** Registers an entity to the manager.
    *
    * @param e The entity to add.
    */
  def +=(e: Entity): Unit

  // Ditto.
  def register(e: Entity) = +=(e)

  /** Unregisters the specified entity from the manager.
    *
    * @param e The entity to remove.
    */
  def -=(e: Entity): Unit

  // Ditto.
  def unlog(e: Entity) = -=(e)

  /** The listing of all entities that are currently registered to the manager. */
  def entityList: scala.collection.Seq[Entity]
  /** Java interop method to entityList(). */
  def javaEntityList = JavaConversions.seqAsJavaList(entityList)

  /** Filters all entities out that do not satisfy a predicate.
    *
    * @param f The filter function. If the function returns <code>false</code> for
    *          a given entity, that entity is going to be removed.
    */
  def filter(f: (Entity) => Boolean): Unit

  /** Filters all entities out that satisfy a predicate.
    *
    * In contrast to the [[filter()]] method, this one removes the entity if it <b>satisfies</b> a predicate,
    * in other words, if the entity returns <code>false</code>, it is being kept in the manager.
    * @param f The filter function.
    */
  def filterNot(f: (Entity) => Boolean): Unit = filter(f andThen { b => !b })

}

/** The default entity manager.
  *
  * This implementation is not parallel, but I am thinking about a parallel implementation soon.
  * @author Josip Palavra
  */
class DefaultEntityManager extends EntityManagerLike {

  private var _entityList = mutable.MutableList[Entity]()

  override def +=(e: Entity): Unit = _entityList += e

  override def -=(e: Entity): Unit = _entityList = _entityList filter { e eq _ }

  override def filter(f: (Entity) => Boolean): Unit = _entityList filter f

  override def entityList: Seq[Entity] = _entityList.toList
}

package newent

import general.Mechanics
import player.weapon.Item

import scala.collection.{JavaConversions, mutable}

/** Base trait for all inventory classes.
  *
  * @author Josip Palavra
  */
trait InventoryLike {

  def put(i: Item): Unit

  /** Removes one item that satisfies given predicate, if any.
    *
    * @param f The selector function.
    * @return The item that satisfied the predicate, if any.
    */
  def remove(f: (Item) => Boolean): Option[Item]

  /** Removes all items that satisfy a predicate out of the inventory.
    *
    * The items that are in the collection are removed from the inventory.
    *
    * @param f The selector function.
    * @return The removed items from the inventory.
    */
  def removeAll(f: (Item) => Boolean): Seq[Item]

  /** Tries to find a specified item that satisfies given predicate.
    *
    * @param f The find function.
    * @return An item that satisfies a predicate, if any. If nothing has been found,
    *         [[None]] is returned.
    */
  def find(f: (Item) => Boolean): Option[Item]

  def items: Seq[Item]
  def javaItems = JavaConversions.seqAsJavaList(items)

  /** The maximum size of the inventory. */
  def maximumSize = Integer.MAX_VALUE

  def currentSize = items.size

}

class DefaultInventory extends InventoryLike {

  private var _list = mutable.ArrayBuffer[Item]()

  override def put(i: Item): Unit = {
      // TODO: only put in the inventory, if there is space left.
      _list += i
  }

  override def remove(f: (Item) => Boolean): Option[Item] = {
    val opt_find = find(f)
    val index_opt = opt_find map { i => _list.indexOf(i) }
    if(index_opt.isDefined) _list.remove(index_opt.get)
    opt_find
  }

  override def removeAll(f: (Item) => Boolean): Seq[Item] = {
    val ret = _list filter f
    _list = _list filterNot f
    ret
  }

  override def find(f: (Item) => Boolean): Option[Item] = _list find f

  override def items = _list.toList
}

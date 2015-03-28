package newent

import player.item.{Item, PutItemAmount}

import scala.collection.{JavaConversions, mutable}

/**
  * Base trait for all inventory classes.
  *
  * @author Josip Palavra
  */
trait InventoryLike {

  def put(i: Item): Boolean

  def put(putItemAmount: PutItemAmount): Unit = putItemAmount.putInto(this)

  /**
    * Removes one item that satisfies given predicate, if any.
    *
    * @param f The selector function.
    * @return The item that satisfied the predicate, if any.
    */
  def remove(f: (Item) => Boolean): Option[Item] = remove(f, 1).headOption

  /**
    * Removes given items with an amount.
    * @param f The selector function.
    * @param amount How many items be removed from the inventory.
    * @return
    */
  def remove(f: Item => Boolean, amount: Int): Seq[Item]

  /**
    * Removes all items that satisfy a predicate out of the inventory.
    *
    * The items that are in the collection are removed from the inventory.
    *
    * @param f The selector function.
    * @return The removed items from the inventory.
    */
  def removeAll(f: (Item) => Boolean): Seq[Item]

  /**
    * Tries to find a specified item that satisfies given predicate.
    *
    * @param f The find function.
    * @return An item that satisfies a predicate, if any. If nothing has been found,
    *         [[scala.None]] is returned.
    */
  def find(f: (Item) => Boolean): Option[Item]

  def items: Seq[Item]
  def javaItems = JavaConversions.seqAsJavaList(items)

  /** The maximum size of the inventory. */
  def maximumSize = Integer.MAX_VALUE

  def currentSize = items.size

  /** Removes every item from the inventory. The inventory will be empty afterwards.  */
  def clear(): Unit

}

class DefaultInventory extends InventoryLike {

  private var _list = mutable.ArrayBuffer[Item]()

  override def put(i: Item): Boolean = {
    if (maximumSize > currentSize) {
      _list += i
      true
    }
    else {
      System.err.println("Inventory.maximumSize reached. Cannot put Item "+i.getClass.getName)
      false
    }
  }

  override def remove(f: (Item) => Boolean, amount: Int): Seq[Item] = {
    var removeCount = 0
    val filtered = _list.filterNot(item => {
      if (removeCount < amount && f(item)) {
        removeCount += 1
        true
      }
      else false
    })
    _list = filtered
    _list diff filtered
  }

  override def removeAll(f: (Item) => Boolean): Seq[Item] = remove(f, Int.MaxValue)

  override def find(f: (Item) => Boolean): Option[Item] = _list find f

  override def items = _list.toList

  /** Removes every item from the inventory. The inventory will be empty afterwards.  */
  override def clear(): Unit = {
    _list.clear()
  }
}

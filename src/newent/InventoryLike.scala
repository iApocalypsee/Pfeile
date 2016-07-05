package newent

import java.util.function._
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, _}

import general.JavaInterop._
import player.item.{Item, PutItemAmount}

import scala.collection.JavaConverters._
import scala.collection.{JavaConversions, mutable}
import scala.compat.java8.FunctionConverters._
import scala.compat.java8.OptionConverters._
import scala.compat.java8._

// Self-explanatory, I guess. Except for that it is an interface with some default implementations...
trait InventoryLike {

  /**
    * Puts the item in the inventory, returning a boolean if the method was successful in doing that.
    *
    * @param i The item to add to the inventory.
    */
  def put(i: Item): Boolean

  /**
    * Experimental DSL for me (playing around with Scala a little :D).
    *
    * @param putItemAmount The DSL brick to interpret.
    */
  def put(putItemAmount: PutItemAmount): Unit = putItemAmount.putInto(this)

  /**
    * Removes one item that satisfies given predicate, if any.
    *
    * @param f The selector function.
    * @return The item that satisfied the predicate, if any.
    */
  def remove(f: Predicate[Item]): Optional[Item] = remove(f, 1).headOption

  /**
    * Removes given items with an amount.
    *
    * @param f The selector function.
    * @param amount How many items be removed from the inventory.
    * @return A list of all removed items.
    *         This list may not have up to `amount` elements for obvious reasons.
    */
  def remove(f: Predicate[Item], amount: Int): IList[Item]

  /**
    * Removes the item f. If it has been found, it will be removed from the inventory and returned. If the item is
    * listed several times only the first occurrence is removed.
    *
    * @param f any item
    * @return the removed item or an empty option if the item does not exist
    */
  def remove(f: Item): Optional[Item] = remove(_ == f)

  /* Removes every item from the list, which is from the same instance as <code>Item f</code>. */
  //def removeAllInstanceOf(f: Item): Unit

  /**
    * Removes all items that satisfy a predicate out of the inventory.
    *
    * The items that are in the collection are removed from the inventory.
    *
    * @param f The selector function.
    * @return The removed items from the inventory.
    */
  def removeAll(f: Predicate[Item]): IList[Item]

  /**
    * Tries to find a specified item that satisfies given predicate.
    *
    * @param f The find function.
    * @return An item that satisfies a predicate, if any. If nothing has been found,
    *         [[scala.None]] is returned.
    */
  def find(f: Predicate[Item]): Optional[Item]

  def items: Seq[Item]
  def getItems: IList[Item] = JavaConversions.seqAsJavaList(items)

  /**
    * The maximum size of the inventory.
    * `Integer.MAX_VALUE` is not appropriate all of the time, it should be made a constant that can be set
    * through the constructor.
    */
  def capacity = Integer.MAX_VALUE

  /**
    * Number of how many items are currently stored inside the inventory.
    */
  def currentSize = items.size

  /**
    * Removes every item from the inventory. The inventory will be empty afterwards.
    */
  def clear(): Unit

  override def toString: String = {
    "Inventory [size: " + currentSize + "/" + capacity + "]"
  }

}

/**
  * Default inventory implementation.
  */
class DefaultInventory extends InventoryLike {

  private var _list = mutable.ArrayBuffer[Item]()

  override def put(i: Item): Boolean = {
    if (capacity > currentSize) {
      _list += i
      true
    }
    else {
      System.err.println("Inventory.capacity reached. Cannot put Item " + i.getName)
      false
    }
  }

  override def remove(f: Predicate[Item], amount: Int): IList[Item] = {
    var removeCount = 0
    val filtered = _list.filterNot(item => {
      if (removeCount < amount && f(item)) {
        removeCount += 1
        true
      }
      else false
    })
    val old = _list
    _list = filtered
    (old diff filtered).asJava
  }

  override def removeAll(f: Predicate[Item]): IList[Item] = remove(f, Int.MaxValue)

  override def find(f: Predicate[Item]): Optional[Item] = (_list find f.asScala).asJava

  override def items = _list.toList

  /** Removes every item from the inventory. The inventory will be empty afterwards.  */
  override def clear(): Unit = {
    _list.clear()
  }
}

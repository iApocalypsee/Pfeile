package misc

import java.util
import scala.util.Random

/**
 *
 * @author Josip
 * @version 28.05.2014
 */
object RandomHelper {

  def pick[T <: Any](collection: util.Collection[T]): T = {
    val random = new Random
    val index = random nextInt(collection size)
    val it = collection.iterator
    var count = 0
    while(it.hasNext) {
      val value = it.next
      if(count == index) return value
      count += 1
    }
    throw new AssertionError("Impossible cause.")
  }

  def pick[T <: Any](collection: Iterable[T]): T = {
    val list = collection.toList
    val rand = new Random
    list.apply(rand.nextInt(list.size))
  }

}

package general.property

import java.util.function._
import java.util.{Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import scala.compat.java8.FunctionConverters._

/**
  * The most basic trait of a property is the get and set branch.
  *
  * @tparam A The type of property. No variance by design.
  */
trait PropertyBase[A] extends PropertyLike[A] with EagerEval[A] {
  // A property needs to have an accessor style. Needs to have.
  self: AccessorStyle[_] =>

  private var value: Option[A] = None

  override def get: A = option.getOrElse(throw new RuntimeException("Property is undefined. Set to null somewhere? Lazy compute triggered? Did setter accept value?"))

  override def set(x: A): Unit = value = Option(x)

  override def option = value

  override def ifdef(x: Consumer[A]) = option.foreach(x.asScala)

}

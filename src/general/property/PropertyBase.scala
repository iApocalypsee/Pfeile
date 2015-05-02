package general.property

import java.util.function._

import general.JavaInterop

/**
  * The most basic trait of a property is the get and set branch.
  * @tparam A The type of property. No variance by design.
  * @tparam CompatibleA A compatibility type erasure of the `A` type parameter.
  *                     Only needed for some Java primitives. For everything else
  *                     just put the same in this as you would put into A.
  */
trait PropertyBase[A] {
  // A property needs to have an accessor style. Needs to have.
  self: AccessorStyle[_] =>

  private var value: Option[A] = None

  def get: A = option.getOrElse(throw new RuntimeException("Property is empty. Was the (lazy compute) value accepted by setter/validation and the property initialized lazily before?"))

  def set(x: A): Unit = x match {
    case anyRef: AnyRef =>
      if (anyRef == null) value = None
      else value = Some(x)
    case _: Any =>
      value = Some(x)
  }

  def option = value

  def ifdef(x: A => Unit) = option.foreach(x)
  def ifdef(javafun: Consumer[A]): Unit = ifdef(JavaInterop.asScala(javafun))

  def isDefined = option.isDefined
  def isEmpty = option.isEmpty

  @inline def orElse[B >: A](alternative: => Option[B]) = option.orElse(alternative)
}

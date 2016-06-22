package general.property

import java.util.function.{Consumer, Supplier}

import scala.compat.java8.FunctionConverters.asScalaFromConsumer

/**
  * The most abstract trait for properties.
  * @tparam A The type of variable to hold.
  */
trait PropertyLike[A] {
  setStyle: SetEvalStyle[_] =>

  def get: A

  @deprecated("Use get instead") def apply(): A = get

  def option: Option[A]

  def ifdef(x: A => Unit)

  def ifdefj(x: Consumer[A]) = ifdef(asScalaFromConsumer(x))

  def isDefined = option.isDefined

  def isEmpty = option.isEmpty

  @inline def orElse[B >: A](alternative: => Option[B]) = option.orElse(alternative)
}

/**
  * Normal set behavior. Evaluates given variable instantly.
 *
  * @tparam A The type of variable to hold.
  */
trait EagerEval[A] extends PropertyLike[A] with SetEvalStyle[EagerEval[A]] {
  def set(x: A)
  def :=(x: A) = set(x)
}

/**
  * Trait which declares that setting this property does not evaluate the given argument in the set method,
  * but evaluates it just then as it is really required.
 *
  * @tparam A The type of variable to hold.
  */
trait LazyEval[A] extends PropertyLike[A] with SetEvalStyle[EagerEval[A]] {
  def set(f: => A): Unit
  def :=(f: => A): Unit = set(f)
  def set(javafun: Supplier[A]): Unit = set(javafun.get())
}

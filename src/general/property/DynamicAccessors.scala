package general.property

import java.util.function._
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import scala.compat.java8.FunctionConverters._
import scala.compat.java8._

/**
  * Accessor style for properties which get/set branch can be changed dynamically.
  * This type of accessor is something to look out for, it can cause many bugs.
  * @tparam A The type of property. No variance by design.
  */
trait DynamicAccessors[A] extends PropertyBase[A] with AccessorStyle[DynamicAccessors[A]] {

  private var _dynGetter = identity[A] _
  private var _dynSetter = identity[A] _

  /**
    * The getter to use. Defaults to returning the input parameter.
    */
  def dynGet = _dynGetter

  protected def dynGet_=(x: A => A) = {
    require(x != null)
    _dynGetter = x
  }

  protected def setDynamicGetter(f: Function[A, A]): Unit = this.dynGet = f.asScala

  /**
    * The setter to use. Defaults to returning the input parameter.
    */
  def dynSet = _dynSetter

  /**
    * Sets a setter function for this property.
    *
    * The setter function can change its provided input argument, so use with care.
    * @param x The new setter (which can override the input argument)
    */
  protected def dynSet_=(x: A => A) = {
    require(x != null)
    _dynSetter = x
  }

  /**
    * Sets a setter function which simply produces some side effect.
    *
    * The original input value provided to the setter function is not changed at all because of Unit return type.
    * @param x The side-effect function to apply as setter.
    */
  protected def dynPass(x: A => Unit) = dynSet_=({ y => x(y); y })

  protected def setDynamicSetter(f: Function[A, A]) = this.dynSet = f.asScala

  protected def appendGet(x: A => A): Unit = dynGet = dynGet andThen x
  protected def appendSet(x: A => A): Unit = dynSet = dynSet andThen x

  protected def appendGetJava(x: Function[A, A]): Unit = appendGet(x.asScala)
  protected def appendSetJava(x: Function[A, A]): Unit = appendSet(x.asScala)

  override def get = dynGet(super.get)

  override def set(x: A) = super.set(dynSet(x))

}

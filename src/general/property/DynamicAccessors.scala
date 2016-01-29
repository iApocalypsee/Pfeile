package general.property

import java.util.function._

import general.JavaInterop

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

  protected def setDynamicGetter(f: Function[A, A]): Unit = this.dynGet = JavaInterop.asScala(f)

  /**
    * The setter to use. Defaults to returning the input parameter.
    */
  def dynSet = _dynSetter

  protected def dynSet_=(x: A => A) = {
    require(x != null)
    _dynSetter = x
  }

  protected def setDynamicSetter(f: Function[A, A]) = this.dynSet = JavaInterop.asScala(f)

  protected def appendGet(x: A => A): Unit = dynGet = dynGet andThen x
  protected def appendSet(x: A => A): Unit = dynSet = dynSet andThen x

  protected def appendGetJava(x: Function[A, A]): Unit = appendGet(JavaInterop.asScala(x))
  protected def appendSetJava(x: Function[A, A]): Unit = appendSet(JavaInterop.asScala(x))

  override def get = dynGet(super.get)

  override def set(x: A) = super.set(dynSet(x))

}

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
  def dynamicGetter = _dynGetter

  def dynamicGetter_=(x: A => A) = {
    require(x != null)
    _dynGetter = x
  }
  
  def setDynamicGetter(f: Function[A, A]): Unit = this.dynamicGetter = JavaInterop.asScala(f)
  

  /**
    * The setter to use. Defaults to returning the input parameter.
    */
  def dynamicSetter = _dynSetter

  def dynamicSetter_=(x: A => A) = {
    require(x != null)
    _dynSetter = x
  }
  
  def setDynamicSetter(f: Function[A, A]) = this.dynamicSetter = JavaInterop.asScala(f)
  

  def appendGetter(x: A => A): Unit = dynamicGetter = dynamicGetter andThen x
  def appendSetter(x: A => A): Unit = dynamicSetter = dynamicSetter andThen x

  def appendGetterJava(x: Function[A, A]): Unit = appendGetter(JavaInterop.asScala(x))
  def appendSetterJava(x: Function[A, A]): Unit = appendSetter(JavaInterop.asScala(x))

  override def get = dynamicGetter(super.get)

  override def set(x: A) = super.set(dynamicSetter(x))
}

package general.property

import java.lang

class DynamicProperty[A] extends PropertyBase[A] with DynamicAccessors[A] {

  def this(x: A) = {
    this()
    this set x
  }

}

object DynamicProperty {

  def lazily[A](x: => A): LazyDynamicProperty[A] = {
    val lazyprop = new LazyDynamicProperty[A]
    lazyprop.lazyCompute = () => x
    lazyprop
  }

}

class LazyDynamicProperty[A] extends DynamicProperty[A] with LazyInit[A]

class IntDynamicProperty extends DynamicProperty[lang.Integer]
class DoubleDynamicProperty extends DynamicProperty[lang.Double]
class LongDynamicProperty extends DynamicProperty[lang.Long]

class LazyIntDynamicProperty extends LazyDynamicProperty[lang.Integer]
class LazyDoubleDynamicProperty extends LazyDynamicProperty[lang.Double]
class LazyLongDynamicProperty extends LazyDynamicProperty[lang.Long]

//class LazyDynamicProperty[A] extends PropertyLike[A, A] with DynamicAccessors[A, A] with LazyInit[A, A]


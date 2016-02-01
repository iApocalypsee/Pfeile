package general.property

import java.lang

class StaticProperty[A](initGetTransform: A => A, initSetTransform: A => A) extends PropertyBase[A] with StaticAccessors[A] {

  private val getTransform = Option(initGetTransform)
  private val setTransform = Option(initSetTransform)

  def this() = {
    this(null, null)
  }

  def this(x: A) = {
    this()
    this set x
  }

  override def staticSetter(x: A) = if(setTransform.isDefined) super.staticSetter(setTransform.get.apply(x)) else super.staticSetter(x)

  override def staticGetter(x: A) = if(getTransform.isDefined) super.staticGetter(getTransform.get.apply(x)) else super.staticGetter(x)

}

object StaticProperty {
  def lazily[A](x: => A): LazyStaticProperty[A] = {
    val lazyProp = new LazyStaticProperty[A]
    lazyProp.lazyCompute = () => x
    lazyProp
  }
}

class LazyStaticProperty[A] extends StaticProperty[A] with LazyInit[A]

class IntStaticProperty extends StaticProperty[lang.Integer] {
  def this(x: lang.Integer) = {
    this()
    set(x)
  }
}

class FloatStaticProperty extends StaticProperty[lang.Float] {
  def this(x: lang.Float) {
    this()
    this set x
  }
}

class DoubleStaticProperty extends StaticProperty[lang.Double] {
  def this(x: lang.Double) = {
    this()
    set(x)
  }
}

class LongStaticProperty extends StaticProperty[lang.Long] {
  def this(x: lang.Long) = {
    this()
    set(x)
  }
}

class LazyIntStaticProperty extends LazyStaticProperty[lang.Integer]
class LazyDoubleStaticProperty extends LazyStaticProperty[lang.Double]
class LazyFloatStaticProperty extends LazyStaticProperty[lang.Float]
class LazyLongStaticProperty extends LazyStaticProperty[lang.Long]

//class LazyStaticProperty[A] extends PropertyLike[A, A] with StaticAccessors[A, A] with LazyInit[A, A]

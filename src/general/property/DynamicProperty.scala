package general.property

import java.lang

class DynamicProperty[A <: AnyRef] extends PropertyBase[A] with DynamicAccessors[A]
class LazyDynamicProperty[A <: AnyRef] extends DynamicProperty[A] with LazyInit[A]

class IntDynamicProperty extends DynamicProperty[lang.Integer]
class DoubleDynamicProperty extends DynamicProperty[lang.Double]
class LongDynamicProperty extends DynamicProperty[lang.Long]

class LazyIntDynamicProperty extends LazyDynamicProperty[lang.Integer]
class LazyDoubleDynamicProperty extends LazyDynamicProperty[lang.Double]
class LazyLongDynamicProperty extends LazyDynamicProperty[lang.Long]

//class LazyDynamicProperty[A] extends PropertyLike[A, A] with DynamicAccessors[A, A] with LazyInit[A, A]


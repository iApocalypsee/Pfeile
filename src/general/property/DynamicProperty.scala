package general.property

import java.lang

class DynamicProperty[A <: AnyRef] extends PropertyBase[A] with DynamicAccessors[A]

class IntDynamicProperty extends DynamicProperty[lang.Integer]
class DoubleDynamicProperty extends DynamicProperty[lang.Double]
class LongDynamicProperty extends DynamicProperty[lang.Long]

//class LazyDynamicProperty[A] extends PropertyLike[A, A] with DynamicAccessors[A, A] with LazyInit[A, A]


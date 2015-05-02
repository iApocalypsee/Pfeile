package general.property

import java.lang

class StaticProperty[A <: AnyRef] extends PropertyBase[A] with StaticAccessors[A]

class IntStaticProperty extends StaticProperty[lang.Integer]
class DoubleStaticProperty extends StaticProperty[lang.Double]
class LongStaticProperty extends StaticProperty[lang.Long]

//class LazyStaticProperty[A] extends PropertyLike[A, A] with StaticAccessors[A, A] with LazyInit[A, A]

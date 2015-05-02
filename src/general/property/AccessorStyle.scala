package general.property

/**
  * Trait for defining a style through which the property is accessed.
  * Every [[general.property.PropertyBase]] '''requires''' exactly __one__ AccessorStyle mixin to
  * be able to compile.
  * @tparam SelfType The subclassing class/trait itself.
  */
trait AccessorStyle[SelfType]

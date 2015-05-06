package general.property

/**
  * Trait for defining a style through which the property is accessed.
  * Every [[general.property.PropertyBase]] '''requires''' exactly __one__ AccessorStyle mixin to
  * be able to compile.
  * @tparam SelfType The subclassing class/trait itself.
  */
// Lazy property accessor styles should not trigger computation of set argument! So I need another
// accessor style hierarchy for lazy evaluation.
trait AccessorStyle[SelfType]

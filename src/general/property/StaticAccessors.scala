package general.property

/**
  * Trait for properties carrying static getters and setters.
  * This style is the "normal" style for creating properties. It resembles writing a get/set method
  * for the property.
  * @tparam A The type of property. No variance by design.
  */
trait StaticAccessors[A] extends PropertyBase[A] with AccessorStyle[StaticAccessors[A]] {

  /**
    * The getter that is never going to change for the property.
    * Just like writing a get method, which can never change as well.
    * Override this method to create customized getter.
    */
  def staticGetter(x: A): A = x

  /**
    * The setter that is never going to change for the property.
    * Just like writing a set method, which can never change as well.
    * Override this method to create customized setter.
    */
  def staticSetter(x: A): A = x

  /**
    * Ditto.
    * @return A possibly transformed value of the property. Depends on the static getter.
    */
  override def get = staticGetter(super.get)

  /**
    * Overrides the set method so that it uses the static setter to transform the value.
    * @param x The value to set the property to.
    */
  override def set(x: A) = super.set(staticSetter(x))

}

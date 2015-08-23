package comp

import general.traitarg

/** An object that can be represented by a [[comp.IComponent]] object.
  *
  * The component of the [[comp.DisplayRepresentable]] object can be changed easily by just calling
  * the setter.
  */
trait DisplayRepresentable {

  /** The component. Is going to be lazily initialized in the getter. */
  private var _component: Component = null

  /** Returns the component of the representable object. */
  def component: Component = {
    // Lazy initialization of the component, because some components rely
    // on data that is initiialized maybe afterwards.
    if(_component == null) {
      val start = startComponent
      require(start != null)
      _component = start
    }
    _component
  }

  /** Sets the representing component. */
  def component_=(a: Component) = {
    require(a != null)
    _component = a
  }

  // Ditto.
  def getComponent = component
  def setComponent(a: Component) = this.component = a

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  @traitarg protected def startComponent: Component

}

abstract class AbstractDisplayRepresentable extends DisplayRepresentable

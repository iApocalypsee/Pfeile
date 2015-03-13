package comp

/** An object that can be represented by a [[comp.IComponent]] object.
  *
  * The component of the [[DisplayRepresentable]] object can be changed easily by just calling
  * the setter.
  */
trait DisplayRepresentable {

  /** The component. Is going to be lazily initialized in the getter. */
  private var _component: Component = null

  /** Returns the component of the representable object. */
  def component: Component = {
    // Lazy initialization of the component, because some components rely
    // on data that is initiialized maybe afterwards.
    if(_component eq null) {
      val start = startComponent
      require(start ne null)
      _component = start
    }
    _component
  }
  /** Sets the representing component. */
  def component_=(a: Component) = {
    require(a ne null)
    _component = a
  }

  // Ditto.
  def getComponent = component
  def setComponent(a: Component) = this.component = a

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  protected def startComponent: Component

}

abstract class AbstractDisplayRepresentable extends DisplayRepresentable

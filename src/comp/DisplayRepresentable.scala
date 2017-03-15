package comp

/**
  * An object that can be represented by a [[comp.Component]] object.
  *
  * The component of the [[comp.DisplayRepresentable]] object can be changed easily by just calling
  * the setter.
  */
trait DisplayRepresentable {

  /**
    * The component. Is going to be lazily initialized in the getter.
    */
  private var m_component = null.asInstanceOf[Component]

  /**
    * Returns the component of the representable object.
    */
  def component: Component = {
    // Lazy initialization of the component, because some components rely
    // on data that is initialized maybe afterwards.
    if(m_component == null) {
      component = startComponent
    }
    m_component
  }

  /**
    * Sets the represented component.
    */
  def component_=(a: Component): Unit = {
    require(a != null)
    m_component = a
  }

  def getComponent: Component = component
  def setComponent(a: Component): Unit = this.component = a

  /**
    * The component that the representable object uses first. Method is called only once.
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    */
  protected def startComponent: Component

}

/**
  * This class exists for compatibility reasons.
  */
abstract class AbstractDisplayRepresentable extends DisplayRepresentable

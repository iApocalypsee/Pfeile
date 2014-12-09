package newent

import general.{Metadatable, Property}

/** Represents the current state of the entity. */
abstract class Attributes {

  /** The current properties of the entity. */
  lazy val current: Property[Current] = {
    val startObject = initialCurrent(new Current)
    require(startObject != null)
    Property.withValidation(startObject)
  }

  /** The lasting properties of the entity. */
  lazy val lasting: Property[Lasting] = {
    val startObject = initialLasting(new Lasting)
    require(startObject != null)
    Property.withValidation(startObject)
  }

  /** Checks if the new current object has enough metadata objects. */
  lazy val currentDataCheck = Property.withValidation[Current => Boolean] { _: Current => true }
  /** Checks if the new lasting object has enough metadata objects. */
  lazy val lastingDataCheck = Property.withValidation[Lasting => Boolean] { _: Lasting => true }

  // Pass the new set object to the currentDataCheck function to validate it.
  current.onSet += { _.newVal.map { x => require(currentDataCheck.get.apply(x)) } }
  lasting.onSet += { _.newVal.map { x => require(lastingDataCheck.get.apply(x)) } }

  protected def initialCurrent(initObject: Current): Current

  protected def initialLasting(initObject: Lasting): Lasting

  class Current extends Metadatable

  class Lasting extends Metadatable

}

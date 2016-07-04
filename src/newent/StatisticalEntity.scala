package newent

import general.property.StaticProperty

/**
  * Any entity that can contain some sort of statistics to represent itself.
  *
  */
trait StatisticalEntity extends Entity {

  /**
    * The statistics object in which every statistical occurrence is collected.
    */
  lazy val statistics = {
    val initAttribs = initialAttribute
    alignInitialAttributes(initAttribs)
    new StatisticsRecorder(initAttribs)
  }

  /**
    * The initial attribute object with which the entity begins recording its attributes.
    *
    * The method just gets called once to initialize an underlying field.
    */
  protected def initialAttribute: Attributes = new Attributes {
    override protected def initialAttribs(initObject: Table) = initObject
  }

  /**
    * Provides the opportunity to subclasses to add their own attributes to the metadata table
    * of the [[newent.Attributes]] object.
    * Subclasses are not required to override this method, but if they do, they __have to__
    * call `super.alignInitialAttributes(x)` to guarantee the success of the attribute mechanism.
 *
    * @param x The attributes to be changed.
    */
  protected def alignInitialAttributes(x: Attributes): Unit = {}

}

/**
  * Wrapper class around the attributes object.
  *
  * Wrapping is necessary for adding statistical methods later; these would not fit the Attributes class
  * but more a separate class devoted to statistics alone.
 *
  * @param at The attribute object to pull the data from.
  */
class StatisticsRecorder(at: Attributes) {

  lazy val attributes = new StaticProperty(at)

}

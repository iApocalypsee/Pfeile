package newent

import general.Property

/** Any entity that can contain some sort of statistics to represent itself.
  *
  */
trait StatisticalEntity extends Entity {

  /** The statistics object in which every statistical occurrence is collected. */
  lazy val statistics = new StatisticsRecorder(initialAttribute)

  /** The initial attribute object with which the entity begins recording its attributes.
    *
    * The method just gets called once to initialize an underlying field.
    */
  protected def initialAttribute: Attributes

}

class StatisticsRecorder(at: Attributes) {

  lazy val attributes = Property.withValidation(at)

}

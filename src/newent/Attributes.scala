package newent

import general._
import general.property.StaticProperty

/**
  * Represents the current, generic state of the entity.
  */
abstract class Attributes {

  /**
    * The current properties of the entity.
    */
  lazy val current = new StaticProperty[Table]() {
    val startObject = initialAttribs(new Table)

    override def staticSetter(x: Table) = {
      require(startObject != null)
      require(isTableOkay(x))
      x
    }
  }

  protected def isTableOkay(t: Table) = true

  protected def initialAttribs(initObject: Table): Table

  class Table extends Metadatable

}

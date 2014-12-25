package general

import scala.collection.mutable

/**
  * A choice which can have multiple decisions and consequences attached to them.
  *
  * To line up decisions for a particular choice, subclass the `Choice` class and
  * start adding fields to the choice which have the type [[general.Choice#Decision]].
  * These decisions will be added automatically to the [[general.Choice#decisions()]]
  * list.
  */
class Choice {

  /** All decisions with which the request can be completed. */
  lazy val decisions = {
    val thisClass = this.getClass
    // I have to interact with code that possibly has not been even written yet.
    val decisionFields = thisClass.getDeclaredFields.filter { x =>
      x.setAccessible(true)
      x.getType == classOf[Decision]
    }
    // Collects every field of type decision from this class under this list.
    val collectingList = mutable.ListBuffer[Decision]()
    for (x <- decisionFields) {
      collectingList += x.get(this).asInstanceOf[Decision]
    }
    collectingList.toList
  }

  case class Decision(private val consequence: () => Unit) {

    /**
      * Called when this particular decision has been triggered.
      * This delegate applies only to this decision and nothing else,
      * unlike the [[general.Choice#onChoosed()]].
      */
    lazy val onChoosed = Delegate.createZeroArity

    /** Picks this decision. */
    def decide(): Unit = {
      onChoosed()
      consequence()
    }

  }

}


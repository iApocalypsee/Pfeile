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
      // I cannot access private fields, so make them accessible
      x.setAccessible(true)
      // If the field is not of type Decision, ignore it
      val isDecisionType = x.getType == classOf[Decision]
      // If the field has a IgnorableDecision annotation attached to it, ignore it.
      val hasAnnotation = x.getAnnotation(classOf[IgnorableDecision]) != null
      isDecisionType && !hasAnnotation
    }

    // Collects every field of type decision from this class under this list.
    val collectingList = mutable.ListBuffer[Decision]()
    for (x <- decisionFields) {
      collectingList += x.get(this).asInstanceOf[Decision]
    }
    collectingList.toList
  }

  case class Decision(private val consequence: () => Unit) {

    /** Picks this decision. */
    def decide(): Unit = {
      consequence()
    }

  }

}


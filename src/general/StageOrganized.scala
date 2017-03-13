package general

import general.io.StageDescriptable

import scala.collection.mutable

/**
  * Can execute stages in a certain order.
  */
class StageOrganized {

  private val m_stages = mutable.ListBuffer[StageDescriptable[_]]()

  /**
    * Called when a stage has completed execution.
    */
  val onStageDone = Delegate.create[StageCompletedEvent[_]]

  /**
    * Called when the last stage has completed execution.
    */
  val onLastStageDone = Delegate.create[StageCompletedEvent[_]]

  // Last stage callback
  onStageDone += { event => if(event.stage == m_stages.last) onLastStageDone(event) }

  /**
    * The stages registered to the stage collection.
    * @return A list of all registered stages.
    */
  def stages = m_stages.toList

  /**
    * Adds a stage to this stage collection.
    * The handed stage is prepared internally for execution, some delegates are given callbacks specific to
    * this class.
    * @param x The stage to add.
    */
  def addStage(x: StageDescriptable[_]): Unit = {
    x.onStageExecuted += { result => onStageDone(StageCompletedEvent(x, result)) }
    m_stages += x
  }

  /**
    * Executes all registered stages.
    */
  def execute(): Unit = {
    for (stage <- m_stages) {
      stage.executeStage()
    }
  }

}

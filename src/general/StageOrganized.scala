package general

import general.io.StageDescriptable

/**
  * Can execute stages in a certain order.
  */
class StageOrganized {

  /**
    * Called when a stage has completed execution.
    */
  val onStageDone = Delegate.create[StageDescriptable[_]]

  /**
    * Property in which the current executing stage is saved.
    */
  private val currentStageProp = Property[StageDescriptable[_]]()
  currentStageProp appendSetter { newStage =>
    // Redirect the callback flow into the onStageDone delegate.
    newStage.onStageExecuted.registerOnce(() => onStageDone(newStage))
    newStage
  }

  /**
    * Executes the given stage.
    * Be careful when using threads, this implementation is '''not thread-safe'''.
    *
    * Does exactly the same as invoking the set branch of [[general.StageOrganized#currentStage()]] property.
    * @param stage The stage to execute.
    * @tparam A The return type of the stage.
    * @return The value computed by the stage.
    */
  def execute[A](stage: StageDescriptable[A]): A = {
    currentStageProp set stage
    stage.executeStage()
  }

}

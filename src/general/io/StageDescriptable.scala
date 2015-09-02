package general.io

import general.Delegate

/**
  * An object which can be associated with a "stage name".
  *
  * The use of StageDescriptable is this:
  *
  * The StageDescriptable object can be used to get executed by a [[general.StageOrganized]] instance. In most
 * cases, you are going to subclass StageOrganized
  */
trait StageDescriptable[A] {

  /** The name of the stage. */
  def stageName: String

  /** Executes the stage. */
  def executeStage(): Unit = {
    val ret = executeStageImpl()
    onStageExecuted(ret)
  }

  /** The implementation of the stage. */
  protected def executeStageImpl(): A

  override def toString: String = "Stage[" + stageName + "]"

  /** Called when the stage execution has been completed. */
  val onStageExecuted = Delegate.create[A]

}

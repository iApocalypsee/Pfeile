package general.io

import general.Delegate

/** An object which can be associated with a "stage name".
  *
  * For example, if a class does just loading, the class can give itself a name
  * for this particular stage of loading, e.g. "Loading world".
  *
  */
trait StageDescriptable[A] {
  
  /** The name of the stage. */
  def stageName: String

  /** Executes the stage. */
  def executeStage(): A = {
    val ret = executeStageImpl()
    onStageExecuted()
    ret
  }

  /** The implementation of the stage. */
  protected def executeStageImpl(): A
  
  /** Called when the stage execution has been completed. */
  val onStageExecuted = Delegate.createZeroArity

}

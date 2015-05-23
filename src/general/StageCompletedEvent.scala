package general

import general.io.StageDescriptable

/**
  * Fired when a stage completes in the [[StageOrganized]] class.
  * @param stage The stage that finished executing.
  * @param result The result of the computation.
  * @tparam A The type of the result.
  */
case class StageCompletedEvent[A](stage: StageDescriptable[_], result: A)

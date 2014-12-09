package general

import general.io.StageDescriptable

/**
 *
 * @author Josip Palavra
 */
class StageOrganized {

  /** The current stage that is executing right now. */
  lazy val currentStage = Property.withValidation[StageDescriptable[_]]()

}

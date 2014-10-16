package newent

/**
 *
 * @author Josip Palavra
 */
trait VisionEntity extends Entity {

  val visionMap = new VisionMap(this)

  var visionRadius = 4

  // These calls are NOT OPTIONAL right now, don't delete them!
  setGridX(getGridX)
  setGridY(getGridY)

}

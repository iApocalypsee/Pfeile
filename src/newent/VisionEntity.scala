package newent

/** Entity that keeps track of its vision.
  *
  * Entities with this trait can keep track of their vision field. The vision field
  * can contain tiles that have been discovered.
  */
trait VisionEntity extends Entity {

  /** The data where every vision detail is stored. */
  val visionMap = new VisionMap(this)

  /** The sight radius. */
  var visionRadius = 5

}

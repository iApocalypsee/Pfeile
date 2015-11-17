package newent

import newent.VisionMap.VisionPromise

import scala.beans.BeanProperty

/**
  * Entity that keeps track of its vision.
  *
  * Entities with this trait can keep track of their vision field. The vision field
  * can contain tiles that have been discovered.
  */
trait VisionEntity extends Entity {

  /**
    * Object mapping all tiles to certain states based on whether this entity can see or
    * has already seen a tile.
    */
  val visionMap = new VisionMap(this)

  /**
    * The sight radius of the vision entity.
    * Only applied when self tracking is activated.
    * To activate self tracking, set the self tracking property to true.
    * Do not set negative values.
    */
  @BeanProperty var visionRadius = 5

  private var m_selfVisionPoint: Option[VisionPromise] = None
  private var m_selfTracking = false

  def hasSelfTracking = m_selfTracking
  def hasSelfTracking_=(x: Boolean) = {
    m_selfTracking = x
    if (m_selfTracking) onAppliedSelfTracking()
    else onDeactivatedSelfTracking()
  }

  def setSelfTracking(x: Boolean) = hasSelfTracking_=(x)

  /**
    * Called when the self tracking property of the vision entity has been set to true.
    */
  private def onAppliedSelfTracking(): Unit = {
    releaseVisionPoint()
    m_selfVisionPoint = Some(visionMap.grantVision(getGridX, getGridY, visionRadius))
  }

  /**
    * Called when the self tracking property of the vision entity has been set to false.
    */
  private def onDeactivatedSelfTracking(): Unit = {
    releaseVisionPoint()
    m_selfVisionPoint = None
  }

  /**
    * Releases the current self tracking vision point, if it exists.
    */
  private def releaseVisionPoint(): Unit = {
    for(visionPoint <- m_selfVisionPoint) visionPoint.releaseVision()
  }

  /**
    * Allocates a new self tracking local vision point located at given coordinates.
    * @param gridX Ditto.
    * @param gridY Ditto.
    */
  private def refreshSelfTracking(gridX: Int, gridY: Int): Unit = {
    releaseVisionPoint()
    m_selfVisionPoint = Some(visionMap.grantVision(gridX, gridY, visionRadius))
  }

  override protected def setGridPosition(x: Int, y: Int) = {
    super.setGridPosition(x, y)
    refreshSelfTracking(x, y)
  }
}

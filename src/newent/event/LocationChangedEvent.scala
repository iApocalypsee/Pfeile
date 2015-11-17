package newent.event

import newent.GameObject

/** Represents an event in which an entity's location changed.
  *
  * @param fromX From what x position the entity started.
  * @param fromY From what y position the entity started.
  * @param toX To what x position the entity heads.
  * @param toY To what y position the entity heads.
  * @param entity A reference to the entity.
  */
case class LocationChangedEvent(fromX: Int, fromY: Int, toX: Int, toY: Int, entity: GameObject) {

  require(entity != null)

  /** The start tile. */
  def start = entity.world.terrain.tileAt(fromX, fromY)
  /** The end tile. */
  def end = entity.world.terrain.tileAt(toX, toY)

  def diffX = toX - fromX
  def diffY = toY - fromY

}

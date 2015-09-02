package world

import newent.EntityLike

/** Trait for tiles that hold references to currently standing entities locally and do
  * not calculate the entities on demand. <p>
  * When the position of an entity changes, the changes are automatically captured. <p>
  * <b>Seems not to work right now. Do not use it.</b>
  */
trait LocalEntitySaveStrategy extends TileLike {

  import scala.collection.mutable

  private val _entities = mutable.ArrayBuffer[EntityLike]()

  terrain.world.entities.onEntityRegistered += { entity =>
    entity.onLocationChanged += { locationChange =>

      locationChange.start match {
        case x: LocalEntitySaveStrategy => x._entities -= locationChange.entity
        case _ =>
      }
      locationChange.end match {
        case x: LocalEntitySaveStrategy => x._entities += locationChange.entity
        case _ =>
      }

    }
  }

  /** The entities that are currently on this tile. */
  override def entities: Seq[EntityLike] = _entities.toList


}

/** Trait for tiles that calculate the entities that are standing currently on the tile
  * by going through the entity manager and filtering all entities that satisfy the position.
  */
trait OnDemandEntitiesStrategy extends TileLike {
  override def entities: Seq[EntityLike] = terrain.world.entities.entityList.filter { e => e.containsCoordinate(this.getGridX, this.getGridY) }
}

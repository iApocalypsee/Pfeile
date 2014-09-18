package world

import newent.EntityManagerLike

/** Base trait for all worlds.
  *
  * Other than in the previous implementations, the world class is just here to link "puzzle pieces" together,
  * such as the terrain with the entities.
  * @author Josip Palavra
  */
trait WorldLike {

  def terrain: TerrainLike
  def entities: EntityManagerLike

}

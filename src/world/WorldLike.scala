package world

import newent.{DefaultEntityManager, EntityManagerLike}

/** Base trait for all worlds.
  *
  * Other than in the previous implementations, the world class is just here to link "puzzle pieces" together,
  * such as the terrain with the entities.
  * @author Josip Palavra
  */
trait WorldLike {

  /** The terrain that describes the geography of the world. */
  def terrain: TerrainLike
  /** The entities that describe the population of the world. */
  def entities: EntityManagerLike

  /** The name of the world. Defaults to its hash code. */
  def name: String = hashCode().toString

}

class DefaultWorld extends WorldLike {

  /** The terrain that describes the geography of the world. */
  override val terrain = new DefaultTerrain(this)
  terrain.generate()()

  /** The entities that describe the population of the world. */
  override val entities = new DefaultEntityManager

}

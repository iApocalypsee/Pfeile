package world

import newent.EntityManager

/** Base trait for all worlds.
  *
  * Other than in the previous implementations, the world class is just here to link "puzzle pieces" together,
  * such as the terrain with the entities.
  *
  * @author Josip Palavra
  */
class World(width: Int, height: Int) {

  /**
    * The terrain that describes the geography of the world.
    */
  val terrain: Terrain = new Terrain(this, width, height)
  def getTerrain = terrain

  /**
    * The entities that describe the population of the world.
    */
  val entities = new EntityManager
  def getEntities = entities

  /**
    * The name of the world. Defaults to its hash code.
    */
  def name: String = hashCode().toString

  def generateDefault(): Unit = {
    terrain.generate(new scala.util.Random().nextLong())
  }

}

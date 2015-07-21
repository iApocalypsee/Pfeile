package player.item.ore

import general.LogFacility

import scala.annotation.tailrec
import scala.collection.{JavaConversions, mutable}
import scala.util.Random

/**
  * Keeps track of all ores that are in the game.
  */
object OreRegistry {

  private val m_registeredOres = mutable.ListBuffer[RegistryEntry]()

  def add(x: RegistryEntry): Unit = {
    LogFacility.log(s"Registered $x to ore registry", "Info", "orereg")
    m_registeredOres += x
  }

  def load(json: String): Unit = {
    throw new NotImplementedError("JSON loading not implemented yet")
  }

  def registeredOres = m_registeredOres.toList
  def getRegisteredOres = JavaConversions.seqAsJavaList(registeredOres)

  /**
    * Picks a completely random ore. This method does not care about how probably a spawning of an ore is.
    */
  def completelyRandomOre = registeredOres(Random.nextInt(registeredOres.size))

  /**
    * Picks a random ore, taking into account the probability of the OreSpawner.
    * The current implementation uses the [[completelyRandomOre]] method if the algorithm
    * cannot pick an ore.
    */
  def randomOre: RegistryEntry = {

    // The maximum amount of iterations the recursion is executing
    val maxIterations = 3

    /**
      * Implementation of the random ore picking. May be changed later, this algorithm is not perfect.
      * @param remainingSpawners The spawners from which to choose yet.
      * @param iterations How many times the recursion has been going.
      * @return An ore registry entry that has been picked.
      */
    @tailrec def recursion(remainingSpawners: Seq[RegistryEntry], iterations: Int): Option[RegistryEntry] = {
      val probability = Random.nextDouble()
      val nextSpawners = remainingSpawners.filter(_.spawner.spawnProbability <= probability)
      if (iterations == maxIterations && nextSpawners.nonEmpty) Option(nextSpawners(Random.nextInt(nextSpawners.size)))
      else if(iterations == maxIterations) Option.empty
      else recursion(nextSpawners, iterations + 1)
    }

    recursion(registeredOres, 1) getOrElse completelyRandomOre
  }

  class RegistryEntry(val oreClass: Class[_ <: ItemOre], val spawner: OreSpawn) {
    def toJson = ???

    override def toString: String = s"RegistryEntry($oreClass, $spawner)"
  }

}

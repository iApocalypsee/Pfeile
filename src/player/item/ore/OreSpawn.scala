package player.item.ore

import geom.functions.FunctionCollection

import scala.util.Random

/**
  * Represents the circumstances under which an ore spawns.
  * @param spawnProbability The probability of spawning this ore while performing a brush stroke.
  *                         Clamped between 0 and 1.
  * @param brushStrokeLength The average length of the stroke.
  * @param brushStrokeLengthVariation How much the stroke length may vary. Must be positive.
  * @param brushDensity How dense the ore should be generated. Clamped between 0 and 1.
  * @param depthMin The minimum depth of this ore.
  * @param depthMax The maximum depth of this ore.
  * @param amountMin The minimum amount of ore that spawns in one ore pocket.
  * @param amountMax The maximum amount of ore that spawns in one ore pocket.
  * @param oreSpawning The ore spawned in the world.
  */
case class OreSpawn(spawnProbability: Double, brushStrokeLength: Double, brushStrokeLengthVariation: Double, brushDensity: Double,
               depthMin: Int, depthMax: Int, amountMin: Int, amountMax: Int, oreSpawning: Class[_ <: ItemOre]) {

  def onSpawnAllocated(e: OreSpawnAllocatedEvent): Unit = {
    val generateProbably = Random.nextDouble()
    if(generateProbably > spawnProbability) return

    val spawnX = e.point.x
    val spawnY = e.point.y
    val oreAmount = FunctionCollection.clamp(Random.nextInt(amountMax), amountMin, amountMax)
    val depth = FunctionCollection.clamp(Random.nextInt(depthMax), depthMin, depthMax)

    for (tile <- e.spawnedIn.tileAtOption(spawnX, spawnY)) {
      tile.tileProperties.oreDeposit = Option(new OreDeposit(() => oreSpawning.newInstance(), this, oreAmount, depth, tile))
    }
  }

}

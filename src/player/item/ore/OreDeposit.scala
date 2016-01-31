package player.item.ore

import general.LogFacility
import geom.functions.FunctionCollection
import world.Tile

import scala.collection.JavaConversions

/**
  * Represents an underground ore deposit which can be mined for ore.
  * @param dropOre The function providing the actual ore object on mining.
  * @param spawnCircumstance Under what circumstances this ore has spawned.
  * @param m_amount The initial amount of ore that can be mined.
  * @param depth The physical depth of the ore deposit (how much a player has to dig down to actually "find" the deposit)
  * @param coupleTile The tile where the ore deposit can be found.
  */
class OreDeposit(val dropOre: () => ItemOre, val spawnCircumstance: OreSpawn, private var m_amount: Int, val depth: Int, private val coupleTile: Tile) {

  /**
    * Creates the given amount of ItemOre objects and returns them in a list.
    * The amount that is entered is clamped between 0 and the currently available amount of ore in this deposit.
    * So if the argument should exceed the currently available ore, it gets capped.
    *
    * @param amount The amount of ore to mine.
    * @return The mined ore.
    */
  def mine(amount: Int = 1): Seq[ItemOre] = {
    // The amount that can actually be mined. Runtime check variable to not generate too many ore objects.
    val effectiveAmount = FunctionCollection.clamp(amount, 0, m_amount)
    val minedOres = for (i <- 0 until amount) yield dropOre()
    LogFacility.log(s"Mined $amount ores: $minedOres")
    m_amount -= effectiveAmount
    if (m_amount <= 0) {
      LogFacility.log(s"Ore deposit at $coupleTile empty.", "Info")
      coupleTile.tileProperties.oreDeposit := null
    }
    minedOres
  }

  def mineJava(amount: Int): java.util.List[ItemOre] = JavaConversions.seqAsJavaList(mine(amount))

}

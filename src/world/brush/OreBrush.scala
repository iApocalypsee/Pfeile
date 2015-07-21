package world.brush

import java.awt.Point

import general.LogFacility
import player.item.ore.{OreRegistry, OreSpawnAllocatedEvent}
import world.TerrainLike

import scala.beans.BeanProperty

class OreBrush extends BrushLike {

  @BeanProperty var appliedOre: OreRegistry.RegistryEntry = null

  private var wasAppliedOrePreviouslyNull = false

  override protected def applySideEffects(t: TerrainLike, x: Int, y: Int, centerX: Int, centerY: Int): Unit = {
    if(appliedOreNullRoutine()) generateOrePocket(t, x, y)
  }

  /**
    * Method called for checking null on [[appliedOre]] and issuing proper commands for handling certain nullity cases.
    * Returns a flag telling if side effects are to be applied for the current call.
    */
  private def appliedOreNullRoutine(): Boolean = {
    if (appliedOre != null) {
      wasAppliedOrePreviouslyNull = false
      true
    }
    else if (!wasAppliedOrePreviouslyNull) {
      LogFacility.log("OreBrush: Ore to apply is null, skipping application until [[appliedOre]] is set to nonnull value")
      false
    }
    else false
  }

  /**
    * Method for generating the actual ore pocket for the tile.
    */
  private def generateOrePocket(t: TerrainLike, x: Int, y: Int) = {
    appliedOre.spawner.onSpawnAllocated(new OreSpawnAllocatedEvent(t, new Point(x, y)))
  }
}

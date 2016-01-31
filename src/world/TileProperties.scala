package world

import general.property.StaticProperty
import player.item.ore.OreDeposit

/**
  * Describes the state of the tile.
  */
class TileProperties {

  val oreDeposit = new StaticProperty[OreDeposit]
  var placedObject = Option.empty[Placeable]

}

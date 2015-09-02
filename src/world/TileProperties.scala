package world

import player.item.ore.OreDeposit

/**
  * Describes the state of the tile.
  */
class TileProperties {

  var oreDeposit = Option.empty[OreDeposit]
  var placedObject = Option.empty[Placeable]

}

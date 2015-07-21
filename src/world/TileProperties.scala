package world

import player.item.ore.OreDeposit

/**
  * Describes the state of the tile.
  */
class TileProperties {

  var oreDeposit: Option[OreDeposit] = None
  var placedObject: Option[Placeable] = None

}

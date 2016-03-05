package player.item.ore

import java.awt.image.BufferedImage

class IronOre extends ItemOre("IronOre") {
  override def getImage: BufferedImage = ???

  override protected def getTranslationIdentifier = "item/nature/ore/iron"
}

object IronOre {
  val SpawnCondition = new OreSpawn(0.4, 3, 0, 1, 10, 50, 50, 200, classOf[IronOre])
}

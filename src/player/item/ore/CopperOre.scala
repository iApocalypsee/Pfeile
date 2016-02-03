package player.item.ore

import java.awt.image.BufferedImage

class CopperOre extends ItemOre("CopperOre") {

  override def getImage: BufferedImage = ???

  override def getNameEnglish: String = "Copper ore"

  override def getNameGerman: String = "Kupfererz"

  override protected def getTranslationIdentifier = "item/nature/ore/copper"
}

object CopperOre {
  val SpawnCondition = OreSpawn(0.6, 5, 3, 1, 10, 40, 100, 200, classOf[CopperOre])
}

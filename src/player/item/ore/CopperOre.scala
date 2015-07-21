package player.item.ore

import java.awt.image.BufferedImage

class CopperOre extends ItemOre("Copper ore") {

  override def getImage: BufferedImage = ???

}

object CopperOre {
  val SpawnCondition = OreSpawn(0.6, 5, 3, 1, 10, 40, 100, 200, classOf[CopperOre])
}

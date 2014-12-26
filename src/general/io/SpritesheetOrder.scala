package general.io

import java.awt.image.BufferedImage

/**
  * Sorts unique sprite parts under a name.
  *
  * For instance, if you need a special texture from the spritesheet that is somewhere in the image,
  * the SpritesheetOrder class can help you retrieve the image and save it for later access.
  * {{{
  *   val order = new SpritesheetOrder(spritesheet)
  *   val x = order.addEntry("soldier", SpritesheetOrderEntry(3, 3, 1, 2))
  *   val y = order.apply("soldier")
  *   assert(x == y) // -> will throw no exception because x and y are indeed equal
  * }}}
  * This code snippet demonstrates the usage of this class:
  * Since the spritesheet is partitioned into squares, the user only has to care about from which squares
  * he wants to extract a texture.
  * In the example the user pulls out a soldier texture. The texture starts at (3|3), whilst the sprite
  * should be 1 square long and 2 squares high.
  *
  * @param spritesheet The spritesheet to pull the data from.
  */
class SpritesheetOrder private[io] (private val spritesheet: Spritesheet) {

  private var _entries = Map[String, SpritesheetOrderEntry]()

  def addEntry(partName: String, s: SpritesheetOrderEntry): SpritesheetOrderEntry = {
    _entries = _entries + (partName -> s)
    s
  }

  def apply(partName: String) = _entries(partName)

  case class SpritesheetOrderEntry(startX: Int, startY: Int, spriteWidth: Int, spriteHeight: Int) {

    def subimage: BufferedImage = {
      def squareMultiply(x: Int) = x * spritesheet.squareSize
      spritesheet.img.getSubimage(squareMultiply(startX), squareMultiply(startY), squareMultiply(spriteWidth), squareMultiply(spriteHeight))
    }

  }

}


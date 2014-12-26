package general.io

import java.awt.image.BufferedImage

/**
  * A spritesheet which an contain several images.
  * @param img The image itself.
  * @param squareSize How big a single "subimage" is. The "subimage" is always formed as a square.
  */
class Spritesheet private (val img: BufferedImage, val squareSize: Int) {

  // The length of a side of one sprite should be even...
  require(squareSize % 2 == 0)

  /** The ordering from which unique sprites can be retrieved through names. */
  val order = new SpritesheetOrder(this)

}

object Spritesheet {

  def create(img: BufferedImage, squareSize: Int) = new Spritesheet(img, squareSize)

}

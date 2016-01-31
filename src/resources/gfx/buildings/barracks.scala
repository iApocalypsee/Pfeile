package resources.gfx.buildings

import java.awt.Point
import java.io.File
import javax.imageio.ImageIO

import gui.image.TextureUsage

object BarracksTexture extends TextureUsage {

  override protected def textureLoader = ImageIO.read(new File("src/resources/gfx/buildings/barracks.png"))

  sourceShapeVertices = Seq(
  new Point(343, 0),
  new Point(345, 0),
  new Point(688, 172),
  new Point(688, 313),
  new Point(345, 485),
  new Point(343, 485),
  new Point(0, 313),
  new Point(0, 172)
  )

}

// Mandatory for every texture script to return an instance of its usage
// new BarracksTexture

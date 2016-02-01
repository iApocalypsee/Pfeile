package world

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

import gui.image.TextureAtlas

class GrassTile(latticeX: Int, latticeY: Int, terrain: Terrain) extends Tile(latticeX,
  latticeY, terrain) {

  override def color = GrassTile.TileColor

  override def textureAtlas = GrassTile.Atlas

  override def requiredMovementPoints = 1
}

object GrassTile {
  val TileColor = new Color(0x1C9618)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/grass.png")))
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: Terrain) extends Tile(latticeX,
  latticeY, terrain) {

  override def color = SeaTile.TileColor

  override def textureAtlas = SeaTile.Atlas

  override def requiredMovementPoints: Int = 10
}

object SeaTile {
  val TileColor = new Color(0x3555DB)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/ocean.png")))
}

class CoastTile(latticeX: Int, latticeY: Int, terrain: Terrain) extends Tile(latticeX, latticeY, terrain) {

  /** Returns the color that is used to represent the isometric tile. */
  override def color = CoastTile.TileColor

  override def textureAtlas = CoastTile.Atlas

  /** The movement points that are required to get on this tile. */
  override def requiredMovementPoints = 6
}

object CoastTile {
  val TileColor = new Color(0x4865E0)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/ocean.png")))
}

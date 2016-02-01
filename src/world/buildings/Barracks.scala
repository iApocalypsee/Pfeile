package world.buildings

import comp.ImageComponent
import gui.image.TextureUsage
import gui.screen.GameScreen
import newent.Entity
import world.{Tile, World}

class Barracks(world: World, x: Int, y: Int) extends Entity(world, x, y) {

  override protected def startComponent = new VisualBarracks(this)

}

class VisualBarracks(barracks: Barracks) extends ImageComponent(0, 0, VisualBarracks.currentUsage.asInstanceOf[TextureUsage], GameScreen.getInstance()) {

  getTransformation.resetTransformation()

  setSourceShape(textureUsage.resultingSourceShape)

  getTransformation.scale(textureUsage.scaleX * (Tile.TileWidth * 2 / getWidth.asInstanceOf[Double]),
                          textureUsage.scaleY * (Tile.TileWidth * 2 / getWidth.asInstanceOf[Double]))

  setLocation(textureUsage.offsetX, Tile.TileHalfHeight * 3 - getHeight + textureUsage.offsetY)

  setParent(barracks.tileLocation.component)

}

object VisualBarracks {

  private val currentUsage = null

}

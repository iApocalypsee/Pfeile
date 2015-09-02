package world.buildings

import java.awt.Point
import java.io.File

import comp.ImageComponent
import gui.image.TextureUsage
import gui.screen.GameScreen
import newent.Entity
import world.{IsometricPolygonTile, WorldLike}

class Barracks(world: WorldLike, x: Int, y: Int) extends Entity(world, x, y, Seq(new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 0))) {

  override protected def startComponent = new VisualBarracks(this)

}

class VisualBarracks(barracks: Barracks) extends ImageComponent(0, 0, VisualBarracks.currentUsage, GameScreen.getInstance()) {

  getTransformation.resetTransformation()

  setSourceShape(textureUsage.resultingSourceShape)

  getTransformation.scale(textureUsage.scaleX * (IsometricPolygonTile.TileWidth * 2 / getWidth.asInstanceOf[Double]),
                          textureUsage.scaleY * (IsometricPolygonTile.TileWidth * 2 / getWidth.asInstanceOf[Double]))

  setLocation(textureUsage.offsetX, IsometricPolygonTile.TileHalfHeight * 3 - getHeight + textureUsage.offsetY)

  setParent(barracks.tileLocation.component)

}

object VisualBarracks {

  private val currentUsage = TextureUsage.parseUsageDirect(new File("src/resources/gfx/buildings/barracks.scala"))

}

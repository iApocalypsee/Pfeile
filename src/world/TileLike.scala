package world

import java.awt._
import java.io.File
import javax.imageio.ImageIO

import comp.DisplayRepresentable
import gui.image.TextureAtlas
import newent.{AttackContainer, EntityLike}

import scala.collection.JavaConversions
import scala.collection.JavaConverters._

/**
  * Base trait for all tiles.
  *
  * I am knowingly not implementing [[comp.IComponent]] here, because if the
  * Component framework changes, it would be easier to adapt the changes with a base trait
  * that has not implemented any Component interface.
  * @author Josip Palavra
  */
trait TileLike extends AnyRef with DisplayRepresentable with AttackContainer {

  /** The x position in the grid of the world. */
  val latticeX: Int
  /** The y position in the grid of the world. */
  val latticeY: Int
  /** The terrain to which the tile belongs to. */
  val terrain: TerrainLike

  /**
    * The properties of this tile.
    */
  def tileProperties: TileProperties

  /** The movement points that are required to get on this tile. */
  def requiredMovementPoints: Int

  /** The tile located north of this tile. */
  def north: Option[TileLike]

  /** The tile located northeast of this tile. */
  def northEast: Option[TileLike]

  /** The tile located east of this tile. */
  def east: Option[TileLike]

  /** The tile located southeast of this tile. */
  def southEast: Option[TileLike]

  /** The tile located south of this tile. */
  def south: Option[TileLike]

  /** The tile located southwest of this tile. */
  def southWest: Option[TileLike]

  /** The tile located west of this tile. */
  def west: Option[TileLike]

  /** The tile located northwest of this tile. */
  def northWest: Option[TileLike]

  /** The neighbors the tile has. */
  def neighbors: Seq[TileLike] = Seq(north, northEast, east, southEast, south, southWest, west, northWest).flatten

  /** The neighbors of this tile as a Java list. */
  def getNeighbors = neighbors.asJava

  /** The entities that are currently on this tile. */
  def entities: Seq[EntityLike]

  /** Java interop method for the entity list. */
  def javaEntities = JavaConversions.seqAsJavaList(entities)

  override def toString = s"(x=$latticeX|y=$latticeY) - ${getClass.getName}"

}

class GrassTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = GrassTile.TileColor

  override def textureAtlas = GrassTile.Atlas

  override def requiredMovementPoints = 1
}

object GrassTile {
  val TileColor = new Color(0x1C9618)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/grass.png")))
}

class SeaTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX,
  latticeY, terrain) {

  override def color = SeaTile.TileColor

  override def textureAtlas = SeaTile.Atlas

  override def requiredMovementPoints: Int = 10
}

object SeaTile {
  val TileColor = new Color(0x3555DB)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/ocean.png")))
}

class CoastTile(latticeX: Int, latticeY: Int, terrain: DefaultTerrain) extends IsometricPolygonTile(latticeX, latticeY, terrain) {

  /** Returns the color that is used to represent the isometric tile. */
  override def color = CoastTile.TileColor

  override def textureAtlas = CoastTile.Atlas

  /** The movement points that are required to get on this tile. */
  override def requiredMovementPoints = 10
}

object CoastTile {
  val TileColor = new Color(0x4865E0)
  val Atlas = new TextureAtlas(ImageIO.read(new File("src/resources/gfx/tile/ocean.png")))
}

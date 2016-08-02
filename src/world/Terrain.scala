package world

import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, _}

import world.brush.TileTypeBrush

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Random

/**
  * Base trait for all terrain types.
  *
  * With a need to support extensibility, we need a base "interface" so that we can support terrains
  * that are implemented differently.
  *
  * @author Josip Palavra
  */
class Terrain(val world: World, val width: Int, val height: Int) {

  require(width > 0, "Width may not be negative or 0.")
  require(height > 0, "Height may not be negative or 0.")

  // Wow. The generation process is so straightforward.
  private val _primitiveRandom = new Random
  // The tiles are managed in a (multidimensional) array, NOT in a scala collection class.
  private lazy val _tiles = Array.tabulate(width, height) { (x, y) =>
    _primitiveRandom.nextInt(2) match {
      case 0 => new GrassTile(x, y, Terrain.this)
      case 1 => new SeaTile(x, y, Terrain.this)
      case _ => null
    }
  }

  /**
    * Returns true if the specified coordinate is valid.
    *
    * @param x The x coordinate.
    * @param y The y coordinate.
    * @return is the point at the given point on the map (<code>x >= 0 && y >= 0 && x < width && y < height</code>)
    */
  def isTileValid(x: Int, y: Int) = x >= 0 && x < width && y >= 0 && y < height

  /**
    * Returns the tile at the given coordinate.
    *
    * It is implementation-specific how terrains are coping with coordinates that are out of bounds.
    * Some may throw an exception, others just return null. Like I said, implementation-specific.
    *
    * @param x The x coordinate.
    * @param y The y coordinate.
    * @return The tile at the specified coordinate.
    */
  def tileAt(x: Int, y: Int) = if(isTileValid(x, y)) _tiles(x)(y) else null
  def tileAtOption(x: Int, y: Int) = Option(tileAt(x, y))

  def getTileAt(x: Int, y: Int): Tile = tileAt(x, y)
  def getOptionalTile(x: Int, y: Int) = Optional of tileAt(x, y)

  /**
    * Returns the tiles in a one dimensional list.
    */
  def tiles: List[Tile] = _tiles.flatten.toList
  def getTiles: IList[Tile] = tiles.asJava

  /**
    * Sets the tile type at the given position to the specified tile.
    *
    * @param x The x position.
    * @param y The x position.
    * @param t The new tile type to set to.
    */
  def setTileAt(x: Int, y: Int, t: Tile): Unit = {
    require(t != null)
    _tiles(x)(y) = t
  }

  /**
    * Overwrites the contents of the tile array with grass fields.
    */
  def generatePlain(): Unit = {
    for(x <- 0 until width;
        y <- 0 until height) {
      _tiles(x)(y) = new GrassTile(x, y, this)
    }
  }

  /**
    * Generates the world with a random seed.
    *
    * @param seed The seed to use for the world generation algorithm.
    */
  def generate(seed: Long): Unit = {

    val r = new Random(seed)

    // Converts all sea tiles that have a grass tile neighbor to a coast tile
    def coasten() = {
      val adjacentSeaTiles = _tiles.flatten.collect {
        case seaTile: SeaTile if seaTile.neighbors.exists(_.isInstanceOf[GrassTile]) => seaTile
      }
      for (tile <- adjacentSeaTiles) setTileAt(tile.getGridX, tile.getGridY, new CoastTile(tile.getGridX, tile.getGridY, this))
    }

    // Wild painting. Literally, it's random painting.
    def tileTypeStage(): Unit = {

      case class TTPoint(x: Int, y: Int, tileType: Class[_ <: Tile])

      val points = mutable.MutableList[TTPoint]()

      for (i <- 0 until ((width * height) / 2)) {
        points += TTPoint(r.nextInt(width), r.nextInt(height), r.nextInt(2) match {
          case 0 => classOf[SeaTile]
          case 1 => classOf[GrassTile]
        })
      }

      val brush = new TileTypeBrush

      points foreach { p =>
        brush.tileType = p.tileType
        brush.applyBrush(this, p.x, p.y)
      }

    }

    r.setSeed(seed)

    tileTypeStage()
    coasten()

  }

  /**
    * The helper object for this terrain. This is the preferred way of obtaining the helper object,
    * since it is inline with Java then.
    */
  def helper = Helper

  /**
    * All helper methods related to the terrain come in here.
    */
  object Helper {

    /**
      * Finds the tile at the gui-position (posX|posY).
      *
      * @param posX the x-position on the screen / of a component
      * @param posY the y-position on the screen / of a component
      * @return The tile at given coordinates, or null if no tile can be found at these particular screen coordinates.
      */
    def findTile(posX: Double, posY: Double): Tile = tiles.find(tile => tile.component.getBounds.contains(posX, posY)).orNull

  }

}

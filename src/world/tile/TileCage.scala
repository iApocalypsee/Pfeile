package world.tile

import geom.PointDef
import world.{IBaseTile, WorldViewport}

/**
 * Represents a tile "wireframe". Each tile will have
 * a tile cage object. The tile cage object takes care of determining
 * the pixel positions of the tile with all transformations included, such
 * as zooming, moving the map, or something alike.
 * The use of the functions could be time and processor-expensive,
 * this class is running on a mathematical basis.
 *
 * @author Josip Palavra
 */
// TODO Add height matrix translations
// TODO I need more performance
class TileCage(private val tile: IBaseTile) {

  // a reference to the world viewport
  private lazy val _vp = tile.getWorld.getViewport
  // The translation that has to be made in order to make all y coordinates positive
  private lazy val _positiveTranslation = WorldViewport.TILE_PX_Y_DIMENSION * tile.getWorld.getSizeY / 2

  private def averageHeight(part1: IBaseTile, part2: IBaseTile, part3: IBaseTile): Int = {
    val part1h: Int = if(part1 == null) tile.getTileHeight else part1.getTileHeight
    val part2h: Int = if(part2 == null) tile.getTileHeight else part2.getTileHeight
    val part3h: Int = if(part3 == null) tile.getTileHeight else part3.getTileHeight
    (part1h + part2h + part3h) / 3
  }

  // These functions calculate the default pixel positions of the "wireframe"
  // of the tile.
  private def calcWestCorner(): PointDef = {
    val defHeight = averageHeight(tile.northwest(), tile.west(), tile.southwest()) - tile.getTileHeight
    new PointDef((tile.getGridX * WorldViewport.TILE_PX_X_DIMENSION + tile.getGridY * WorldViewport.TILE_PX_X_DIMENSION,
      tile.getGridX * WorldViewport.TILE_PX_Y_DIMENSION - tile.getGridY * WorldViewport.TILE_PX_Y_DIMENSION - defHeight))
  }

  private def calcNorthCorner(): PointDef = {
    val defHeight = averageHeight(tile.north(), tile.northwest(), tile.northeast()) - tile.getTileHeight
    val west = calcWestCorner()
    new PointDef((west.getX + WorldViewport.TILE_PX_X_DIMENSION, west.getY - WorldViewport.TILE_PX_Y_DIMENSION - defHeight))
  }

  private def calcSouthCorner(): PointDef = {
    val defHeight = averageHeight(tile.south(), tile.southeast(), tile.southwest()) - tile.getTileHeight
    val west = calcWestCorner()
    new PointDef((west.getX + WorldViewport.TILE_PX_X_DIMENSION, west.getY + WorldViewport.TILE_PX_Y_DIMENSION - defHeight))
  }

  private def calcEastCorner(): PointDef = {
    val defHeight = averageHeight(tile.northeast(), tile.east(), tile.southeast()) - tile.getTileHeight
    val west = calcWestCorner()
    new PointDef((west.getX + 2 * WorldViewport.TILE_PX_X_DIMENSION, west.getY - defHeight))
  }

  // DEFAULT VALUES, DEPENDANT ON THE GIVEN GRID POSITION
  // all of those point objects receive default values,
  // from these values, the final function results will be taken
  private var _northCorner: PointDef = null
  private var _westCorner: PointDef = null
  private var _southCorner: PointDef = null
  private var _eastCorner: PointDef = null

  def recomputeBase() {
    _northCorner = calcNorthCorner()
    _westCorner = calcWestCorner()
    _southCorner = calcSouthCorner()
    _eastCorner = calcEastCorner()
  }

  def north = {
    new PointDef((_northCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_northCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  }
  def west = {
    new PointDef((_westCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_westCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  }
  def south = {
    new PointDef((_southCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_southCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  }
  def east = {
    new PointDef((_eastCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_eastCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  }

}

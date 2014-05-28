package world.tile

import geom.PointDef
import world.Tile
import world.WorldViewport

/**
 * Represents a tile "wireframe". Each tile will have
 * a tile cage object. The tile cage object takes care of determining
 * the pixel positions of the tile with all transformations included, such
 * as zooming, moving the map, or something alike.
 * The use of the functions could be time and processor-expensive,
 * this class is running on a mathematical basis.
 *
 * With the TileCage class, the method `updateGUI()` is obsolete.
 * @author Josip Palavra
 */
// TODO Add height matrix translations
// TODO I need more performance
class TileCage(val tile: Tile) {

  // a reference to the world viewport
  private lazy val _vp = tile.getWorld.getViewport
  // The translation that has to be made in order to make all y coordinates positive
  private lazy val _positiveTranslation = WorldViewport.TILE_PX_Y_DIMENSION * tile.getWorld.getSizeY / 2

  // These functions calculate the default pixel positions of the "wireframe"
  // of the tile.
  private def calcWestCorner(): PointDef = {
    new PointDef((tile.getGridX * WorldViewport.TILE_PX_X_DIMENSION + tile.getGridY * WorldViewport.TILE_PX_X_DIMENSION,
      tile.getGridX * WorldViewport.TILE_PX_Y_DIMENSION - tile.getGridY * WorldViewport.TILE_PX_Y_DIMENSION))
  }

  private def calcNorthCorner(): PointDef = {
    val west = calcWestCorner()
    new PointDef((west.getX + WorldViewport.TILE_PX_X_DIMENSION, west.getY - WorldViewport.TILE_PX_Y_DIMENSION))
  }

  private def calcSouthCorner(): PointDef = {
    val west = calcWestCorner()
    new PointDef((west.getX + WorldViewport.TILE_PX_X_DIMENSION, west.getY + WorldViewport.TILE_PX_Y_DIMENSION))
  }

  private def calcEastCorner(): PointDef = {
    val west = calcWestCorner()
    new PointDef((west.getX + 2 * WorldViewport.TILE_PX_X_DIMENSION, west.getY))
  }

  // DEFAULT VALUES, DEPENDANT ON THE GIVEN GRID POSITION
  // all of those point objects receive default values,
  // from these values, the final function results will be taken
  private lazy val _northCorner: PointDef = calcNorthCorner()
  private lazy val _westCorner: PointDef = calcWestCorner()
  private lazy val _southCorner: PointDef = calcSouthCorner()
  private lazy val _eastCorner: PointDef = calcEastCorner()

  def north = new PointDef((_northCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_northCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  def west = new PointDef((_westCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_westCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  def south = new PointDef((_southCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_southCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))
  def east = new PointDef((_eastCorner.getX * _vp.getZoomFactor + _vp.getShiftX, (_eastCorner.getY + _positiveTranslation) * _vp.getZoomFactor + _vp.getShiftY))

}

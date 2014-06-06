package world

import geom.PointDef
import java.awt.geom.Point2D
import world.tile.FakeTile

/**
 *
 * @author Josip Palavra
 * @version 31.05.2014
 */
class GridElement(x: Int, y: Int) {

  private[world] var _gridX = x
  private[world] var _gridY = y
  private var _tile: BaseTile = new FakeTile(this)
  private[world] var _world: ScaleWorld = null
  private var _north: PointDef = calcNorthCorner()
  private var _east: PointDef = calcEastCorner()
  private var _south: PointDef = calcSouthCorner()
  private var _west: PointDef = calcWestCorner()

  private def calcWestCorner(): PointDef = {
    new PointDef((gridX * WorldViewport.TILE_PX_X_DIMENSION + gridY * WorldViewport.TILE_PX_X_DIMENSION,
      gridX * WorldViewport.TILE_PX_Y_DIMENSION - gridY * WorldViewport.TILE_PX_Y_DIMENSION))
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

  def tile = synchronized(this)._tile
  private[world] def tile_=(tile: BaseTile) = {
    _tile._gridElem = null
    val oldHeight = _tile.getTileHeight
    _tile = tile
    _tile._gridElem = this
    _tile.setTileHeight(oldHeight)
  }
  def gridX = _gridX
  def gridY = _gridY

  def recomputeCornerPosition() {
    _north = calcNorthCorner()
    _west = calcWestCorner()
    _south = calcSouthCorner()
    _east = calcEastCorner()
  }
  def north: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX - 1, _gridY + 1)) return null
    _tile.north().asInstanceOf[BaseTile]._gridElem
  }
  def northwest: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX - 1, _gridY)) return null
    _tile.northwest().asInstanceOf[BaseTile]._gridElem
  }
  def west: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX - 1, _gridY - 1)) return null
    _tile.west().asInstanceOf[BaseTile]._gridElem
  }
  def southwest: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX, _gridY - 1)) return null
    _tile.southwest().asInstanceOf[BaseTile]._gridElem
  }
  def south: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX + 1, _gridY - 1)) return null
    _tile.south().asInstanceOf[BaseTile]._gridElem
  }
  def southeast: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX + 1, _gridY)) return null
    _tile.southeast().asInstanceOf[BaseTile]._gridElem
  }
  def east: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX + 1, _gridY + 1)) return null
    _tile.east().asInstanceOf[BaseTile]._gridElem
  }
  def northeast: GridElement = {
    if(!_tile.getWorld.isTileValid(_gridX, _gridY + 1)) return null
    _tile.northeast().asInstanceOf[BaseTile]._gridElem
  }

  def northCorner = _north
  def eastCorner = _east
  def southCorner = _south
  def westCorner = _west
  private[world] def northCorner_=(ncorner: PointDef) = _north = ncorner
  private[world] def westCorner_=(wcorner: PointDef) = _west = wcorner
  private[world] def southCorner_=(scorner: PointDef) = _south = scorner
  private[world] def eastCorner_=(ecorner: PointDef) = _east = ecorner

  def world = _world

  def gridCenter() = {
    val p = new Point2D {

      var x = 0.0
      var y = 0.0

      override def setLocation(x: Double, y: Double): Unit = {
        this.x = x
        this.y = y
      }

      override def getY: Double = y

      override def getX: Double = x
    }
    p.setLocation(_gridX + 0.5, _gridY + 0.5)
    p
  }

  /**
   * Links the vertices of this grid element and the tile specified in the parameter.
   * @param that The tile being linked to this tile.
   * @throws ImpossibleLinkException if the tile is no neighbor tile.
   */
  private[world] def link(that: GridElement) {
    // if that is null, just return, there is no need to compute values for null
    if(that eq null) return
    // if that is no neighbor tile, throw exception; you should not be able to link
    // totally unrelated tiles together
    if(that.ne(north) && that.ne(northeast) && that.ne(east) && that.ne(southeast)
      && that.ne(south) && that.ne(southwest) && that.ne(west) && that.ne(northwest)) {
      throw new ImpossibleLinkException
    }

    if(that eq north) that._south = _north
    else if(that eq northwest) {
      that._east = _north
      that._south = _west
    } else if(that eq west) that._east = _west
    else if(that eq southwest) {
      that._north = _west
      that._east = _south
    } else if(that eq south) that._north = _south
    else if(that eq southeast) {
      that._north = _east
      that._west = _south
    } else if(that eq east) that._west = _east
    else {
      that._west = _north
      that._south = _east
    }
  }

  private class ImpossibleLinkException extends RuntimeException

}

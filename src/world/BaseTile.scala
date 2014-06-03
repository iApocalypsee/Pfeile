package world

import misc.metadata.OverrideMetadatable
import gui.AdjustableDrawing
import comp.{GUIUpdater, Component}
import java.awt.{Color, Graphics2D}
import world.tile.TileCage
import java.awt.geom.Point2D

import geom.DoubleRef.DoubleRef2Int
import geom.PointDef

import world.brush.HeightBrush

/**
 *
 * @author Josip Palavra
 * @version 31.05.2014
 */
abstract class BaseTile protected[world](private[world] var _gridElem: GridElement) extends
  Component with IBaseTile with AdjustableDrawing with OverrideMetadatable with GUIUpdater {

  private def selectMethod(g: Graphics2D) {
    g setColor BaseTile.selectColor
    g fillPolygon getBounds

  }

  // append an adjustable drawing
  // this handle only executes when the mouse is hovering over the tile
  private val selectDrawHandle = handle(selectMethod, () => isMouseFocused)

  private[world] var tileHeight: Int = 0

  override def getGridX: Int = _gridElem.gridX
  override def getGridY: Int = _gridElem.gridY
  override def getField: IField = ???
  override def getWorld: IWorld = _gridElem._world
  override def getCage: TileCage = ???
  override def getTileHeight: Int = getMetadata(HeightBrush.meta_key).asInstanceOf[Int]
  def setTileHeight(x: Int) = setMetadata(HeightBrush.meta_key, x.asInstanceOf[Integer])

  override def draw(gl: Graphics2D): Unit = {
    val g = gl.create().asInstanceOf[Graphics2D]
    // translate the whole matrix context, just like OpenGL's "glPushMatrix()" and "glPopMatrix()"
    g.translate(getWorld.getViewport.getShiftX, getWorld.getViewport.getShiftY)
    // draw the base tile shape with the base color
    g setColor getColor
    g fillPolygon getBounds
    // draw all other individual stuff
    drawAll(g)
    g.dispose()
  }

  def north: IBaseTile = {
    val x: Int = getGridX - 1
    val y: Int = getGridY + 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def northeast: IBaseTile = {
    val x: Int = getGridX
    val y: Int = getGridY + 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def east: IBaseTile = {
    val x: Int = getGridX + 1
    val y: Int = getGridY + 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def southeast: IBaseTile = {
    val x: Int = getGridX + 1
    val y: Int = getGridY
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def south: IBaseTile = {
    val x: Int = getGridX + 1
    val y: Int = getGridY - 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def southwest: IBaseTile = {
    val x: Int = getGridX
    val y: Int = getGridY - 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def west: IBaseTile = {
    val x: Int = getGridX - 1
    val y: Int = getGridY - 1
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  def northwest: IBaseTile = {
    val x: Int = getGridX - 1
    val y: Int = getGridY
    if (getWorld.isTileValid(x, y)) getWorld.getTileAt(x, y)
    else null
  }

  override def updateGUI() {
    super.updateGUI()
    val n = _gridElem.northCorner
    val w = _gridElem.westCorner
    val s = _gridElem.southCorner
    val e = _gridElem.eastCorner
    val poly = getBounds
    val vp = getWorld.getViewport

    val actn = new PointDef((n.getX * vp.getZoomFactor/* + vp.getShiftX */, n.getY * vp.getZoomFactor/* + vp.getShiftY*/))
    val actw = new PointDef((w.getX * vp.getZoomFactor/* + vp.getShiftX */, w.getY * vp.getZoomFactor/* + vp.getShiftY*/))
    val acts = new PointDef((s.getX * vp.getZoomFactor/* + vp.getShiftX */, s.getY * vp.getZoomFactor/* + vp.getShiftY*/))
    val acte = new PointDef((e.getX * vp.getZoomFactor/* + vp.getShiftX */, e.getY * vp.getZoomFactor/* + vp.getShiftY*/))

    poly.xpoints.update(0, actn.getRefX)
    poly.xpoints.update(1, actw.getRefX)
    poly.xpoints.update(2, acts.getRefX)
    poly.xpoints.update(3, acte.getRefX)

    poly.ypoints.update(0, actn.getRefY)
    poly.ypoints.update(1, actw.getRefY)
    poly.ypoints.update(2, acts.getRefY)
    poly.ypoints.update(3, acte.getRefY)

    poly.npoints = 4
    poly.invalidate()
  }

  override def gridCenter(): Point2D = {
    _gridElem.gridCenter()
  }
}

object BaseTile {
  /**
   * The default selection color (the color being drawn over the
   * tile when the mouse is pointing at the tile).
   */
  val selectColor = new Color(39, 38, 38, 161)
}

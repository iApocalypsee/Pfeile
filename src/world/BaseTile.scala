package world

import java.awt.event.MouseEvent
import java.awt.geom.Point2D
import java.awt.{Color, Graphics2D}

import comp.{Component, GUIUpdater}
import entity.AttackContainer
import geom.DoubleRef.DoubleRef2Int
import geom.PointDef
import gui.AdjustableDrawing
import misc.metadata.OverrideMetadatable
import player.weapon.{AttackEvent, AttackQueue}
import world.brush.HeightBrush
import world.tile.TileCage

/**
 *
 * @author Josip Palavra
 * @version 31.05.2014
 */
abstract class BaseTile protected[world](private[world] var _gridElem: GridElement) extends
  Component with IBaseTile with AdjustableDrawing with OverrideMetadatable with GUIUpdater with
  AttackContainer {

  addMouseListener(new java.awt.event.MouseAdapter {
    override def mouseEntered(e: MouseEvent): Unit = {
      BaseTile.infoHeight = getTileHeight
    }
  })

  private def selectMethod(g: Graphics2D) {
    g setColor BaseTile.selectColor
    g fillPolygon getBounds
  }

  // ***** GEOMETRY RELEVANT VARIABLES AND VALUES ***** //

  private var lastZoomFactor = 1.0
  private var lastIsoHeight = WorldViewport.TILE_PX_Y_DIMENSION

  // ***** MOVEMENT VARIABLES ***** //

  /**
   * Represents how much movement points it takes with basic circumstances to
   * walk onto this type of field.
   */
  val requiredMovementPoints: Int

  // ***** OTHER VARIABLES AND VALUES ***** //

  // append an adjustable drawing
  // this handle only executes when the mouse is hovering over the tile
  private val selectDrawHandle = handle(selectMethod, () => isMouseFocused)

  override def getGridX: Int = _gridElem.gridX
  override def getGridY: Int = _gridElem.gridY
  override def getField: IField = ???
  override def getWorld: IWorld = _gridElem._world
  override def getCage: TileCage = ???
  override def getTileHeight: Int = getMetadata(HeightBrush.meta_key).asInstanceOf[Int]
  override def getRequiredMovementPoints = requiredMovementPoints

  // ***** DRAWING METHODS ***** //

  override def draw(g: Graphics2D): Unit = {
    //val g = gl.create().asInstanceOf[Graphics2D]
    // translate the whole matrix context, just like OpenGL's "glPushMatrix()" and "glPopMatrix()"
    //g.translate(getWorld.getViewport.getShiftX, getWorld.getViewport.getShiftY)
    // draw the base tile shape with the base color
    //g.rotate(getWorld.getViewport.getRotation)
    g setColor getColor
    g fillPolygon getBounds
    // draw all other individual stuff
    drawAll(g)
    drawLines(g)

    //g.dispose()
  }

  private def drawLines(g: Graphics2D): Unit = {
    g.setColor(Color.black)
    g.drawPolygon(getBounds)
  }

  private def checkGetTile(x: Int, y: Int): IBaseTile = {
    if(!getWorld.isTileValid(x, y)) null
    else getWorld.getTileAt(x, y)
  }

  // ***** DIRECTIONAL METHODS ***** //

  override def north: IBaseTile = checkGetTile(getGridX - 1, getGridY + 1)
  override def northeast: IBaseTile = checkGetTile(getGridX, getGridY + 1)
  override def east: IBaseTile = checkGetTile(getGridX + 1, getGridY + 1)
  override def southeast: IBaseTile = checkGetTile(getGridX + 1, getGridY)
  override def south: IBaseTile = checkGetTile(getGridX + 1, getGridY - 1)
  override def southwest: IBaseTile = checkGetTile(getGridX, getGridY - 1)
  override def west: IBaseTile = checkGetTile(getGridX - 1, getGridY - 1)
  override def northwest: IBaseTile = checkGetTile(getGridX - 1, getGridY)

  override def updateGUI() {
    super.updateGUI()
    val n = _gridElem.northCorner
    val w = _gridElem.westCorner
    val s = _gridElem.southCorner
    val e = _gridElem.eastCorner
    val poly = getBounds
    val vp = getWorld.getViewport

    val zoom = {
      if(vp.getZoomFactor == lastZoomFactor) 1
      else if(vp.getZoomFactor < lastZoomFactor) 0.9
      else 1.1
    }

    def calcActualRaster(p: PointDef): PointDef = {
      if(p.getMetadata(BaseTile.meta_link_check).asInstanceOf[java.lang.Boolean] == true) return p
      if(lastZoomFactor != vp.getZoomFactor) {
        p.setLocation(p.getX * zoom, p.getY * zoom)
      }
      /*
      if(lastIsoHeight != tih) {
        // take the diff first
        val diff = tih - lastIsoHeight
        // then calculate the new height
        p.setLocation(p.getX, p.getY + diff)
      }
      */

      p.setMetadata(BaseTile.meta_link_check, true.asInstanceOf[java.lang.Boolean])
      p
    }

    val actn = calcActualRaster(n)
    val actw = calcActualRaster(w)
    val acts = calcActualRaster(s)
    val acte = calcActualRaster(e)

    _gridElem.northCorner = actn
    _gridElem.westCorner = actw
    _gridElem.southCorner = acts
    _gridElem.eastCorner = acte

    poly.xpoints = new Array[Int](4)
    poly.ypoints = new Array[Int](4)

    poly.xpoints.update(0, actn.getRefX + vp.getShiftX)
    poly.xpoints.update(1, actw.getRefX + vp.getShiftX)
    poly.xpoints.update(2, acts.getRefX + vp.getShiftX)
    poly.xpoints.update(3, acte.getRefX + vp.getShiftX)

    poly.ypoints.update(0, actn.getRefY + vp.getShiftY)
    poly.ypoints.update(1, actw.getRefY + vp.getShiftY)
    poly.ypoints.update(2, acts.getRefY + vp.getShiftY)
    poly.ypoints.update(3, acte.getRefY + vp.getShiftY)

    poly.npoints = 4
    poly.invalidate()

    updateLastZoomFactor()
    updateLastIsoHeight()
  }

  private def updateLastZoomFactor() {
    lastZoomFactor = getWorld.getViewport.getZoomFactor
  }

  private[BaseTile] def updateLastIsoHeight() = {
    lastIsoHeight = getWorld.getViewport.tileIsoHeight
  }

  override def gridCenter(): Point2D = {
    _gridElem.gridCenter()
  }
}

object BaseTile {

  /**
   * Related to the updateGUI() method and for unapplying some metadata.
   * Irrelevant for you. <br>
   * Yes, you.
   */
  val meta_link_check = "pfeile.tile.geometry.linkEdited"
  /**
   * The default selection color (the color being drawn over the
   * tile when the mouse is pointing at the tile).
   */
  val selectColor = new Color(39, 38, 38, 161)

  // ***** INFOBOX VARIABLES ***** //
  var infoHeight: Int = 0

  implicit def attackEventtoAttackQueue(a: AttackEvent) = new AttackQueue(a)
}

package world.tile

import comp.{GUIUpdater, Component}
import world.{IWorld, IBaseTile, World, IField}
import gui.AdjustableDrawing
import java.awt.{Color, Graphics2D}
import entity.Entity
import scala.collection.mutable
import misc.metadata.OverrideMetadatable

/**
 * Scala implementation of the tile class.
 * The base tile needs to be subclassed in order to get specific "tile types" on the map
 * like grass tiles, sea tiles, and so on.
 * @author Josip Palavra
 */
trait BaseTile extends Component with IBaseTile with AdjustableDrawing with OverrideMetadatable with GUIUpdater {

  // append an adjustable drawing
  // this handle only executes when the mouse is hovering over the tile
  private val selectDrawHandle = handle(g => {
    g setColor BaseTile.selectColor
    g fillPolygon getBounds
  }, () => isMouseFocused)

  // The entities.
  private val _entities = new mutable.HashSet[Entity]
  private[world] var _gridX = -1
  private[world] var _gridY = -1
  private var _field: IField = null
  private[world] var _world: World = null
  private val cage = new TileCage(this)

  override def getGridX: Int = _gridX
  override def getGridY: Int = _gridY
  override def getField: IField = _field
  override def getWorld: IWorld = _world

  override def draw(g: Graphics2D): Unit = {
    // draw the base tile shape with the base color
    g setColor getColor
    g fillPolygon getBounds
    // draw all other individual stuff
    drawAll(g)
  }

  override def updateGUI() {
    val north = cage.north
    val east = cage.east
    val south = cage.south
    val west = cage.west

    val poly = getBounds
    poly.xpoints.update(0, west.getX.asInstanceOf[Int])
    poly.xpoints.update(1, south.getX.asInstanceOf[Int])
    poly.xpoints.update(2, east.getX.asInstanceOf[Int])
    poly.xpoints.update(3, north.getX.asInstanceOf[Int])

    poly.ypoints.update(0, west.getY.asInstanceOf[Int])
    poly.ypoints.update(1, south.getY.asInstanceOf[Int])
    poly.ypoints.update(2, east.getY.asInstanceOf[Int])
    poly.ypoints.update(3, north.getY.asInstanceOf[Int])
    poly.npoints = 4
    poly invalidate()
  }
}

object BaseTile {

  /**
   * The default selection color (the color being drawn over the
   * tile when the mouse is pointing at the tile).
   */
  val selectColor = new Color(39, 38, 38, 161)

}

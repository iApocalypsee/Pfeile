package world

import java.awt.event.{MouseAdapter, MouseEvent}
import java.awt.geom.Point2D
import java.awt.{Color, Font, Graphics2D, Point}
import javax.swing.SwingUtilities

import comp.{Component, TransformationEvent}
import general.Main
import general.property.{DynamicProperty, PropertyBase, StaticProperty}
import gui.AdjustableDrawing
import gui.image.TextureAtlas.AtlasPoint
import gui.screen.GameScreen
import newent.MovableEntity
import newent.pathfinding.Path
import world.interfaces.TileComponentLike

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * The component that is responsible for drawing the isometric shape of the tile.
  * Note that this component is not able to rotate! If you attempt a rotation, the component
  * will throw a UnsupportedOperationException, stating that rotation is not supported.
  */
class IsometricPolygonTileComponent(val isoTile: Tile) extends Component with AdjustableDrawing with TileComponentLike {

  import world.Tile._

  // Just make sure that the isometric tile is not being rotated.
  onTransformed += "IsometricPolygonTileComponent Rotation check" -> TransformationEvent.InvalidRotation

  //<editor-fold desc="Visual appearance">

  /**
    * The shape from which the component is going to retrieve the corner points.
    * The corner points are needed to draw border lines on seperate sides.
    */
  private val cornerPoints = new {

    var north: Point = null
    var east: Point = null
    var south: Point = null
    var west: Point = null

    def use(x: IsometricTileAbsoluteShape) = {
      north = x.north
      east = x.east
      south = x.south
      west = x.west
    }

  }

  /**
    * The shape of the tile.
    *
    * On setting this property, the component is trying to calculate the new corner points of the isometric shape.
    */
  val tileShape = new DynamicProperty(Tile.TileShape) {
    // DynSet called every time the property assumes a new value
    dynSet = { x => cornerRecalculation(x, getX, getY, getWidth, getHeight); x }
  }

  private def cornerRecalculation(relativeShape: IsometricTileRelativeShape, x: Int, y: Int, width: Int, height: Int): Unit = {
    val absoluteShape = relativeShape.construct(x, y, width, height)
    cornerPoints.use(absoluteShape)
  }

  onTransformed += { _ =>
    cornerRecalculation(tileShape.get, getX, getY, getWidth, getHeight)
  }

  /**
    * The color with which the tile object gets filled as the mouse points on it.
    */
  val mouseFocusColor = new StaticProperty(DefaultMouseFocusColor)

  //</editor-fold>

  //<editor-fold desc="Context-sensitive methods for controlling UI">

  /**
    * Prepares this component for `VisionStatus.Hidden`
    */
  override def adaptHidden(): Unit = {
    setVisible(false)
  }

  /**
    * Prepares this component for `VisionStatus.Revealed`
    */
  override def adaptRevealed(): Unit = {
    setVisible(true)
  }

  /**
    * Prepares this component for `VisionStatus.Visible`
    */
  override def adaptVisible(): Unit = {
    setVisible(true)
  }

  //</editor-fold>

  //<editor-fold desc="Texturing">

  lazy val texture = isoTile.textureAtlas.getTexture(isoTile.getGridX, isoTile.getGridY, getBounds, atlasImageCutPosition).get

  /**
    * For texture atlas: Determines where the upper left corner of the image cut shall be.
    *
    * @param atlasPoint The atlas point for which the cut should apply.
    * @return The cut point on the atlas image.
    */
  private def atlasImageCutPosition(atlasPoint: AtlasPoint) =
    new Point2D.Double(atlasPoint.x * TileHalfWidth + atlasPoint.y * TileHalfWidth,
      atlasPoint.x * TileHalfHeight - atlasPoint.y * TileHalfHeight)

  //</editor-fold>

  //<editor-fold desc="Initialization code">

  setBackingScreen(GameScreen.getInstance())
  setSourceShape(tileShape.get.construct(-TileHalfWidth / 2, -TileHalfHeight / 2, TileWidth, TileHeight).polygon)
  setName((isoTile.getGridX, isoTile.getGridY).toString())

  // Translate so that the tile fits into the grid again.
  getTransformation.translate(normalX, normalY)

  //</editor-fold>

  //<editor-fold desc="Visual path prediction">

  private[world] var predictedPath = Option.empty[Seq[Tile]]

  private def isTargetForPathPrediction = predictedPath.isDefined

  addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent) = if (e.getButton == MouseEvent.BUTTON3) {
      onGainedMoveTargetFocus()
    }

    override def mouseReleased(e: MouseEvent) = if (e.getButton == MouseEvent.BUTTON3) {
      onLostMoveTargetFocus()
    }

    override def mouseEntered(e: MouseEvent): Unit = if (SwingUtilities.isRightMouseButton(e)) {
      onGainedMoveTargetFocus()
    }

    override def mouseExited(e: MouseEvent): Unit = if (SwingUtilities.isRightMouseButton(e)) {
      onLostMoveTargetFocus()
    }
  })

  private def onLostMoveTargetFocus() = {
    GuiMovementPrediction.erasePath()
  }

  /**
    * Executes when the tile is selected by an entity for being its next movement target.
    */
  private def onGainedMoveTargetFocus() = {
    // The entity to calculate the path for
    val entity = Main.getContext.entitySelection.selectedEntity

    /**
      * Actual prediction logic. This code is not written inside the future or match statement
      * on purpose.
      *
      * @param x The entity to predict the path to this tile for.
      * @return Ditto.
      */
    def predictWith(x: MovableEntity): Option[Path] = {
      x.pathfinderLogic.findPath(x, isoTile.getGridX, isoTile.getGridY)
    }

    val prediction = Future {
      entity match {
        case entity: MovableEntity => predictWith(entity)
        case _ => throw new RuntimeException("Current entity selection is no MovableEntity")
      }
    }

    prediction.foreach { pathOption =>
      pathOption.map { path =>
        GuiMovementPrediction.replace(path, isoTile.terrain)
      } getOrElse {
        GuiMovementPrediction.erasePath()
      }
    }
  }

  // Only draw a prediction if the tile is actually being targeted
  postDraw { g =>
    if (isTargetForPathPrediction) {
      g.setStroke(MoveTargetStroke)
      g.setColor(MoveTargetColor)
      g.draw(getBounds)
    }
  }

  //</editor-fold>

  //<editor-fold desc="Entity movement">

  // When the user is clicking on the tile, the active player should move towards it.
  addMouseListener(new MouseAdapter {
    override def mouseReleased(e: MouseEvent): Unit = if (e.getButton == MouseEvent.BUTTON3) {
      Main.getContext.entitySelection.selectedEntity match {
        case move: MovableEntity => move.moveTowards(isoTile.getGridX, isoTile.getGridY)
        case _ => ???
      }
    }
  })

  // When the mouse moves over the tile, it should be marked with a grayish-style color.
  postDraw(drawMouseFocused, isMouseFocused)

  /**
    * Sub-draw method for drawing something when the mouse hovers over the tile.
    *
    * @param g Self-evident.
    */
  private def drawMouseFocused(g: Graphics2D): Unit = {
    g.setColor(mouseFocusColor.get)
    g.fill(getBounds)
  }

  //</editor-fold>

  //<editor-fold desc="Border line drawing">

  private def drawBorders(g: Graphics2D) = {
    def drawRoutine(colorProperty: PropertyBase[Color], begin: Point, end: Point) = colorProperty ifdef { color =>
      g.setStroke(isoTile.border.stroke.get)
      g.setColor(color)
      g.drawLine(begin.x, begin.y, end.x, end.y)
    }

    drawRoutine(isoTile.border.northWest, cornerPoints.west, cornerPoints.north)
    drawRoutine(isoTile.border.northEast, cornerPoints.north, cornerPoints.east)
    drawRoutine(isoTile.border.southWest, cornerPoints.west, cornerPoints.south)
    drawRoutine(isoTile.border.southEast, cornerPoints.south, cornerPoints.east)
  }

  //</editor-fold>

  //<editor-fold desc="Debug capabilities">

  /**
    * Properties related to drawing coordinates on top of the tiles.
    */
  private val coordinateDraw = new {

    /**
      * Flag for drawing the coordinates on top of the tiles.
      * If true, the coordinates will be drawn.
      */
    var isCoordinateDrawn = true

    /**
      * The font used for drawing the debug coordinates of the visible fields.
      */
    lazy val debugCoordinateFont = new Font(Font.MONOSPACED, Font.PLAIN, 10)

    /**
      * The text drawn on top of the tile when drawing coordinates.
      */
    def text = s"(${isoTile.getGridX}|${isoTile.getGridY})"

    /**
      * The text bounds of the tile coordinate string, calculated with the `debugCoordinateFont`.
      */
    lazy val textBounds = Component.getTextBounds(text, debugCoordinateFont)

  }

  private def drawCoordinates(g: Graphics2D) = if (coordinateDraw.isCoordinateDrawn) {
    g.setFont(coordinateDraw.debugCoordinateFont)
    g.setColor(Color.white)
    g.drawString(coordinateDraw.text, getX + getWidth / 2 - coordinateDraw.textBounds.width / 2, getY + getHeight / 2 + coordinateDraw.textBounds.height / 2)
  }

  //</editor-fold>

  /**
    * Calculates the normal x position for this tile, considering that the center of the (0|0) tile
    * is at screen coordinates (0|0).
    *
    * @return The x position of the upper left corner of this tile while considering that the map has not been moved
    *         at all.
    */
  def normalX = isoTile.getGridX * TileHalfWidth + isoTile.getGridY * TileHalfWidth

  /**
    * Calculates the normal y position for this tile, considering that the center of the (0|0) tile
    * is at screen coordinates (0|0).
    *
    * @return The y position of the upper left corner of this tile while considering that the map has not been moved
    *         at all.
    */
  def normalY = isoTile.getGridX * TileHalfHeight - isoTile.getGridY * TileHalfHeight

  /**
    * Sets the position of this tile component to the normal position, to which the given translation is added.
    *
    * @param leftCornerX How much x units to move this tile additionally.
    * @param leftCornerY How much y units to move this tile additionally.
    */
  def setPositionRelativeToMap(leftCornerX: Int, leftCornerY: Int): Unit = {
    setLocation(normalX + leftCornerX, normalY + leftCornerY)
  }

  override def draw(g: Graphics2D): Unit = {
    super.draw(g)
    g.setColor(Color.black)
    g.draw(getBounds)
    g.setColor(isoTile.color)
    g.drawImage(texture, getX, getY, getWidth, getHeight, null)
    drawBorders(g)
    drawCoordinates(g)
  }
}


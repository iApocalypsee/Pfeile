package comp

import java.awt.{Color, Graphics2D}

import gui.Screen

import scala.collection.mutable

/**
 *
 * @author Josip Palavra
 */
class Bar(x: Int, y: Int, width: Int, height: Int, back: Screen) extends Component(x, y, width, height, back) {

  private var _fillFactor = 1.0
  private var _endingWidth = (getWidth * _fillFactor).asInstanceOf[Int]
  private var _splits = mutable.MutableList[Bar.Split]()

  var majorSplitColor = Color.green

  split(0.0, majorSplitColor)

  def fillFactor = _fillFactor

  /** Sets the fill factor of the bar. The method does only accept values in between 0.0 and 1.0
    *
    * @param a The new fill factor.
    */
  def fillFactor_=(a: Double) = {
    require(a >= 0.0 && a <= 1.0)
    _fillFactor = a
    _endingWidth = (getWidth * fillFactor).asInstanceOf[Int]
  }

  /** Does the same thing as calling <code>split(at, withColor, false)</code>
    *
    * @param at Where to split the bar up.
    * @param withColor The color to use beyond the split point.
    */
  def split(at: Double, withColor: Color): Unit = split(at, withColor, relative = false)

  /** Splits the bar at the specified position into two colors.
    *
    * @param at Where to split it up. Measured in percent (unit for measuring here = 1)
    * @param withColor The color to use beyond the split point.
    * @param relative Does the split point behave relatively to the whole fill factor of the bar?
    */
  def split(at: Double, withColor: Color, relative: Boolean): Unit = {
    require(withColor ne null)
    val addition = new Bar.Split(at, withColor, relative)
    addition.startingWidth = (getWidth * addition.splitFactorPosition).asInstanceOf[Int]
    _splits += addition
  }

  override def draw(g: Graphics2D): Unit = {

    // The inner color that lies behind the fill status
    //g.setColor(getBorder.getInnerColor)
    //g.fillPolygon(getBounds)

    var cumulation = 0

    for(i <- 0 until _splits.size) {
      val nextOpt = _splits.get(i + 1)
      val current = _splits(i)

      val nextStart = if(nextOpt.isDefined) nextOpt.get.startingWidth else _endingWidth
      val drawWidth = nextStart - current.startingWidth

      g.setColor(current.color)
      g.fillRect(getX + cumulation, getY, drawWidth, getHeight)

      cumulation += drawWidth
    }

    // The outer color.
    g.setColor(getBorder.getOuterColor)
    g.drawPolygon(getBounds)

  }
}

object Bar {

  private class Split(private var _splitFactorPosition: Double, var color: Color, var relative: Boolean) {

    var startingWidth = 0

    def splitFactorPosition = _splitFactorPosition
    def splitFactorPosition_=(a: Double) = _splitFactorPosition = a

  }

}

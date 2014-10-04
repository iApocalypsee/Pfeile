package gui

import java.awt.{Color, Graphics2D}

import comp.{Label, Bar}
import player.Life

/** Class for drawing the UI components of the active player's life.
  *
  * We do not want to draw the user interface of every player to the screen.
  * The only relevant player for the machine is the active player, so I need him.
  *
  * The class just pulls the data from the underlying Life class and tries to display them nicely.
  *
  */
class LifeUI(var x: Int, var y: Int, private var _life: Life) extends Drawable {

  def life = _life
  def life_=(l: Life) = {
    require(l ne null)
    _life = l
  }

  // I assume GameScreen for the listener system.
  private val lifebar = new Bar(x, y, 160, 20, GameScreen.getInstance())

  private val percentLife = new Label(lifebar.getX + lifebar.getWidth - 37, lifebar.getY + lifebar.getHeight + 5,
    GameScreen.getInstance(), percentageLifeDetails)
  percentLife.getBorder.setNotAvailableColor(java.awt.Color.white)

  private val accurateLife = new Label(lifebar.getX, lifebar.getY + lifebar.getHeight + 5, GameScreen.getInstance(), accurateLifeDetails)
  accurateLife.getBorder.setNotAvailableColor(java.awt.Color.white)

  _life.onLifeChanged += { l =>
    lifebar.fillFactor = l.getNewLife / _life.getMaxLife
    if(lifebar.fillFactor < 0.25 && lifebar.majorSplitColor.ne(Color.red)) {
      lifebar.majorSplitColor = Color.red
      lifebar.split(0.0, Color.red)
    }
    percentLife setText percentageLifeDetails
    accurateLife setText accurateLifeDetails
  }

  override def draw(g: Graphics2D): Unit = {
    lifebar.draw(g)
    percentLife.draw(g)
    accurateLife.draw(g)
  }

  private def accurateLifeDetails = f"${life.getLife}%.1f / ${life.getMaxLife}%.1f"
  private def percentageLifeDetails = f"${life.getRelativeLife}%.1f${"%"}%s"
}

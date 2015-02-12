package gui

import comp.{Bar, Label}
import geom.functions.FunctionCollectionEasing
import gui.screen.GameScreen
import player.Life

import java.awt.{Color, Graphics2D}

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

  private val percentLife = new Label(lifebar.getX + lifebar.getWidth - 61, lifebar.getY + lifebar.getHeight + 5,
      GameScreen.getInstance(), percentageLifeDetails)
  percentLife.getBorder.setNotAvailableColor(Color.white)

  private val accurateLife = new Label(lifebar.getX + 5, lifebar.getY + lifebar.getHeight + 5, GameScreen.getInstance(), accurateLifeDetails)
  accurateLife.getBorder.setNotAvailableColor(Color.white)

  _life.onLifeChanged += { l =>
    lifebar.fillFactor = l.getNewLife / _life.getMaxLife

    // red: it is quadratic increased up to the middle and then the growth decreases quadratically with fillFactor: 0 -> 255
    // blue: it is "0" at 0 or max Life and the more it gets to a fillFactor of 0.5, the higher is the blue value
    // green: opposite of red. 255 -> 0


    lifebar.majorSplitColor = new Color(
       255 - FunctionCollectionEasing.quadratic_easing_inOut(255.0 * lifebar.fillFactor, 0.0, 255.0, 255.0).asInstanceOf[Int],
       FunctionCollectionEasing.quadratic_easing_inOut(255.0 * lifebar.fillFactor, 0.0, 255.0, 255.0).asInstanceOf[Int],
       (Math.sin(lifebar.fillFactor * Math.PI) * 32.0).asInstanceOf[Int],
       255)

    lifebar.split(0.0, lifebar.majorSplitColor)

    percentLife setText percentageLifeDetails
    accurateLife setText accurateLifeDetails
  }

  override def draw(g: Graphics2D): Unit = {
    lifebar.draw(g)
    percentLife.draw(g)
    accurateLife.draw(g)
  }

  private def accurateLifeDetails = f"${life.getLife.asInstanceOf[Int]} / ${life.getMaxLife.asInstanceOf[Int]}"
  private def percentageLifeDetails = f"${life.getRelativeLife}%.2f${"%"}%s"
}

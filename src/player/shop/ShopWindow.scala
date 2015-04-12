package player.shop

import java.awt.{Graphics2D, Insets}

import comp._
import general.Main
import gui.screen.GameScreen

import scala.beans.BeanProperty

/**
  * The main shop window through which every input is directed while shopping.
  */
class ShopWindow extends DisplayRepresentable {

  import player.shop.ShopWindow._

  @BeanProperty val parentComponent: Region = new Region(ShopWindow.x, ShopWindow.y, ShopWindow.Width, ShopWindow.Height, ShopWindow.BackingScreen) {

    override def draw(g: Graphics2D): Unit = {
      super.draw(g)
      window.draw(g)
    }

  }

  parentComponent.setName("parent: ShopWindow")

  @BeanProperty val window: InternalFrame = {
    val frame = new InternalFrame(0, 0, parentComponent.getWidth, parentComponent.getHeight, parentComponent.getBackingScreen)
    frame.setParent(parentComponent)
    frame.onClosed += { () =>
      parentComponent.setVisible(false)
    }
    frame
  }

  window.setName("frame: ShopWindow")

  @BeanProperty val articleComponents: Seq[Component] = {
    val components = articleComponentCollection(ShopCentral.articles)
    for(component <- components) window add component
    components
  }

  parentComponent setVisible false

  /**
    * Maps given articles to a collection of components that can be used to display the articles
    * in the shop window.
    */
  private def articleComponentCollection(articles: Seq[Article]): Seq[Component] = {
    def rowToTable(index: Int) = (index % ButtonRowCount, index / ButtonRowCount)
    for(i <- 0 until articles.size) yield ShopButton.create(rowToTable(i)._1, rowToTable(i)._2, articles(i), this)
  }

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent: Component = parentComponent

}

object ShopWindow {

  //<editor-fold desc='Window position and dimensions'>

  lazy val x = 150
  lazy val y = 200
  lazy val Width = Main.getWindowWidth - 2 * x
  lazy val Height = Main.getWindowHeight - 2 * y
  lazy val BackingScreen = GameScreen.getInstance()

  //</editor-fold>

  //<editor-fold desc='Button layout'>

  /**
    * The insets for the area in which the article buttons are being placed.
    */
  lazy val ButtonsInsetsInsideWindow = new Insets(20, 40, 20, 40)

  /**
    * How many buttons can fit in one row?
    */
  @transient private lazy val ButtonRowCount = 6

  //</editor-fold>

}

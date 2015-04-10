package player.shop

import java.awt.{Graphics2D, Insets}

import comp._
import general.Main
import gui.screen.GameScreen

/**
  * The main shop window through which every input is directed while shopping.
  */
class ShopWindow extends DisplayRepresentable {

  import player.shop.ShopWindow._

  val parentComponent: Region = new Region(ShopWindow.x, ShopWindow.y, ShopWindow.Width, ShopWindow.Height, ShopWindow.BackingScreen) {

    override def draw(g: Graphics2D): Unit = {
      super.draw(g)
      window.draw(g)
    }

  }

  parentComponent.setName("parent: ShopWindow")

  val window: InternalFrame = {
    val frame = new InternalFrame(0, 0, parentComponent.getWidth, parentComponent.getHeight, parentComponent.getBackingScreen)
    frame.setParent(parentComponent)
    frame.onClosed += { () =>
      parentComponent.setVisible(false)
    }
    frame
  }

  window.setName("frame: ShopWindow")

  val articleComponents: Seq[Component] = {
    val components = articleComponentCollection(ShopCentral.articles)
    for(component <- components) window add component
    components
  }

  def getWindow = window
  def getParentComponent = parentComponent
  def getArticleComponent = articleComponents

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
    * Fixed width of the article buttons.
    */
  lazy val ButtonsFixedWidth = 75

  /**
    * Fixed height of the article buttons.
    */
  lazy val ButtonsFixedHeight = 40

  /**
    * The insets for the area in which the article buttons are being placed.
    */
  lazy val ButtonsInsetsInsideWindow = new Insets(20, 40, 20, 40)

  /**
    * Insets between article buttons.
    */
  lazy val ButtonsInsetsBetween = new Insets(5, 25, 5, 25)

  /**
    * The space reserved for the article buttons.
    */
  @transient private lazy val ButtonsFreeSpace = Width - ButtonsInsetsInsideWindow.left - ButtonsInsetsInsideWindow.right

  /**
    * How many buttons can fit in one row?
    */
  @transient private lazy val ButtonRowCount = ButtonsFreeSpace / (ButtonsFixedWidth + ButtonsInsetsBetween.left + ButtonsInsetsBetween.right)

  //</editor-fold>

}

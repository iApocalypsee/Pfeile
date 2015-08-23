package player.shop

import java.awt.{Graphics2D, Insets}
import java.util

import comp._
import general.{ImmutableObjectManagerFacade, Main, ObjectManager}
import gui.screen.GameScreen

import scala.beans.BeanProperty
import scala.collection.JavaConversions

/**
  * The main shop window through which every input is directed while shopping.
  */
class ShopWindow(articles: Seq[Article], val representing: TraderLike) extends DisplayRepresentable {

  /**
    * Additional constructor for Java lists.
    * @param javaArticles The list of articles to display. Java-form.
    */
  def this(javaArticles: util.List[Article], representing: TraderLike) = this(JavaConversions.asScalaBuffer(javaArticles), representing)

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

  window.setName(FrameName)

  @volatile private var tempArticleComponents: Seq[Component] = null

  // Every time the window opens, the article buttons should be recomputed again.
  // Who knows if something has changed?
  // See VisualArticleAttributes.filterArticlesFunction(...)
  window.onOpened += { () =>
    parentComponent.setVisible(true)
    rebuildArticleComponents()
  }

  def articleComponents: Seq[Component] = {
    rebuildArticleComponents()
    tempArticleComponents
  }
  def getArticleComponents = articleComponents

  /**
    * Maps given articles to a collection of components that can be used to display the articles
    * in the shop window.
    */
  private def articleComponentCollection(articles: Seq[Article]): Seq[Component] = {

    // Returns a tuple with the x and y coordinates in the grid
    def rowToTable(index: Int) = (index % ButtonRowCount, index / ButtonRowCount)

    // What player to determine the button layout for?
    val forWho = Main.getContext.activePlayer

    val filteredArticles = articles.filter(VisualArticleAttributes.filterArticlesFunction(forWho))

    for (i <- filteredArticles.indices) yield {
      val (x_grid, y_grid) = rowToTable(i)
      ShopButton.create(x_grid, y_grid, filteredArticles(i), this)
    }
  }

  private def rebuildArticleComponents(): Unit = {
    clearOldArticleGUI()
    tempArticleComponents = articleComponentCollection(articles)
    for(component <- tempArticleComponents) {
      component.setName(articleComponentName(component.hashCode()))
      window.add(component)
    }
  }

  private def clearOldArticleGUI(): Unit = {
    if(tempArticleComponents != null) {
      for(component <- tempArticleComponents) component.unparent()
      tempArticleComponents = null
    }
  }

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent: Component = parentComponent

  parentComponent.setVisible(false)
  accessObjectManagement.manage(this)

}

object ShopWindow {

  //<editor-fold desc='ShopWindow object management.'>

  private val accessObjectManagement = new ObjectManager[ShopWindow]
  val objectManagement = new ImmutableObjectManagerFacade(accessObjectManagement)

  //</editor-fold>

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
  val ButtonsInsetsInsideWindow = new Insets(20, 40, 20, 40)

  /**
    * How many buttons can fit in one row?
    */
  @transient private val ButtonRowCount = 6

  //</editor-fold>

  //<editor-fold desc='Component names'>

  private val FrameName = "@[[ShopWindow]]: frame"
  private def articleComponentName(hash: Int) = s"@[[ShopWindow]]: articleComponent id=$hash"

  //</editor-fold>

}

package player.shop

import java.awt.{Graphics2D, Insets}
import java.util.concurrent.atomic.AtomicReference
import java.util.{List => JList}

import comp._
import general._
import gui.screen.GameScreen

import scala.beans.BeanProperty
import scala.collection.JavaConversions
import scala.collection.parallel.immutable.ParVector

/**
  * The main shop window through which every input is directed while shopping.
  */
class ShopWindow(articles: Seq[Article], val representing: TraderLike) extends DisplayRepresentable {

  /**
    * Additional constructor for Java lists.
    * @param javaArticles The list of articles to display. Java-form.
    */
  def this(javaArticles: JList[Article], representing: TraderLike) = this(JavaConversions.asScalaBuffer(javaArticles), representing)

  import player.shop.ShopWindow._

  //<editor-fold desc="Main components: region and frame">

  @BeanProperty val parentComponent = new Region(ShopWindow.x, ShopWindow.y, ShopWindow.Width, ShopWindow.Height, ShopWindow.BackingScreen) {

    override def draw(g: Graphics2D): Unit = {
      super.draw(g)
      window.drawChecked(g)
    }

  }

  parentComponent.setName("parent: ShopWindow")

  @BeanProperty val window: InternalFrame = {
    val frame = new InternalFrame(0, 0, parentComponent.getWidth, parentComponent.getHeight, parentComponent.getBackingScreen)
    frame.onClosed += { () =>
      parentComponent.setVisible(false)
    }
    frame.setParent(parentComponent)
    frame.setName(FrameName)
    frame
  }

  //</editor-fold>

  //<editor-fold desc="Events">

  /**
    * Called when the components representing the articles have been recomputed.
    */
  val onArticleComponentsRebuilt = Delegate.create[SwapEvent[Seq[Component]]]

  //</editor-fold>

  private val tempArticleComponents = new AtomicReference(new ParVector[Component])

  // Every time the window opens, the article buttons should be recomputed again.
  // Who knows if something has changed?
  // See VisualArticleAttributes.filterArticlesFunction(...)
  window.onOpened += { () =>
    parentComponent.setVisible(true)
    rebuildArticleComponents()
  }

  def articleComponents: Seq[Component] = tempArticleComponents.get().toVector
  def getArticleComponents: Seq[Component] = articleComponents

  /**
    * Maps given articles to a collection of components that can be used to display the articles
    * in the shop window.
    */
  private def articleComponentCollection(articles: Seq[Article]): Seq[ShopButton] = {

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

  def rebuildArticleComponents(): Unit = {
    val oldComponents = tempArticleComponents.get().toVector
    clearOldArticleGUI()
    val shopButtons = articleComponentCollection(articles)
    for(component <- shopButtons) {
      component.setName(articleComponentName(component))
    }
    val newComponents = ParVector(shopButtons:_*)
    tempArticleComponents.set(newComponents)
    onArticleComponentsRebuilt(SwapEvent(oldComponents, newComponents.toVector))
  }

  private def clearOldArticleGUI(): Unit = {
    if(tempArticleComponents.get().nonEmpty) {
      for(component <- tempArticleComponents.get()) {
        component.getBackingScreen.remove(component)
      }
      tempArticleComponents.set(ParVector.empty)
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

  override def toString = s"ShopWindow(tempArticleComponents=$tempArticleComponents, representing=$representing)"

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
  private def articleComponentName(component: ShopButton) = s"${component.article}"

  //</editor-fold>

}

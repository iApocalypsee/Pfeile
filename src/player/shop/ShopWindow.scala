package player.shop

import java.awt.{Graphics2D, Insets}
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import comp._
import general.JavaInterop._
import general._
import gui.screen.GameScreen
import player.shop.trader.TraderLike

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * A window with which the articles that are on sale can be reviewed.
  *
  * @constructor Constructs a new window displaying the buyable contents of given trader.
  */
class ShopWindow(val represented: TraderLike) extends DisplayRepresentable {

  import player.shop.ShopWindow._

  private def articles = represented.articles.asScala

  /**
    * Subset of the components that are being held by the internal window.
    */
  private val m_articleComponents = mutable.ArrayBuffer.empty[ShopButton]

  def articleComponents: Seq[Component] = m_articleComponents.toSeq

  def getArticleComponents: IList[Component] = articleComponents.asJava.toImmutableList

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
    * Called when the components represented the articles have been recomputed.
    * The argument will be an immutable list of the new components that have been generated on this
    * generation pass.
    */
  val onArticleComponentsRebuilt = Delegate.create[IList[Component]]

  //</editor-fold>

  // <editor-fold desc="Initialization code">

  // Every time the window opens, the article buttons should be recomputed again.
  // Who knows if something has changed?
  // See VisualArticleAttributes.filterArticlesFunction(...)
  window.onOpened += { () =>
    parentComponent.setVisible(true)
    rebuildArticleComponents()
  }

  parentComponent.setVisible(false)
  accessObjectManagement.manage(this)

  // </editor-fold>

  // <editor-fold desc="Shop button generation">

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

  /**
    * Actual rebuilding of the GUI components used to display the articles to the caller.
    * In the process, every shop button will be destroyed and replaced by a new one.
    */
  def rebuildArticleComponents(): Unit = {
    val shopButtons = articleComponentCollection(articles)
    shopButtons.foreach(button => button.setName(articleComponentName(button)))

    clearOldArticleGUI()

    m_articleComponents ++= shopButtons

    // Since Java collections are invariant...
    onArticleComponentsRebuilt(shopButtons.map(_.asInstanceOf[Component]).asJava.toImmutableList)
  }

  private def clearOldArticleGUI(): Unit = {
    m_articleComponents.foreach(component => component.getBackingScreen.remove(component))
    m_articleComponents.clear()
  }

  // </editor-fold>

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    *
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent: Component = parentComponent

  override def toString = s"ShopWindow(articleComponents=$m_articleComponents, represented=$represented)"

}

object ShopWindow {

  //<editor-fold desc='ShopWindow object management.'>

  private val accessObjectManagement = new ObjectManager[ShopWindow]
  val objectManagement = new ImmutableObjectManagerFacade(accessObjectManagement)

  //</editor-fold>

  //<editor-fold desc='Window position and dimensions'>

  lazy val x = 150
  lazy val y = 200
  lazy val Width = GameWindow.WIDTH - 2 * x
  lazy val Height = GameWindow.HEIGHT - 2 * y
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

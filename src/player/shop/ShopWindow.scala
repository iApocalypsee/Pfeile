package player.shop

import java.awt.{Graphics2D, Insets, Point}

import comp._
import general.Main
import gui.screen.GameScreen

/**
  * The main shop window through which every input is directed while shopping.
  */
class ShopWindow extends DisplayRepresentable {

  import player.shop.ShopWindow._

  lazy val parentComponent: Region = new Region(ShopWindow.x, ShopWindow.y, ShopWindow.Width, ShopWindow.Height, ShopWindow.BackingScreen) {

    override def draw(g: Graphics2D): Unit = {
      super.draw(g)
      window.draw(g)
    }

  }

  lazy val window: InternalFrame = new InternalFrame(0, 0, parentComponent.getWidth, parentComponent.getHeight, parentComponent.getBackingScreen)
  lazy val articleComponents = articleComponentCollection(ShopCentral.articles)

  def getWindow = window
  def getParentComponent = parentComponent
  def getArticleComponent = articleComponents

  window setParent parentComponent
  window.onClosed += { () =>
    parentComponent.setVisible(false)
  }
  articleComponents foreach { x => window.add(x) }

  parentComponent setVisible false

  /**
    * Maps given articles to a collection of components that can be used to display the articles
    * in the shop window.
    */
  private def articleComponentCollection(articles: Seq[Article]): Seq[Component] = {

    // Some methods that can be adjusted for later use.
    // These are extracted from the implementation further below so that I can tweak behaviour later on.

    /**
      * Calculates the upper left point for the item at `row = rowIndex` and `column = articleColumnIndex`
      * @param rowIndex The row in which the given article is located.
      * @param articleColumnIndex The index of the article in the row (= column).
      * @return The position of the article button.
      */
    def calculatePosition(rowIndex: Int, articleColumnIndex: Int) = new Point(
      ButtonsInsetsInsideWindow.left + articleColumnIndex * (ButtonsFixedWidth + ButtonsInsetsBetween.left + ButtonsInsetsBetween.right),
      ButtonsInsetsInsideWindow.top + rowIndex * (ButtonsFixedHeight + ButtonsInsetsBetween.top + ButtonsInsetsBetween.bottom))

    /**
      * Constructs a string that is going to be the button representation for the given article.
      * @param article The article for which to construct a button string.
      * @return A string representation for the button.
      */
    def buttonText(article: Article): String = s"${article.item().getName}: ${article.price} Money Units"

    //////////////////// From this point: Implementation ////////////////////

    // TODO Buttons not iconified yet.

    val sortRanges = for (i <- 0 to articles.size / ShopWindow.ButtonRowCount) yield {
      val startIndex = i * ShopWindow.ButtonRowCount
      startIndex until startIndex + ShopWindow.ButtonRowCount
    }

    val sortedArticles = for (range <- sortRanges) yield range.collect(articles)

    // Collections are nested in this variable, as you can see from the specified type. A nested Seq.
    val nestedArticleComponents: Seq[Seq[Button]] = (0 until sortedArticles.size).map { rowIndex =>
      val rowArticles = sortedArticles(rowIndex)

      for (articleIndex <- 0 until rowArticles.size) yield {
        val article = rowArticles(articleIndex)

        val buttonPosition = calculatePosition(rowIndex, articleIndex)
        val button = new Button(buttonPosition.x, buttonPosition.y, ShopWindow.BackingScreen, buttonText(article))
        button.setWidth(ShopWindow.ButtonsFixedWidth)
        button.setHeight(ShopWindow.ButtonsFixedHeight)
        button
      }
    }

    // Here, the buttons are all in one seq, as opposed to the upper one.
    // Totally contrasting the upper seq, because the buttons are not scattered around in a seq.
    val flattenedArticleComponents: Seq[Button] = nestedArticleComponents.flatMap(identity)

    flattenedArticleComponents
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

  lazy val x = 20
  lazy val y = 20
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

package player.shop

import java.awt._

import _root_.geom.Vector2
import comp.{Component, _}
import general._

import scala.collection.mutable

/**
  * GUI representation of the shop button.
  * @param gridX The x position in the grid of listed shop buttons.
  * @param gridY The y position in the grid of listed shop buttons.
  * @param article The article to display.
  * @param shopWindow The shop window to which the shop button belongs.
  */
private[shop] class ShopButton private (gridX: Int, gridY: Int, article: Article, shopWindow: ShopWindow) extends Component(0, 0, 1, 1, shopWindow.component.getBackingScreen) {

  private[this] def w = ShopButton.Style.fixedWidth()
  private[this] def h = ShopButton.Style.fixedHeight()
  private[this] def wdiv2 = ShopButton.Style.fixedWidth() / 2
  private[this] def hdiv2 = ShopButton.Style.fixedHeight() / 2

  setSourceShape(new Rectangle(-wdiv2, -hdiv2, w, h))

  private var imageDrawLocation: Point = null
  private var imageDrawDimension: Dimension = null
  private var textDrawLocation: Point = null
  private var textDrawDimension: Dimension = null
  private var text = ""

  private val cachedItem = article.cachedItem

  private def positionalsUpdate(moveDiff: Vector2): Unit = {
    if (imageDrawLocation != null && textDrawLocation != null) {
      imageDrawLocation.x += moveDiff.x.asInstanceOf[Int]
      imageDrawLocation.y += moveDiff.y.asInstanceOf[Int]
      textDrawLocation.x += moveDiff.x.asInstanceOf[Int]
      textDrawLocation.y += moveDiff.y.asInstanceOf[Int]
    }
  }

  onMoved += positionalsUpdate
  recalculateStyle()

  private def recalculateStyle(): Unit = recalculateStyleWithArgs(gridX, gridY)

  private def constructText(article: Article) = s"${cachedItem.getName}: ${article.price} Money Units"

  private[this] def recalculateStyleWithArgs(gridX: Int, gridY: Int): Unit = {

    val text = article.item().getName
    val buttonStyle = ShopButton.Style
    val xInset = shopWindow.getWindow.getX
    val yInset = shopWindow.getWindow.getY

    val rawX = gridX * (buttonStyle.fixedWidth() + buttonStyle.insetsBetweenEach().left + buttonStyle.insetsBetweenEach().right)
    val rawY = gridY * (buttonStyle.fixedHeight() + buttonStyle.insetsBetweenEach().top + buttonStyle.insetsBetweenEach().bottom)
    val position = Vector2(xInset + rawX, yInset + rawY)

    //setLocation(position.x.asInstanceOf[Int], position.y.asInstanceOf[Int])
    setLocation(rawX, rawY)

    val absolutePosition = Vector2(getX, getY)

    val imageSize = buttonStyle.imageSize
    val rawImagePosition = buttonStyle.imagePosition
    val imagePosition = rawImagePosition + absolutePosition

    imageDrawDimension = imageSize.toDimension
    imageDrawLocation = imagePosition.toPoint

    val font = buttonStyle.font()
    val textBounds = Component.getTextBounds(text, font)
    val textOrientation = buttonStyle.textOrientation()

    val rawTextPosition = Vector2(buttonStyle.textLeftInset() + textOrientation.horizontal.apply(textBounds.getWidth.asInstanceOf[Int], imageSize.x.asInstanceOf[Int]),
      buttonStyle.textTopInset() + buttonStyle.imageInsets().top + textBounds.height + imageSize.y + textOrientation.vertical.apply(textBounds.height, buttonStyle.textGridCellHeight()))

    val textPosition = rawTextPosition + absolutePosition

    textDrawLocation = textPosition.toPoint
    textDrawDimension = textBounds

    this.text = constructText(article)

  }

  override def draw(g: Graphics2D): Unit = {
    getBorder.draw(g)

    g.setColor(article.shopButtonAttributes.textColor())
    g.drawString(text, textDrawLocation.x, textDrawLocation.y)

    g.drawImage(cachedItem.getImage, imageDrawLocation.x, imageDrawLocation.y, imageDrawDimension.width, imageDrawDimension.height, null)
  }
}

private[shop] object ShopButton {

  object Style {

    //<editor-fold desc='Property initialization (not important)'>

    /**
      * The setter function that every property in the [[player.shop.ShopButton.Style]] object.
      * @tparam A The datatype the setter is expecting.
      * @return A setter with side effects special to the ShopButtons.
      */
    private def commonSetSideEffect[A] = identityWith[A] { () =>
      issueRecalculation()
    }

    /**
      * Creates a property with the common setter described above.
      * @param startValue The start value of the property.
      * @tparam A The datatype of the property
      * @return A property with [[player.shop.ShopButton.Style$#commonSetSideEffect()]] applied.
      */
    private def commonProperty[A](startValue: A) = {
      val prop = Property(startValue)
      prop.setter = commonSetSideEffect
      prop
    }

    //</editor-fold>

    //<editor-fold desc='In relation to the button itself'>

    val fixedWidth = commonProperty(75)

    val fixedHeight = commonProperty(100)

    val imageInsets = commonProperty(new Insets(10, 10, 10, 10))

    val textLeftInset = commonProperty(10)

    val textTopInset = commonProperty(0)

    val textGridCellHeight = commonProperty(15)

    val textOrientation = commonProperty(new Orientation(HorizontalOrientation.Centered, VerticalOrientation.Top))

    //</editor-fold>

    //<editor-fold desc='Common values'>

    val font = commonProperty(Component.STD_FONT)

    val insetsBetweenEach = commonProperty(new Insets(5, 150, 5, 150))

    /**
      * Calculates the size of the image contained by the shop button and returns it
      * as a Vector2.
      * @return A vector representing the image size inside the button.
      */
    def imageSize = Vector2(fixedWidth - imageInsets.left - imageInsets.right,
      fixedHeight - imageInsets.top - imageInsets.bottom - textGridCellHeight - textTopInset)

    /**
      * Returns the position of the image relative to the top left corner of the button.
      * @return the position of the image relative to the top left corner of the button.
      */
    def imagePosition = Vector2(imageInsets.left, imageInsets.top)

    //</editor-fold>

  }

  //<editor-fold desc='Shop button object management'>

  private val buttonBuffer = mutable.MutableList[ShopButton]()

  def create(x: Int, y: Int, article: Article, shopWindow: ShopWindow): ShopButton = {
    val button = new ShopButton(x, y, article, shopWindow)
    buttonBuffer += button
    button
  }

  private def issueRecalculation(): Unit = {
    for (button <- buttonBuffer) button.recalculateStyle()
    LogFacility.log("Recalculated styles of every shop button", "Debug")
  }

  //</editor-fold>

}

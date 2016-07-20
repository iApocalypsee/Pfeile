package player.shop

import java.awt._

import _root_.geom.Vector
import comp.{Component, _}
import general.JavaInterop.JavaPrimitives.JavaInt
import general._
import general.property.DynamicProperty

import scala.collection.mutable

/**
  * GUI representation of the shop button.
  *
  * @param gridX The x position in the grid of listed shop buttons.
  * @param gridY The y position in the grid of listed shop buttons.
  * @param article The article to display.
  * @param shopWindow The shop window to which the shop button belongs.
  */
private[shop] class ShopButton private (gridX: Int, gridY: Int, val article: Article, shopWindow: ShopWindow)
  extends Component(0, 0, 1, 1, shopWindow.component.getBackingScreen) {

  private[this] def w = ShopButton.Style.fixedWidth()
	private[this] def h = ShopButton.Style.fixedHeight()
	private[this] def wdiv2 = ShopButton.Style.fixedWidth() / 2
	private[this] def hdiv2 = ShopButton.Style.fixedHeight() / 2

  setSourceShape(new Rectangle(-wdiv2, -hdiv2, w, h))
  setParent(shopWindow.window)

  private var imageDrawLocation = new Point(0, 0)
  private var imageDrawDimension = new Dimension(0, 0)

  private val cachedItem = article.cachedItem

  private val textLabel = new comp.Label(0, 0, getBackingScreen, constructText(article))
  textLabel.setParent(this)

  private def positionalsUpdate(t: TranslationChange): Unit = {
    val movPt = t.delta
    if(imageDrawLocation != null) {
      imageDrawLocation.setLocation(imageDrawLocation.x + movPt.getX.asInstanceOf[Int], imageDrawLocation.y + movPt.getY.asInstanceOf[Int])
    }
  }

  getTransformation.onTranslated += positionalsUpdate _
  recalculateStyle()

  private def recalculateStyle(): Unit = recalculateStyleWithArgs(gridX, gridY)

  private def constructText(article: Article) = Main.tr("shopPrice", cachedItem.getNameDisplayed,
    article.price.asInstanceOf[JavaInt])

  private[this] def recalculateStyleWithArgs(gridX: Int, gridY: Int): Unit = {

    val text = article.item().getNameDisplayed
    val buttonStyle = ShopButton.Style
    val xInset = shopWindow.getWindow.getX
    val yInset = shopWindow.getWindow.getY

    val rawX = gridX * (buttonStyle.fixedWidth() + buttonStyle.insetsBetweenEach().left + buttonStyle.insetsBetweenEach().right) + ShopWindow.ButtonsInsetsInsideWindow.left
    val rawY = gridY * (buttonStyle.fixedHeight() + buttonStyle.insetsBetweenEach().top + buttonStyle.insetsBetweenEach().bottom) + ShopWindow.ButtonsInsetsInsideWindow.top

    setRelativeLocation(rawX, rawY)

    val absolutePosition = new Vector(getX, getY)

    val imageSize = buttonStyle.imageSize
    val rawImagePosition = buttonStyle.imagePosition
    val imagePosition = rawImagePosition + absolutePosition

    imageDrawDimension = new Dimension(imageSize.getX.asInstanceOf[Int], imageSize.getY.asInstanceOf[Int])
    imageDrawLocation = new Point(imagePosition.getX.asInstanceOf[Int], imagePosition.getY.asInstanceOf[Int])

    val font = buttonStyle.font()
    val textBounds = Component.getTextBounds(text, font)
    val textOrientation = buttonStyle.textOrientation()

    val rawTextPosition = new Vector(buttonStyle.textLeftInset() + textOrientation.horizontal.apply(textBounds.getWidth.asInstanceOf[Int], imageSize.getX.asInstanceOf[Int]),
      buttonStyle.textTopInset() + buttonStyle.imageInsets().top + textBounds.height + imageSize.getY.asInstanceOf[Int] + textOrientation.vertical.apply(textBounds.height, buttonStyle.textGridCellHeight()))

    val textPosition = rawTextPosition + absolutePosition

    textLabel.setLocation(textPosition.getX.asInstanceOf[Int], textPosition.getY.asInstanceOf[Int])

  }

  override def draw(g: Graphics2D): Unit = {
    getBorder.draw(g)

    textLabel.setFontColor(article.shopButtonAttributes.textColor.get)
    textLabel.draw(g)

    g.drawImage(cachedItem.getImage, imageDrawLocation.x, imageDrawLocation.y, imageDrawDimension.width, imageDrawDimension.height, null)
  }
}

private[shop] object ShopButton {

  object Style {

    // <editor-fold desc="Property initialization (not important)">

    /**
      * The setter function that every property in the [[player.shop.ShopButton.Style]] object.
      *
      * @tparam A The datatype the setter is expecting.
      * @return A setter with side effects special to the ShopButtons.
      */
    private def commonSetSideEffect[A] = { (x: A) =>
      issueRecalculation()
      x
    }

    /**
      * Creates a property with the common setter described above.
      *
      * @param startValue The start value of the property.
      * @tparam A The datatype of the property
      * @return A property with [[player.shop.ShopButton.Style$#commonSetSideEffect()]] applied.
      */
    private def commonProperty[A](startValue: A) = {
      val prop = new DynamicProperty(startValue) {
        dynSet = commonSetSideEffect[A]
      }
      prop
    }

    // </editor-fold>

    // <editor-fold desc="In relation to the button itself">

    val fixedWidth = commonProperty(75)

    val fixedHeight = commonProperty(100)

    val imageInsets = commonProperty(new Insets(10, 10, 10, 10))

    val textLeftInset = commonProperty(10)

    val textTopInset = commonProperty(0)

    val textGridCellHeight = commonProperty(15)

    val textOrientation = commonProperty(new Orientation(HorizontalOrientation.Centered, VerticalOrientation.Top))

    // </editor-fold>

    // <editor-fold desc="Common values">

    val font = commonProperty(Component.STD_FONT)

    val insetsBetweenEach = commonProperty(new Insets(5, 35, 5, 35))

    /**
      * Calculates the size of the image contained by the shop button and returns it
      * as a Vector2.
      *
      * @return A vector represented the image size inside the button.
      */
    def imageSize = new Vector(fixedWidth - imageInsets.left - imageInsets.right,
      fixedHeight - imageInsets.top - imageInsets.bottom - textGridCellHeight - textTopInset)

    /**
      * Returns the position of the image relative to the top left corner of the button.
      *
      * @return the position of the image relative to the top left corner of the button.
      */
    def imagePosition = new Vector(imageInsets.left, imageInsets.top)

    // </editor-fold>

  }

  // <editor-fold desc="Shop button object management">

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

  // </editor-fold>

}

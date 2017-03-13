package player.shop

import java.awt._

import _root_.geom.Vector
import comp.{Component, _}
import general.JavaInterop._
import general._
import general.property.StaticProperty

import scala.collection.mutable

/**
  * GUI representation of the shop button.
  *
  * Internally, the shop button stores a cached instance of the item that is represented by the article.
  *
  * @param initGridX  The x position in the grid of listed shop buttons.
  * @param initGridY  The y position in the grid of listed shop buttons.
  * @param article    The article to display.
  * @param shopWindow The shop window to which the shop button belongs.
  */
private[shop] class ShopButton private(initGridX: Int, initGridY: Int, val article: Article, private val shopWindow:
ShopWindow) extends Component {

  private[this] def w = ShopButton.Style.fixedWidth()

  private[this] def h = ShopButton.Style.fixedHeight()

  setBackingScreen(shopWindow.component.getBackingScreen)
  setSourceShape(new Rectangle(-(w / 2), -(h / 2), w, h))
  setParent(shopWindow.window)

  private var imageDrawLocation = new Point(0, 0)
  private var imageDrawDimension = new Dimension(0, 0)

  /**
    * The label containing the description of the article itself.
    */
  private val textLabel = new comp.Label(0, 0, getBackingScreen, article.shopText)

  private val cachedItem = article.cachedItem

  textLabel.setParent(this)

  getTransformation.onTranslated.register(s"${this} onTranslated") { translation =>
    val moveVec = translation.delta
    val x = imageDrawLocation.x + moveVec.getX.asInstanceOf[Int]
    val y = imageDrawLocation.y + moveVec.getY.asInstanceOf[Int]
    imageDrawLocation.setLocation(x, y)
  }

  recalculateStyle()

  /**
    * Recalculates the dimensions of the article image and the location of
    * $ - the shop button itself
    * $ - the article image
    * $ - the article label.
    */
  private def recalculateStyle(): Unit = recalculateStyleWithArgs(initGridX, initGridY)

  /**
    * Recalculates the shop button location itself.
    * This is the only method where a shop window grid location must be provided. All other UI-related
    * calculations are based on what location this method calculates.
    */
  private def recalculateShopButtonLocation(newGridX: Int, newGridY: Int): Unit = {
    val rawX = newGridX * (ShopButton.Style.fixedWidth() + ShopButton.Style.insetsBetweenEach().left +
      ShopButton.Style.insetsBetweenEach().right) + ShopWindow.ButtonsInsetsInsideWindow.left
    val rawY = newGridY * (ShopButton.Style.fixedHeight() + ShopButton.Style.insetsBetweenEach().top +
      ShopButton.Style.insetsBetweenEach().bottom) + ShopWindow.ButtonsInsetsInsideWindow.top

    setRelativeLocation(rawX, rawY)
  }

  private def recalculateImageLocationAndDimension(imageSize: Vector, rawImagePosition: Vector): Unit = {
    imageDrawDimension = new Dimension(imageSize.getX.asInstanceOf[Int], imageSize.getY.asInstanceOf[Int])
    imageDrawLocation = new Point(
      (getX + rawImagePosition.getX).asInstanceOf[Int],
      (getY + rawImagePosition.getY).asInstanceOf[Int])
  }

  private def recalculateLabelPosition(imageSize: Vector, font: Font, textOrientation: Orientation): Unit = {
    val text = article.item().getNameDisplayed
    val textBounds = Component.getTextBounds(text, font)

    val rawTextPosition = new Vector(
      ShopButton.Style.textLeftInset() + textOrientation.horizontal.apply(textBounds.getWidth.asInstanceOf[Int],
        imageSize.getX.asInstanceOf[Int]),
      ShopButton.Style.textTopInset() + ShopButton.Style.imageInsets().top + ShopButton.Style.imageInsets().bottom +
        textBounds.height + imageSize.getY.asInstanceOf[Int] + textOrientation.vertical.apply(textBounds.height + 16,
        ShopButton.Style.textGridCellHeight()))

    val finalLabelPosition = rawTextPosition + new Vector(getX, getY)

    textLabel.setLocation(finalLabelPosition.getX.asInstanceOf[Int], finalLabelPosition.getY.asInstanceOf[Int])
  }

  /**
    * Recalculates the position and dimensions of this shop button in regards to given grid positions
    * in the shop window.
    *
    * The gridX and gridY parameters describe where the shop button should be located in the `ShopWindow` frame.
    */
  private[this] def recalculateStyleWithArgs(newGridX: Int, newGridY: Int): Unit = {
    recalculateShopButtonLocation(newGridX, newGridY)
    recalculateImageLocationAndDimension(ShopButton.Style.imageSize, ShopButton.Style.imagePosition)
    recalculateLabelPosition(ShopButton.Style.imageSize, ShopButton.Style.font, ShopButton.Style.textOrientation)
  }

  override def draw(g: Graphics2D): Unit = {
    getBorder.draw(g)

    textLabel.setFontColor(article.shopButtonAttributes.textColor.get)
    textLabel.draw(g)

    g.drawImage(cachedItem.getImage, imageDrawLocation.x, imageDrawLocation.y, imageDrawDimension.width,
      imageDrawDimension.height, null)
  }

  override def toString = s"ShopButton(article=$article, shopWindow=$shopWindow)"
}

private[shop] object ShopButton {

  object Style {

    // <editor-fold desc="Property initialization (not important)">

    /**
      * Creates a property with the common setter described above.
      *
      * @param startValue The start value of the property.
      * @tparam A The datatype of the property
      * @return A property with [[player.shop.ShopButton.Style#commonSetSideEffect()]] applied.
      */
    private def commonProperty[A](startValue: A): StaticProperty[A] = {
      val prop = new StaticProperty(startValue) {
        override def staticSetter(x: A): A = {
          issueRecalculation()
          x
        }
      }
      prop
    }

    // </editor-fold>

    // <editor-fold desc="In relation to the button itself">

    val fixedWidth: StaticProperty[Int] = commonProperty(75)

    val fixedHeight: StaticProperty[Int] = commonProperty(100)

    val imageInsets: StaticProperty[Insets] = commonProperty(new Insets(10, 10, 10, 10))

    val textLeftInset: StaticProperty[Int] = commonProperty(10)

    val textTopInset: StaticProperty[Int] = commonProperty(7)

    val textGridCellHeight: StaticProperty[Int] = commonProperty(15)

    val textOrientation: StaticProperty[Orientation] = commonProperty(
      new Orientation(HorizontalOrientation.Centered, VerticalOrientation.Top))

    // </editor-fold>

    // <editor-fold desc="Common values">

    val font: StaticProperty[Font] = commonProperty(Component.STD_FONT)

    val insetsBetweenEach: StaticProperty[Insets] = commonProperty(new Insets(10, 48, 10, 55))

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

  /**
    * Recalculates every shop button's look.
    */
  private def issueRecalculation(): Unit = {
    for (button <- buttonBuffer) button.recalculateStyle()
    LogFacility.log("Recalculated styles of every shop button", "Debug")
  }

  // </editor-fold>

}

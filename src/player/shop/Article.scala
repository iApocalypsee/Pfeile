package player.shop

import java.awt.Color
import java.util.Optional
import java.util.function.{Predicate, Supplier}

import general.JavaInterop.JavaPrimitives._
import general.JavaInterop._
import general.Main
import general.property.StaticProperty
import newent.{Entity, Player}
import player.item.Item

import scala.beans.BeanProperty

/**
  * Collects information essential to an article in the shop.
  *
  * This class basically wraps the construction of an item and provides additional information when an entity wants
  * to purchase the underlying item.
  * In this case, the trader checks if the customer has enough money, then constructs the item via given function
  * ([[player.shop.Article#item()]]) and hands the constructed item back to the customer, who is now obliged to pay
  * the specified price
  * in the Article object.
  *
  * @param item     The buyable item. Is a function because the item has to be reconstructed every time
  *                 an entity buys this article.
  * @param price    Ditto.
  * @param keywords Optional keywords with which this article can be found easier.
  *                 Keywords may be necessary to find the given item again in a list, a keyword could be the name of
  *                 the actual item.
  *                 I will rework the keyword array for transparent use.
  */
case class Article(private[shop] val item: Supplier[Item], price: Int, keywords: Array[String]) {

  def this(item: Supplier[Item], price: Int) = this(item, price, Array.empty)

  private[shop] def cachedItem = item()

  def name: String = cachedItem.getName

  def nameDisplayed: String = cachedItem.getNameDisplayed

  /**
    * The text that will be displayed in a shop for this article.
    * This string may incorporate information about the article such as the price, worth, usage, etc.
    */
  def shopText: String = Main.tr("shopPrice", cachedItem.getNameDisplayed, price.asInstanceOf[JavaInt])

  @BeanProperty lazy val shopButtonAttributes = new VisualArticleAttributes

  override def toString: String = "Article{" + cachedItem.getName + ", " + price + "}"

}

/*


 */

class VisualArticleAttributes private[shop] {

  /**
    * The color being used for drawing the name of the article in the shop button.
    */
  val textColor = new StaticProperty(Color.white)

  /**
    * Defines a function which can return a string describing why the given entity
    * cannot buy this article.
    * An entity may buy this article if the function returns an empty optional, and may not buy it if
    * the function returns a non-empty optional, stating the reason why the entity may not buy this article.
    *
    * In ShopWindow, if this article is not available (meaning this function returns a Some), the corresponding
    * shop button is grayed out, but still visible to the entity.
    */
  val notAvailableReason = new StaticProperty[java.util.function.Function[Entity, Optional[String]]](_ => Optional
    .empty[String])

  /**
    * Defines a function which can return a boolean describing if the given article should be seen
    * by the given player.
    * In practice, this only affects the ShopWindow GUI.
    */
  val isVisibleToEntity = new StaticProperty[Predicate[Player]](_ => true)

  /**
    * Returns true if this article is available for the given entity.
    *
    * @param forWho Explanatory.
    * @return A boolean value.
    * @see [[player.shop.VisualArticleAttributes#notAvailableReason()]]
    */
  def isAvailable(forWho: Entity): Boolean = !notAvailableReason(forWho).isPresent

}

object VisualArticleAttributes {

  /**
    * Returns a function that is used to determine whether the shop button correspondent to this article
    * is visible in the shop window.
    *
    * @param forWho For who to check.
    * @return A function for a filter call.
    */
  private[shop] def filterArticlesFunction(forWho: Entity) = (x: Article) => {
    val attribs = x.shopButtonAttributes
    attribs.isAvailable(forWho)
  }

}

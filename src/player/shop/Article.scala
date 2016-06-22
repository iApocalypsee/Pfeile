package player.shop

import java.awt.Color

import general.Property
import newent.{Entity, Player}
import player.item.Item

import scala.beans.BeanProperty
import scala.compat.java8.JFunction0

/**
  * Collects information essential to an article in the shop.
  *
  * This class basically wraps the construction of an item and provides additional information when an entity wants
  * to purchase the underlying item.
  * In this case, the trader checks if the customer has enough money, then constructs the item via given function
  * ([[player.shop.Article#item()]]) and hands the constructed item back to the customer, who is now obliged to pay the specified price
  * in the Article object.
  *
  * @param item The buyable item. Is a function because the item has to be reconstructed every time
  *             an entity buys this article.
  * @param price Ditto.
  * @param availableWhen (Old description, may be ignored) Function determining whether the article can be bought by the specified entity.
  *                      This function does not need to check whether this entity has enough money
  *                      to buy this article. This is considered to be done internally in the shop package.
  * @param visibleWhen (Old description, may be ignored) Function determining whether the article can be seen by the specified entity in the shop window.
  *                    This is not as important as the other parameters.
  * @param keywords Optional keywords with which this article can be found easier.
  *                 Keywords may be necessary to find the given item again in a list, a keyword could be the name of the actual item.
  *                 I will rework the keyword array for transparent use.
  */
case class Article(private[shop] val item: () => Item, price: Int, keywords: Seq[String] = Seq()) {

  /**
    * Additional constructor for Java interop.
    *
    * @param itemConstruction The function which constructs the actual item, Java fashion.
    * @param price The price to pay for one construction of specified item.
    * @param keywords Optional keywords to make it easier to find it in GUI or code.
    */
  def this(itemConstruction: JFunction0[Item], price: Int, keywords: Array[String]) = {
    this(itemConstruction.asInstanceOf[() => Item], price: Int, keywords)
  }

  /**
    * Additional constructor for Java interop.
    *
    * @param itemConstruction The function which constructs the actual item, Java fashion.
    * @param price The price to pay for one construction of specified item.
    */
  def this(itemConstruction: JFunction0[Item], price: Int) = this(itemConstruction, price, Array.empty[String])

  private[shop] def cachedItem = item()

  def name = cachedItem.getName

  private[shop] def toDefiniteArticle = DefiniteArticle(item(), price, keywords)

  @BeanProperty lazy val shopButtonAttributes = new VisualArticleAttributes

}

private case class DefiniteArticle(initializedItem: Item, price: Int, keywords: Seq[String])

class VisualArticleAttributes private[shop] {

  /**
    * The color being used for drawing the name of the article in the shop button.
    */
  val textColor = Property(Color.white)

  /**
    * Defines a function which can return a string describing why the given entity
    * cannot buy this article.
    * If this function returns a [[scala.Some]], then the article is assumed to be not buyable.
    * For Java, if you want to return [[scala.None]], use [[general.JavaInterop#scalaNone]].
    *
    * In ShopWindow, if this article is not available (meaning this function returns a Some), the corresponding
    * shop button is grayed out, but still visible to the entity.
    */
  val notAvailableReason: Property[Entity => Option[String]] = Property(_ => None)

  /**
    * Defines a function which can return a boolean describing if the given article should be seen
    * by the given player.
    * In practice, this only affects the ShopWindow GUI.
    */
  val isVisibleToEntity: Property[Player => Boolean] = Property(_ => true)

  /**
    * Returns true if this article is available for the given entity.
    *
    * @param forWho Explanatory.
    * @return A boolean value.
    * @see [[player.shop.VisualArticleAttributes#notAvailableReason()]]
    */
  def isAvailable(forWho: Entity): Boolean = notAvailableReason()(forWho).isEmpty

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

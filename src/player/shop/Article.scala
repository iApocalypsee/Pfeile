package player.shop

import java.awt.Color

import general.Property
import newent.{Entity, Player}
import player.item.Item

import scala.beans.BeanProperty

/**
  * Collects information essential to an article in the shop.
  * @param item The buyable item. Is a function because the item has to be reconstructed every time
  *             an entity buys this article.
  * @param price Ditto.
  * @param keywords Optional keywords with which this article can be found easier.
  */
case class Article(private[shop] val item: () => Item, price: Int, keywords: Seq[String] = Seq()) {

  private[shop] def cachedItem = item()

  // This constructor's only difference to the previous one is the last argument: it's an array,
  // for Java interop.
  def this(item: () => Item, price: Int, keywords: Array[String]) = this(item, price, keywords.toSeq)

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
   * @param forWho For who to check.
   * @return A function for a filter call.
   */
  private[shop] def filterArticlesFunction(forWho: Entity) = (x: Article) => {
    val attribs = x.shopButtonAttributes
    attribs.isAvailable(forWho)
  }

}

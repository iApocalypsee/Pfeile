package player.shop

import java.awt.Color

import general.Property
import player.item.Item

import scala.beans.BeanProperty

/**
  * Collects information essential to an article in the shop.
  * @param item The buyable item. Is a function because the item has to be reconstructed every time
  *             an entity buys this article.
  * @param price Ditto.
  * @param availableWhen Function determining whether the article can be bought by the specified entity.
  *                      This function does not need to check whether this entity has enough money
  *                      to buy this article. This is considered to be done internally in the shop package.
  * @param visibleWhen Function determining whether the article can be seen by the specified entity in the shop window.
  *                    This is not as important as the other parameters.
  * @param keywords Optional keywords with which this article can be found easier.
  */
case class Article(private[shop] val item: () => Item, price: Int, keywords: Seq[String] = Seq()) {

  private[shop] def cachedItem = item()

  // This constructor's only difference to the previous one is the last argument: it's an array,
  // for Java interop.
  def this(item: () => Item, price: Int, keywords: Array[String]) = this(item, price, keywords.toSeq)

  def name = cachedItem.getName

  @BeanProperty lazy val shopButtonAttributes = new VisualArticleAttributes

}

class VisualArticleAttributes private[shop] {

  /**
    * The color being used for drawing the name of the article in the shop button.
    */
  lazy val textColor = Property(Color.white)

}

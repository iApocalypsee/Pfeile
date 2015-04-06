package player.shop

import newent.Entity
import player.item.Item

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
case class Article(item: () => Item, price: Int, availableWhen: Entity => Boolean, visibleWhen: Entity => Boolean,
    keywords: Seq[String] = Seq()) {

  // This constructor's only difference to the previous one is the last argument: it's an array,
  // for Java interop.
  def this(item: () => Item, price: Int, availableWhen: Entity => Boolean, visibleWhen: Entity => Boolean,
    keywords: Array[String]) = this(item, price, availableWhen, visibleWhen, keywords.toSeq)

}

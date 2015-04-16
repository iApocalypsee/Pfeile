package player.shop

import java.util.{Map => JavaMap}

import newent.MoneyEarner

import scala.collection.JavaConversions

/**
 * Common trait for a seller of goods.
 */
trait Trader extends MoneyEarner with TraderLike {

  // A trader should not have the ability to earn money per turn.
  purse.moneyPerTurn = 0

  /**
   * Checks if the given article is in the trader's stock.
   * @param f The article predicate.
   * @return If the article predicate is at least once in the trader's stock, `true`.
   */
  override def isAvailable(f: Article => Boolean): Boolean = supply(f) > 0

  /**
   * Counts how many specified articles the trader has.
   * @param f The article predicate.
   * @return The count of the predicate.
   */
  def supply(f: Article => Boolean): Int = stock.foldLeft(0) { (count, keyValue) =>
    val (article, amount) = keyValue
    if (f(article)) count + amount else count
  }

  override def sell(to: MoneyEarner, article: Article => Boolean, amount: Int = 1): Boolean = {
    if (!isAvailable(article)) false
    else {
      val soldItems = retrieve(article)
      val paymentSuccessful = to.account.pay(soldItems./:(0) { (carryOver, article) => carryOver + article.price }, this)
      if (paymentSuccessful) {
        for(soldArticle <- soldItems) to.inventory.put(soldArticle.item())
        true
      }
      else false
    }
  }

  def articles: Seq[Article] = stock.keys.toSeq
  def stock: Map[Article, Int]
  def getStock: JavaMap[Article, Int] = JavaConversions.mapAsJavaMap(stock)

  /**
   * Removes `amount` articles from the trader to be put in a seq and returned altogether.
   * '''DO NOT FORGET TO REMOVE THEM ARTICLES.'''
   * @param article The article predicate.
   * @param amount How many articles to remove and put into the returned seq.
   * @return The seq of removed articles.
   */
  protected def retrieve(article: Article => Boolean, amount: Int = 1): Seq[Article]

}

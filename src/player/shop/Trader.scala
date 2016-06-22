package player.shop

import java.util.function.Predicate

import newent._
import world.World

import scala.compat.java8.FunctionConverters._

/**
  * Common trait for a seller of goods.
  *
  * This seller is an actual living entity on the map, meaning that he has an inventory and his own purse where he stores
  * his own money.
  * All subclasses of this class have a default money per turn of 0, meaning that they don't gain any money when
  * a turn/round/whenever the money is paid has been completed.
  */
abstract class Trader(world: World, spawnX: Int, spawnY: Int, name: String) extends Entity(world, spawnX, spawnY, name) with MoneyEarner with TraderLike {

  // A trader should not have the ability to earn money per turn.
  purse.moneyPerTurn = 0

  /**
    * Mapping for how much the trader has in stock of every article.
    *
    * Recall that the Article class is just a wrapper for the actual construction of an Item.
    *
    * @return The mapping of Article to the number of that article he possesses.
    */
  def stock: Map[Article, Int]

  /**
    * Counts how many specified articles the trader has.
    *
    * @param f The article predicate.
    * @return The count of the predicate.
    */
  def supply(f: Article => Boolean): Int = stock.foldLeft(0) { (count, keyValue) =>
    val (article, amount) = keyValue
    if (f(article)) count + amount else count
  }

  /**
    * @see [[player.shop.Trader#supply(java.util.function.Predicate)]]
    */
  def supply(f: Predicate[Article]): Int = supply(asScalaFromPredicate(f))

  /**
    * Removes `amount` articles from the trader to be put in a seq and returned altogether.
    * '''DO NOT FORGET TO REMOVE THE ARTICLES FROM THE UNDERLYING LIST IN THE IMPLEMENTATION.'''
    *
    * @param article The article predicate.
    * @param amount How many articles to remove and put into the returned seq.
    * @return The seq of removed articles.
    */
  protected def retrieve(article: Article => Boolean, amount: Int = 1): Seq[Article]

  /**
    * @see [[player.shop.Trader#retrieve(java.util.function.Predicate, int)]]
    */
  protected def retrieve(article: Predicate[Article], amount: Int): Seq[Article] = retrieve(asScalaFromPredicate(article), amount)

  // <editor-fold desc="Overrides">

  override def articles: Seq[Article] = stock.keys.toSeq

  override def sell(to: MoneyEarner, article: Article => Boolean, amount: Int = 1): Boolean = {
    if (!isAvailable(article)) false
    else {
      val soldItems = retrieve(article)
      val paymentSuccessful = to.account.pay(soldItems./:(0) { (carryOver, article) => carryOver + article.price }, this)
      if (paymentSuccessful) {
        for (soldArticle <- soldItems) to.inventory.put(soldArticle.item())
        true
      }
      else false
    }
  }

  /**
    * Checks if the given article is in the trader's stock.
    *
    * @param f The article predicate.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    */
  override def isAvailable(f: Article => Boolean): Boolean = supply(f) > 0

  // </editor-fold>

}

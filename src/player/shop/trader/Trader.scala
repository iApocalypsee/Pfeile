package player.shop.trader

import java.util.function.Predicate
import java.util.{Optional, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet}

import general.JavaInterop.JavaPrimitives._
import general.JavaInterop._
import newent._
import player.shop.Article
import world.World

import scala.collection.JavaConverters._

/**
  * Common trait for a seller of goods.
  *
  * This seller is an actual living entity on the map, meaning that he has an inventory and his own purse where he stores
  * his own money.
  * All subclasses of this class have a default money per turn of 0, meaning that they don't gain any money when
  * a turn/round/whenever the money is paid has been completed.
  */
abstract class Trader(world: World, spawnX: Int, spawnY: Int, name: String)
      extends Entity(world, spawnX, spawnY, name) with MoneyEarner with TraderLike with MovableEntity {

  // A trader should not have the ability to earn money per turn.
  purse.moneyPerTurn = 0

  /**
    * Mapping for how much the trader has in stock of every article.
    *
    * Recall that the Article class is just a wrapper for the actual construction of an Item.
    *
    * @return The mapping of Article to the number of that article he possesses.
    */
  def stock: IMap[Article, JavaInt]

  /**
    * Counts how many specified articles the trader has.
    *
    * @param f The article predicate.
    * @return The count of the predicate.
    */
  def supplies(f: Predicate[Article]): Int = stock.asScala.foldLeft(0) { (count, keyValue) =>
    val (article, amount) = keyValue
    if (f.test(article)) count + amount else count
  }

  /**
    * Retrieves one given article from the trader to be put in an optional.
    *
    * @param article The article that is to be queued.
    */
  protected def retrieve(article: Predicate[Article]): Optional[Article] = retrieve(article, 1).headOption

  /**
    * Abstraction method for receiving money.
    *
    * @param from        Who is paying the trader?
    * @param moneyAmount How much money to receive.
    */
  override def receive(from: MoneyEarner, moneyAmount: Int): Boolean = {
    require(moneyAmount > 0)
    if(!from.purse.spend(moneyAmount))
      return false
    else {
      this.purse.give(moneyAmount)
      return true
    }
  }

  /**
    * Removes `amount` articles from the trader to be put in a seq and returned altogether.
    * '''DO NOT FORGET TO REMOVE THE ARTICLES FROM THE UNDERLYING LIST IN THE IMPLEMENTATION.'''
    *
    * @param article The article predicate.
    * @param amount How many articles to remove and put into the returned seq.
    * @return The seq of removed articles.
    */
  protected def retrieve(article: Predicate[Article], amount: Int): IList[Article]

  // <editor-fold desc="Overrides">

  override def articles: IList[Article] = stock.keySet().toImmutableList

  override def sell(to: MoneyEarner, article: Predicate[Article], amount: Int): Boolean = {
    if (!isAvailable(article))
      false
    else {
      val soldItems = retrieve(article, amount).asScala
      val paymentSuccessful = to.account.pay(soldItems.foldLeft(0) { (carryOver, article) => carryOver + article.price }, this)
      if (paymentSuccessful) {
        for (soldArticle <- soldItems)
          to.inventory.put(soldArticle.item())
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
  override def isAvailable(f: Predicate[Article]): Boolean = supplies(f) > 0

  // </editor-fold>

}

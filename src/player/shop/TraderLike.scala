package player.shop
import newent.MoneyEarner

/**
  * The most basic trait for trader-like objects.
  * Contains only abstract method definitions.
  */
trait TraderLike {

  /**
    * Checks if the given article is in the trader's stock.
    * @param f The article predicate.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    */
  def isAvailable(f: Article => Boolean): Boolean

  /**
    * Sells the specified article to the given entity.
    * @param to The entity to sell to.
    * @param article The article to sell.
    * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
    * @return Was the transaction successful?
    */
  def sell(to: MoneyEarner, article: Article => Boolean, amount: Int = 1): Boolean

  /**
    * A listing of all articles that the trader can sell.
    * Note that the list should not have the same article twice in it.
    * @return A list of all articles that the trader can sell.
    */
  def articles: Seq[Article]

  /**
    * Abstraction method for receiving money.
    * @param from Who is paying?
    * @param moneyAmount The money to receive.
    */
  protected def receive(from: MoneyEarner, moneyAmount: Int): Unit

}

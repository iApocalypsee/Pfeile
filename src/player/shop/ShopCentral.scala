package player.shop

import java.awt.event.{MouseAdapter, MouseEvent}

import general.LogFacility
import newent.MoneyEarner
import player.item.Item

/**
  * Central object for code to interact with the shop system.
  */
object ShopCentral extends TraderLike {


  ShopWindow.objectManagement.applyOnEnter { window =>
    window.articleComponents.collect {
      case shopButton: ShopButton =>
        shopButton.addMouseListener(new MouseAdapter {
          override def mouseReleased(e: MouseEvent): Unit = {
            LogFacility.log("Selling mechanism not implemented yet...", "Error", "shop")
            ???
          }
        })
    }
  }


  def addArticle(item: () => Item, price: Int): Unit = addArticle(Article(item, price))
  def addArticle(article: Article): Unit = {
    ArticleCollection.addArticle(article)
  }

  /**
    * Finds all articles that fulfill a certain condition.
    * @param f Selector function.
    * @return All articles that comply to the selector function.
    */
  def find(f: (Article) => Boolean): Seq[Article] = ArticleCollection.find(f)

  /**
    * Finds a certain article.
    * @param article The article to look for.
    * @return Ditto.
    */
  def find(article: Article): Option[Article] = ArticleCollection.find(article)

  def articles: Seq[Article] = ArticleCollection.articles

  /**
    * Checks if the given article is in the trader's stock.
    * @param f The article predicate.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    */
  override def isAvailable(f: (Article) => Boolean): Boolean = articles.exists(f)

  /**
    * Sells the specified article to the given entity.
    * @param to The entity to sell to.
    * @param article The article to sell.
    * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
    * @return Was the transaction successful?
    */
  override def sell(to: MoneyEarner, article: (Article) => Boolean, amount: Int): Boolean = {

    //<editor-fold desc='Failure cases'>

    /**
     * Called when the client has not enough money to pay for queried items.
     * @return False, definitely.
     */
    def onNotSufficientClientMoney(): Boolean = {
      LogFacility.log(s"Client $to has not enough money for paying ${amount}x of $article", "Info", "shop")
      false
    }

    /**
     * Called when the given article does not exist in the shop central.
     * @return False, definitely.
     */
    def onNotAvailable(): Boolean = {
      LogFacility.log(s"No such article in [[ShopCentral]]: $article", "Info", "shop")
      false
    }

    //</editor-fold>

    /**
     * Called when given item is available.
     * @return Depends.
     */
    def onAvailable(): Boolean = {
      val sortedArticles = articles.sorted
      val queriedArticles = for (i <- 0 until amount) yield sortedArticles.find(article).get
      val totalTransactionValue = queriedArticles.foldRight(0) { _.price + _ }
      if (to.purse.numericValue < totalTransactionValue) onNotSufficientClientMoney()
      else onSuccess(totalTransactionValue, queriedArticles)
    }

    /**
     * Called when everything has been processed and nothing is in between the handshake anymore.
     * @return True, definitely.
     */
    def onSuccess(transactionValue: Int, articles: Seq[Article]): Boolean = {
      receive(to, amount)
      for(article <- articles) to.inventory.put(article.item())
      true
    }

    if (isAvailable(article)) onAvailable()
    else onNotAvailable()
  }

  /**
   * Abstraction method for receiving money.
   * @param from Who is paying?
   * @param moneyAmount The money to receive.
   */
  override protected def receive(from: MoneyEarner, moneyAmount: Int): Unit = {
    // Do nothing, the central shop is assumed to have no money pockets.
    // Infinitely rich maybe?
  }
}

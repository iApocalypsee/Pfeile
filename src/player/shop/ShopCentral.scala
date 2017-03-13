package player.shop

import java.awt.event.{MouseAdapter, MouseEvent}
import java.util.function._

import akka.actor.ActorDSL._
import akka.actor._
import general.JavaInterop.Implicits.actorSystem
import general.JavaInterop._
import general.{LogFacility, Main}
import general.JavaInterop.JavaAliases._
import newent.MoneyEarner
import player.item.Item
import player.shop.trader.TraderLike

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.compat.java8.FunctionConverters._

/**
  * Central shop for players.
  *
  * This shop is a construct separated from the other traders in the game; further, this shop is accessible at any
  * time from anywhere by every player and has no limits on how many items may be purchased.
  */
object ShopCentral extends TraderLike {

  def instance = this

  private val m_articles = mutable.Buffer.empty[Article]

  /**
    * Reference to the allocated worker.
    */
  @BeanProperty val asyncWorkerRef: ActorRef = actor(new AsyncWorker)

  // <editor-fold desc="Worker actor">

  /**
    * Taking care of shop central business asynchronously.
    * Right now I don't really care about code that does not exhibit Actor behavior; if
    * called code screws synchronization up, I won't be fixing it soon.
    * Data races? I don't care.
    * Deadlocks? Should be no problem.
    */
  private[ShopCentral] class AsyncWorker extends Actor {

    override def receive = {
      case AsyncWorkerMessages.Clicked(button)        => onShopButtonClicked(button)
      case AsyncWorkerMessages.GuiEstablished(window) => onShopWindowRegistered(window)
      case other                                      => unhandled(other)
    }

    /**
      * Called when a shop button has been clicked.
      *
      * @param button The shop button that has been clicked on.
      */
    private def onShopButtonClicked(button: ShopButton): Unit = {
      // Match the article of the button to the article in the shop central and sell it.
      ShopCentral.sell(Main.getContext.activePlayer, article => article == button.article)
    }

    /**
      * Called when a new shop window has been registered to the GUI system.
      * Given shop window will satisfy `window.represented == ShopCentral`.
      *
      * @param window The new shop window.
      */
    private def onShopWindowRegistered(window: ShopWindow): Unit = {

      // For every article component that is existing right now, register the listener.
      // The already computed components are not affected by the onArticleComponentRebuilt callback subscription
      subscribeShopButtonCallbacks(window.articleComponents.collect({ case s: ShopButton => s }))

      // For every article component that is computed later, register the listener as well
      window.onArticleComponentsRebuilt += { swap =>
        subscribeShopButtonCallbacks(swap.asScala.collect({ case s: ShopButton => s }))
      }

    }

  }

  /**
    * Collection of all messages to the async worker actor.
    */
  object AsyncWorkerMessages {

    /**
      * Sent to the worker when a `ShopButton` has been clicked.
      * @param button The button that has been clicked.
      */
    case class Clicked(button: ShopButton)

    /**
      * Sent for processing in [[player.shop.ShopCentral.AsyncWorker#onShopWindowRegistered(player.shop.ShopWindow)]]
      */
    case class GuiEstablished(window: ShopWindow)

  }

  // </editor-fold>

  // <editor-fold desc="ShopWindow representation and article component coordination">

  private def subscribeShopButtonCallbacks(s: Seq[ShopButton]): Unit = {
    for (button <- s) {
      button.addMouseListener(new MouseAdapter {
        override def mouseReleased(e: MouseEvent): Unit = {
          asyncWorkerRef ! AsyncWorkerMessages.Clicked(button)
        }
      })
    }
  }

  ShopWindow.objectManagement.applyOnEnter {
    // Only execute for that window if that window is represented the ShopCentral.
    case window if window.represented == this =>
      asyncWorkerRef ! AsyncWorkerMessages.GuiEstablished(window)
    case _ =>
  }

  // </editor-fold>

  def addArticle(item: Supplier[Item], price: Int): Unit = {
    m_articles += Article(item, price)
  }

  /**
    * Finds all articles that fulfill a certain condition.
    *
    * @param f Selector function.
    * @return All articles that comply to the selector function.
    */
  def filter(f: Predicate[Article]): IList[Article] = m_articles.filter(f.asScala).asJava

  def articles: IList[Article] = m_articles.asJava.toImmutableList

  /**
    * Checks if the given article is in the trader's stock.
    *
    * @param f The article predicate.
    * @return If the article predicate is at least once in the trader's stock, `true`.
    */
  override def isAvailable(f: Predicate[Article]): Boolean = articles.asScala.exists(f.asScala)

  /**
    * Sells the specified article to the given entity.
    *
    * @param to The entity to sell to.
    * @param article The article to sell.
    * @param amount How many articles to sell. Based on this value, the total's transaction value is calculated.
    * @return Was the transaction successful?
    */
  override def sell(to: MoneyEarner, article: Predicate[Article], amount: Int): Boolean = {

    //<editor-fold desc='Failure cases'>

    /**
      * Called when the client has not enough money to pay for queried items.
      *
      * @return False, definitely.
      */
    def onNotSufficientClientMoney(wishlist: Seq[Article]): Boolean = {
      LogFacility.log(s"Client $to has not enough money for paying ${amount}x of ${wishlist.map { article => article.nameDisplayed }}", "Info", "shop")
      false
    }

    /**
      * Called when the given article does not exist in the shop central.
      *
      * @return False, definitely.
      */
    def onNotAvailable(): Boolean = {
      LogFacility.log(s"No such article in [[ShopCentral]]: $article", "Info", "shop")
      false
    }

    //</editor-fold>

    /**
      * Called when given item is available.
      *
      * @return Depends.
      */
    def onAvailable(): Boolean = {
      val sortedArticles = articles.asScala.sorted
      val queriedArticles = for (i <- 0 until amount) yield sortedArticles.find(article.asScala).get
      val totalTransactionValue = queriedArticles.foldRight(0) { _.price + _ }
      if (to.purse.numericValue < totalTransactionValue) onNotSufficientClientMoney(queriedArticles)
      else onSuccess(totalTransactionValue, queriedArticles)
    }

    /**
      * Called when everything has been processed and nothing is in between the handshake anymore.
      *
      * @return True, definitely.
      */
    def onSuccess(transactionValue: Int, articles: Seq[Article]): Boolean = {
      receive(to, transactionValue)
      for (article <- articles) to.inventory.put(article.item())
      true
    }

    if (isAvailable(article)) onAvailable()
    else onNotAvailable()
  }

  /**
    * Abstraction method for receiving money.
    *
    * @param from Who is paying?
    * @param moneyAmount The money to receive.
    */
  override def receive(from: MoneyEarner, moneyAmount: Int): Boolean = {
    // Do nothing, the central shop is assumed to have no money pockets.
    // Infinitely rich maybe?
    from.purse.spend(moneyAmount)
  }
}

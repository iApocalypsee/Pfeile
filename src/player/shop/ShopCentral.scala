package player.shop

import player.item.Item

/**
  * Central object for code to interact with the shop system.
  */
object ShopCentral {

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
}

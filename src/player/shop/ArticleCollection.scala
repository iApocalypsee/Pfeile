package player.shop

import general.LogFacility

import scala.collection.mutable

/**
  * Object collecting all articles.
  */
private[shop] object ArticleCollection {

  private val buffer = emptyBuffer

  private def emptyBuffer = mutable.ArrayBuffer[Article]()

  def addArticle(article: Article): Unit = {
    buffer += article
  }

  /**
    * Finds all articles that fulfill a certain condition.
    * @param f Selector function.
    * @return All articles that comply to the selector function.
    */
  def find(f: Article => Boolean): Seq[Article] = buffer.filter(f)

  /**
    * Finds a certain article.
    * @param article The article to look for.
    * @return Ditto.
    */
  def find(article: Article): Option[Article] = {
    val filtered = find(_ == article)
    filtered.nonEmpty match {
      case true =>
        if (filtered.length > 1) LogFacility.log(s"Multiple articles for query {$article} found", "Warning")
        Some(filtered(0))
      case false => None
    }
  }

  def articles: Seq[Article] = buffer.toSeq

}

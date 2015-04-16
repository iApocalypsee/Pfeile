package player

/**
 * Created by jolecaric on 13/04/15.
 */
package object shop {

  implicit object ArticleOrdering extends Ordering[Article] {

    override def compare(x: Article, y: Article): Int = {
      x.name.compareTo(y.name)
    }
  }

}

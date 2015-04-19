import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

/**
 * Created by jolecaric on 08/04/15.
 */
package object general {

  def identityWith[A](f: A => Unit): A => A = { x =>
    f(x)
    x
  }

  def profile[A](code: => A): (A, FiniteDuration) = {
    val start = System.nanoTime()
    val eval = code
    val end = System.nanoTime()
    (eval, FiniteDuration(end - start, TimeUnit.NANOSECONDS))
  }

}

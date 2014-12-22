package general

import java.util.function
import java.util.function.{Predicate, Consumer, Supplier}

object JavaInterop {

  implicit def asScalaFunction[A](x: Supplier[A]): () => A = () => x.get()

  implicit def asScalaFunction[A](x: Consumer[A]): A => Unit = i => x.accept(i)

  implicit def asScalaFunction[A](x: Predicate[A]): A => Boolean = i => x.test(i)

  implicit def asScalaFunction[A, R](x: function.Function[A, R]): A => R = i => x(i)

  implicit def asJavaFunction[A, R](x: A => R): function.Function[A, R] = new function.Function[A, R] {
    override def apply(t: A) = x(t)
  }

}

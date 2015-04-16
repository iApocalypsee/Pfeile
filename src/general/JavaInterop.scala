package general

import java.util.function.{Consumer, Supplier}
import java.util.{Optional, function}

object JavaInterop {

  implicit def asScala[A](x: Supplier[A]): () => A = () => x.get()

  implicit def asScala[A](x: Consumer[A]): A => Unit = i => x.accept(i)

  /**
    * This method has a different name from the other `asScalaFunction` methods, because it would
    * cause ambiguity when using Java lambda notation.
    * This method converts a Java regular function to a Scala regular `Function1`
    * @param x The Java function to convert to a Scala function.
    * @tparam A The input type.
    * @tparam R The return type.
    * @return The converted scala `Function1`.
    */
  implicit def asScala[A, R](x: function.Function[A, R]): A => R = i => x(i)

  implicit def asJavaFun[A, R](x: A => R): function.Function[A, R] = new function.Function[A, R] {
    override def apply(t: A) = x(t)
  }

  implicit def asJava[R](x: () => R): function.Supplier[R] = new Supplier[R] {
    override def get(): R = x()
  }

  implicit def asJava[A](x: A => Unit): function.Consumer[A] = new function.Consumer[A] {
    override def accept(t: A): Unit = x(t)
  }

  def asJava[A](x: Option[A]): Optional[A] = x match {
    case Some(y) => Optional.of(y)
    case None => Optional.empty[A]()
  }

  def asScala[A](x: Optional[A]): Option[A] = if (x.isPresent) Some(x.get) else None

  def scalaNone = None

}

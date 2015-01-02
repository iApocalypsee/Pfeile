package general

import java.util.function
import java.util.function.{Predicate, Consumer, Supplier}

object JavaInterop {

  implicit def asScalaFunctionSupplier[A](x: Supplier[A]): () => A = () => x.get()

  implicit def asScalaFunctionConsumer[A](x: Consumer[A]): A => Unit = i => x.accept(i)

  implicit def asScalaFunctionPredicate[A](x: Predicate[A]): A => Boolean = i => x.test(i)

  implicit def asScalaFunction[A, R](x: function.Function[A, R]): A => R = i => x(i)

  implicit def asJavaFunction[A, R](x: A => R): function.Function[A, R] = new function.Function[A, R] {
    override def apply(t: A) = x(t)
  }

  implicit def asJavaFunction[R](x: () => R): function.Supplier[R] = new Supplier[R] {
    override def get(): R = x()
  }

  implicit def asJavaFunction[A](x: A => Unit): function.Consumer[A] = new function.Consumer[A] {
    override def accept(t: A): Unit = x(t)
  }

}

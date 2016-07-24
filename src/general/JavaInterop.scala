package general

import java.awt.geom.AffineTransform
import java.awt.{Graphics2D, Paint, Shape}
import java.util
import java.util.concurrent.Executors
import java.util.function._
import java.util.stream.{Collectors, Stream => IStream}
import java.util.{Collection => ICollection, Collections, Deque => IDeque, List => IList, Map => IMap, Set => ISet}

object JavaInterop {

  // <editor-fold desc="Scala wraps around Java constructs">

  object Implicits {
    implicit lazy val actorSystem = Main.getActorSystem
  }

  // Aliases for Java java.lang.Number derivatives
  object JavaPrimitives {
    type JavaInt = java.lang.Integer
    type JavaFloat = java.lang.Float
    type JavaDouble = java.lang.Double
    type JavaLong = java.lang.Long
    type JavaByte = java.lang.Byte
    type JavaShort = java.lang.Short
    type JavaChar = java.lang.Character
  }

  // <editor-fold desc="Collection and stream goodies">

  implicit class StreamOp[A](val stream: IStream[A]) extends AnyVal {
    def toList = stream.collect(Collectors.toList())
    def toSet = stream.collect(Collectors.toSet())
    def toImmutableList = Collections.unmodifiableList(toList)
    def toImmutableSet = Collections.unmodifiableSet(toSet)
  }

  implicit class ICollectionOp[A](val sub: ICollection[A]) extends AnyVal {

    private def listImpl: IList[A] = new util.ArrayList[A]()
    private def setImpl: ISet[A] = new util.HashSet[A]()
    private def dequeImpl: IDeque[A] = new util.ArrayDeque[A]()

    /**
      * Copies the contents of given collection to a new, mutable list.
      */
    def toList: IList[A] = {
      val newList = listImpl
      newList.addAll(sub)
      newList
    }

    /**
      * Copies the contents of given collection to a new, mutable set.
      */
    def toSet: ISet[A] = {
      val newSet = setImpl
      newSet.addAll(sub)
      newSet
    }

    /**
      * Copies the contents of given collection to a new, mutable deque.
      */
    def toDeque: IDeque[A] = {
      val newDeque = dequeImpl
      newDeque.addAll(sub)
      newDeque
    }

    def toImmutableList: IList[A] = sub match {
      case list: IList[A] => Collections.unmodifiableList(list)
      case _ => Collections.unmodifiableList(toList)
    }

    def toImmutableSet: ISet[A] = sub match {
      case set: ISet[A] => Collections.unmodifiableSet(set)
      case _ => Collections.unmodifiableSet(toSet)
    }

    def head = headOption.get()
    def headOption = sub.stream.findFirst()

  }

  // </editor-fold>

  // <editor-fold desc="Graphics goodies">

  implicit class Graphics2DOp(val g: Graphics2D) extends AnyVal {

    private def use[A, U](x: U, g: () => U, s: U => Unit)(f: => A): A = {
      require(x != null)
      val o = g()
      s(x)
      val r = f
      s(o)
      r
    }

    /**
      * Pushes a matrix on top of the "stack" of the Graphics2D object.
      * The matrix specified as `m` is available to given graphics context inside the given code block.
      *
      * @param m The matrix to be pushed on top of the stack.
      * @param f The code block to use the pushed matrix.
      * @tparam A The return type of the code block that uses the pushed matrix.
      * @return The return value of the code block.
      */
    def useMatrix[A](m: AffineTransform)(f: => A) = use(m, g.getTransform, g.setTransform)(f)

    def usePaint[A](p: Paint)(f: => A): A = use(p, g.getPaint, g.setPaint)(f)

    def useClip[A](s: Shape)(f: => A): A = use(s, g.getClip, g.setClip)(f)

  }

  // </editor-fold>

  // <editor-fold desc="Java function goodies">

  implicit class PredicateOp[A](val predicate: Predicate[A]) extends AnyVal {
    def apply(x: A) = predicate.test(x)
  }

  implicit class SupplierOp[A](val supplier: Supplier[A]) extends AnyVal {
    def apply() = supplier.get()
    def consume(f: A => Unit): VoidConsumer = () => f(supplier.get())
    def andThen[B](f: A => B): Supplier[B] = () => f(supplier.get())
  }

  implicit class ConsumerOp[A](val consumer: Consumer[A]) extends AnyVal {
    def apply(x: A) = consumer.accept(x)
  }

  // </editor-fold>

  // </editor-fold>

  def asCallable(x: Runnable) = Executors.callable(x)

  def scalaNone = None

}

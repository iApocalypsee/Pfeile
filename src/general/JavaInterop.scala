package general

import java.util.function.{BiConsumer, BiFunction, Consumer, Supplier}
import java.util.{Optional, function}

object JavaInterop {

  import java.lang

  //<editor-fold desc='Java <-> Scala parameter conversion'>

  trait ParamConversion[From, To] {
    def forward(from: From): To
    def backward(to: To): From
  }

  // Generic function providing a conversion to any type. Only some types require additional conversion.
  implicit def generalConversion[A, B]: ParamConversion[A, B] = new ParamConversion[A, B] {
    override def backward(to: B) = to.asInstanceOf[A]
    override def forward(from: A) = from.asInstanceOf[B]
  }

  implicit object IntConversion extends ParamConversion[Int, lang.Integer] {
    def get = this
    override def backward(to: Integer) = to.intValue()
    override def forward(from: Int) = new Integer(from)
  }

  implicit object FloatConversion extends ParamConversion[Float, lang.Float] {
    def get = this
    override def backward(to: lang.Float) = to.floatValue()
    override def forward(from: Float) = new lang.Float(from)
  }

  implicit object DoubleConversion extends ParamConversion[Double, lang.Double] {
    def get = this
    override def backward(to: lang.Double) = to.doubleValue()
    override def forward(from: Double) = new lang.Double(from)
  }

  implicit object LongConversion extends ParamConversion[Long, lang.Long] {
    def get = this
    override def backward(to: lang.Long) = to.longValue()
    override def forward(from: Long) = new lang.Long(from)
  }

  //</editor-fold>

  //<editor-fold desc='Java function <-> Scala function conversion layer'>

  trait FunctionalTypeClass {
    type UnderlyingType
  }

  trait CommonFunction1[-A, +R] extends FunctionalTypeClass with (A => R)

  trait CommonFunction0[+R] extends FunctionalTypeClass with (() => R)

  //</editor-fold>

  //<editor-fold desc='Generic Java-Scala conversion to "FunctionalTypeClass" type class'>

  implicit def scalaCommonFunction1[A, R](f: A => R): CommonFunction1[A, R] = new CommonFunction1[A, R] {
    type UnderlyingType = A => R
    override def apply(x: A) = f(x)
  }

  implicit def javaCommonFunction1[A, R](f: function.Function[A, R]): CommonFunction1[A, R] = new CommonFunction1[A, R] {
    type UnderlyingType = function.Function[A, R]
    override def apply(x: A) = f(x)
  }

  implicit def scalaCommonFunction0[R](f: () => R): CommonFunction0[R] = new CommonFunction0[R] {
    type UnderlyingType = () => R
    override def apply() = f()
  }

  implicit def javaCommonFunction0[R](f: Supplier[R]): CommonFunction0[R] = new CommonFunction0[R] {
    type UnderlyingType = Supplier[R]
    override def apply() = f.get()
  }

  implicit def javaCommonVoidFunction[A](f: Consumer[A]): CommonFunction1[A, Unit] = new CommonFunction1[A, Unit] {
    type UnderlyingType = Consumer[A]
    override def apply(x: A) = f.accept(x)
  }

  //</editor-fold>

  //<editor-fold desc='Java int <-> Scala Int'>

  implicit def javaIntRetToIntFun[R](f: function.Function[lang.Integer, R]): CommonFunction1[Int, R] = new CommonFunction1[Int, R] {
    override def apply(v1: Int) = f(v1)
    override type UnderlyingType = function.Function[lang.Integer, R]
  }

  implicit def javaIntIntToIntFun(f: function.Function[lang.Integer, lang.Integer]): CommonFunction1[Int, Int] = new CommonFunction1[Int, Int] {
    override def apply(v1: Int) = f(v1)
    override type UnderlyingType = function.Function[lang.Integer, lang.Integer]
  }

  implicit def javaParamIntToIntFun[A](f: function.Function[A, lang.Integer]): CommonFunction1[A, Int] = new CommonFunction1[A, Int] {
    override def apply(v1: A) = f(v1)
    override type UnderlyingType = function.Function[A, lang.Integer]
  }

  //</editor-fold>

  //<editor-fold desc='Java double <-> Scala Double'>

  implicit def javaDoubleRetToDoubleFun[R](f: function.Function[lang.Double, R]): CommonFunction1[Double, R] = new CommonFunction1[Double, R] {
    override def apply(v1: Double) = f(v1)
    override type UnderlyingType = function.Function[lang.Double, R]
  }

  implicit def javaDoubleDoubleToDoubleFun(f: function.Function[lang.Double, lang.Double]): CommonFunction1[Double, Double] = new CommonFunction1[Double, Double] {
    override def apply(v1: Double) = f(v1)
    override type UnderlyingType = function.Function[lang.Double, lang.Double]
  }

  implicit def javaParamDoubleToDoubleFun[A](f: function.Function[A, lang.Double]): CommonFunction1[A, Double] = new CommonFunction1[A, Double] {
    override def apply(v1: A) = f(v1)
    override type UnderlyingType = function.Function[A, lang.Double]
  }

  //</editor-fold>

  //<editor-fold desc='Direct function conversions'>

  //<editor-fold desc="Shorthand Java -> Scala conversions">

  def func[A](f: Supplier[A]) = asScala(f)
  def func[A](f: Consumer[A]) = asScala(f)
  def func[A, B](f: BiConsumer[A, B]) = asScala(f)
  def func[A, R](f: function.Function[A, R]) = asScala(f)
  def func[A, B, R](f: BiFunction[A, B, R]) = asScala(f)

  //</editor-fold>

  implicit def asScala[A](x: Supplier[A]): () => A = () => x.get()

  implicit def asScala[A](x: Consumer[A]): A => Unit = i => x.accept(i)

  implicit def asScala[A, B](f: BiConsumer[A, B]): (A, B) => Unit = (x, y) => f.accept(x, y)

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

  implicit def asScala[A, B, R](f: BiFunction[A, B, R]): (A, B) => R = (x, y) => f(x, y)

  implicit def asJavaFun[A, R](x: A => R): function.Function[A, R] = new function.Function[A, R] {
    override def apply(t: A) = x(t)
  }

  implicit def asJava[R](x: () => R): function.Supplier[R] = new Supplier[R] {
    override def get(): R = x()
  }

  implicit def asJava[A](x: A => Unit): function.Consumer[A] = new function.Consumer[A] {
    override def accept(t: A): Unit = x(t)
  }

  //</editor-fold>

  def asJava[A](x: Option[A]): Optional[A] = x match {
    case Some(y) => Optional.of(y)
    case None => Optional.empty[A]()
  }

  def asScala[A](x: Optional[A]): Option[A] = if (x.isPresent) Some(x.get) else None

  def scalaNone = None

}

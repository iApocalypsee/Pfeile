package general

import java.util.function._

/**
  *
  * @author Josip Palavra
  */
case class Property[A] private (private var value: Option[A]) {

  private var _getter: A => A = identity[A]

  /**
    * The setter function that is able to transform the input value or create some side-effects.
    * For rejecting the new input value, consider using [[general.Property#validation()]] instead.
    */
  private var _setter: A => A = identity[A]

  /**
    * A validator function which can return [[scala.None]] for a good value
    * or a [[scala.Some]] with a message why the value is not accepted.
    */
  private var _validation: A => Option[String] = x => None

  /**
    * A function which can provide lazy initialization defaults when
    */
  private var _lazyInit: () => A = failLazyInit

  private def failLazyInit = () => throw new ExceptionInInitializerError("No lazy init given or already used.")

  /**
    * Returns the underlying value directly.
    *
    * The underlying value is returned directly, bypassing control structures
    * provided by [[scala.Option]]. As a result, the method can throw [[scala.Option]]-specific
    * exceptions.
    *
    * @return The underlying value that the property holds.
    * @see [[scala.Option]]
    */
  def get: A = synchronized {
    option getOrElse {
      val lazyCompute = lazyInit()
      value = Some(lazyCompute)
      lazyInit = failLazyInit
      lazyCompute
    }
  }

  /**
    * Returns the option containing the possible value of the property.
    *
    * The important thing about the option is that the property __does not have
    * to have a value in it__, in contrast to [[general.Property# g e t]].
    *
    * @return The option holding the possible property value.
    */
  def option: Option[A] = value map _getter

  def getter = _getter
  def getGetter = JavaInterop.asJavaFun(getter)
  private def getter_=(x: A => A) = {
    require(x != null)
    _getter = x
  }

  def appendGetter(x: A => A): Property[A] = {
    getter_=(_getter andThen x)
    this
  }

  def appendGetter(javafun: Function[A, A]): Property[A] = {
    appendGetter(x => javafun(x))
    this
  }

  def setter = _setter
  def getSetter = JavaInterop.asJavaFun(setter)
  private def setter_=(x: A => A) = {
    require(x != null)
    _setter = x
  }

  def appendSetter(x: A => A): Property[A] = {
    setter_=(_setter andThen x)
    this
  }
  /*
  def appendSetter(javafun: Function[A, A]): Property[A] = {
    appendSetter(x => javafun(x))
    this
  }
  */

  def validation = _validation
  def validation_=(x: A => Option[String]): Unit = {
    require(x != null)
    _validation = x
  }

  def setValidation(x: Function[A, Option[String]]) = this.validation = in => x(in)

  def lazyInit = _lazyInit
  def lazyInit_=(x: () => A): Unit = {
    require(x != null)
    _lazyInit = x
  }

  def getLazyInit = JavaInterop.asJava(lazyInit)
  def setLazyInit(x: Supplier[A]): Unit = this.lazyInit = () => x.get()

  /**
    * Instructs this property to set itself to the value the other property gets every time the other
    * property is set to another value.
    *
    * So, every time the given property's value is changed through the [[general.Property#set(java.lang.Object)]] method,
    * this property sets its value to the new value of the other property as well.
    * ''This can be a potential source for very annoying bugs, use this with care. You have been warned.''
    *
    * @param another The other property to listen to.
    */
  def complyWith(another: Property[A]): Unit = {
    another appendSetter { x =>
      this set x
      x
    }
    LogFacility.log(s"Property $this complying now to $another")
  }

  /**
    * Does essentially the same as [[general.Property#complyWith(general.Property)]].
    *
    * But why the overhead and introduce a new operator for it?
    * In future Scala code that I am going to write, I want to see __clearly__ what properties
    * are listening to other ones. Operators/unknown signs draw attention, and that's the reason.
    * ''This can be a potential source for very annoying bugs. You have been warned.
    * Use this only in situations where you are 100% sure this is not going to fail entire algorithms.''
    *
    * @param another The other property to listen to.
    * @see [[general.Property#complyWith(general.Property)]]
    */
  def =<<=(another: Property[A]): Unit = complyWith(another)

  /**
    * Sets the underlying value to a new one.
    *
    * From the [[general.Property]] class there are no restrictions in setting
    * the underlying value, so every value is in theory permitted.
    *
    * Note that the delegates `preSet` and `postSet` can throw exceptions, however.
    *
    * @param x The new value of the property.
    */
  def set(x: A): Unit = synchronized {
    val validationCheck = _validation(x)
    require(validationCheck.isEmpty, s"Property validation failed: ${validationCheck.get}")
    val transformedValue = _setter(x)
    x match {
      case ref: AnyRef => require(ref eq transformedValue.asInstanceOf[AnyRef], "")
      case _ =>
    }
    value = Some(transformedValue)
  }

  @inline def filterNot(p: (A) => Boolean) = value.filterNot(p)

  @inline def filter(p: (A) => Boolean) = value.filter(p)

  @inline def toRight[X](left: => X) = value.toRight(left)

  @inline def toLeft[X](right: => X) = value.toLeft(right)

  @inline def nonEmpty = value.nonEmpty

  @inline def flatMap[B](f: (A) => Option[B]) = value.flatMap(f)

  def map[B](f: (A) => B): Option[B] = value.map(f)
  def map[B](javafun: Function[A, B]): Option[B] = map(JavaInterop.asScala(javafun))

  def ifdef(f: A => Unit): Unit = value.foreach(f)
  def ifdef(javafun: Consumer[A]): Unit = ifdef(JavaInterop.asScala(javafun))

  /**
    * Checks if the property has a value associated with it yet.
    * If this method returns false, the [[general.Property#get()]] method will throw an exception.
    * Furthermore, the [[general.Property#option()]] method will return [[scala.None]].
    * @return Value indicating whether the underlying property value is defined.
    */
  def isDefined = value.isDefined

  /**
    * Checks if the property has no value associated with it yet.
    * If this method return true, see [[general.Property#isDefined()]] to study the case in which isDefined()
    * returns false to see what could happen.
    * @return Value indicating whether the underlying property value not defined.
    *         If `true`, then no value has been initialized yet.
    */
  def isEmpty = value.isEmpty

  /**
    * Resets the property to hold no value.
    * After this call, the propertys get method will throw an exception until another valid value
    * is set through the set method.
    */
  private[general] def undef(): Property[A] = {
    value = None
    this
  }

  /**
    * Applies a custom function to the property without mutating it.
    *
    * @param f The function with which to mutate the result of the function.
    * @tparam B The result type of the given function.
    * @return A mutated value.
    */
  def apply[B](f: A => B) = if (f != null) f(get) else get

  def apply() = get

  def update(x: A) = this set x

}

object Property {

  def apply[A](x: A) = new Property(Some(x))

  def apply[A]() = new Property[A](None)

  /**
    * Injects code into the set function of the property to check the new value
    * if it exists (if it is not null).
    *
    * @tparam A The type of the underlying value.
    * @return The property with validation check.
    */
  def withValidation[A](): Property[A] = {
    val ret = Property[A]()
    ret appendSetter { x => require(x != null); x }
    ret
  }

  /**
    * Injects code into the set function of the property to check the new value
    * if it exists (if it is not null).
    *
    * @tparam A The type of the underlying value.
    * @param x The initial value to set the property to.
    * @return The property with validation check.
    */
  def withValidation[A](x: A): Property[A] = {
    val ret = withValidation[A]()
    ret set x
    ret
  }

  @inline @deprecated implicit def toUnderlyingValue[A](property: Property[A]): A = property.get

}

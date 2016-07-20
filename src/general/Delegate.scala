package general

import java.util.UUID
import java.util.function.Consumer

@FunctionalInterface
trait VoidConsumer {
  def call(): Unit
}

/**
  * An implementation of the observer pattern.
  *
  * The observer pattern is found in a variety of situations: calling functions that registered
  * to an event they want to listen to. <br>
  * My opinion was that there was no good implementation for the observer pattern in Scala, so
  * I has to make one myself.
  * This is pretty much the Listener pattern that Java programmers are used to (and I think a lot
  * of Java programmers know them, indeed...).
  *
  * It is recommended that you use the factory methods, but if you really need mixin
  * traits, go ahead and call the constructors for the [[general.DelegateLike]] types directly.
  */
object Delegate {
  def create[In] = new Delegate[In] with ClearableDelegate
  def createZeroArity = new Function0Delegate with ClearableDelegate
}

/**
  * Base trait for all delegates and event types.
  */
sealed trait DelegateLike {

  type FunType

  protected var _callbacks = List[Handle]()

  /**
    * Registers a callback function to the delegate.
    *
    * The new name for the callback is determined by its hash code.
    *
    * @param f The function to register.
    */
  def +=(f: FunType): Handle = +=(UUID.randomUUID().toString)(f)

  /**
    * Registers a key-function pair as a callback.
    *
    * Many side-effects combined together do not make up very well for debugging, since this is
    * an implementation of the observer pattern. The debugging programmer cannot oversee registered callbacks
    * efficiently without attaching some kind of unique name to them.
    *
    * So the first entry in the tuple is the "key" to the callback (a.k.a. its name), and the second entry
    * is the callback itself.
    *
    * When adding a callback to the delegate with a name that already exists in the mapping, an exception is thrown.
    *
    * `+=` as an infix operator with this parameter list style cannot be used, use explicit `register` method instead.
    *
    * @param k The name the callback is assigned.
    * @param f The actual callback.
    * @return A handle to that delegate for later disposal. May be ignored.
    */
  def +=(k: String)(f: FunType): Handle = synchronized {
    val handle = new Handle(k, f)
    assert(!(_callbacks exists (handle => handle.name == k)), ScalaUtil.errorMessage(s"Ambiguous callback (name=$k) in delegate $this"))
    _callbacks = _callbacks ++ List(handle)
    handle
  }

  def +=(df: (String, FunType)): Handle = +=(df._1)(df._2)

  /**
    * Registers a callback function to the delegate. <br>
    * This method exists for Java-interop.
    *
    * @param f The function to register.
    */
  final def register(f: FunType): Handle = +=(f)

  final def register(df: (String, FunType)): Handle = +=(df._1)(df._2)

  final def register(key: String)(f: FunType): Handle = +=(key)(f)

  def registerOnce(f: FunType): Handle = registerOnce(f.hashCode().toString -> f)

  def registerOnce(df: (String, FunType)): Handle

  def registerOnce(k: String)(f: FunType): Handle = registerOnce(k -> f)

  /**
    * Unregisters a callback function from the delegate.
    *
    * @param f The function to unregister.
    */
  final def -=(f: FunType): Unit = synchronized {
    val search = _callbacks find { _.function == f }
    search match {
      case Some(handle) =>
        handle.dispose()
      case None =>
      // Do nothing, the specified function has not been found.
    }
  }

  def -=(h: Handle): Unit = {
    val isHandled = _callbacks.contains(h)
    if (isHandled) {
      h.dispose()
    }
  }

  /**
    * Unregisters a callback function from the delegate. <br>
    * This method exists for Java-interop.
    *
    * @param f The function to unregister.
    */
  final def unlog(f: FunType): Unit = -=(f)

  final def unlog(h: Handle): Unit = -=(h)

  /** Returns the callbacks of the delegate as an immutable list. */
  def callbacks = _callbacks.map(handle => handle.function)

  /**
    * A handle which maps to the function.
    * '''Do not construct instances of this class on your own, it is considered internal to the delegate!'''
    *
    * @param function The function to which the handle maps to.
    */
  class Handle(val name: String, val function: FunType) {

    private var _disposed = false

    /** Does the handle reference to a function in the delegate yet? */
    def isValid = _disposed

    /**
      * Removes the handle from the delegate. <p>
      * The function enables the caller to remove the function he once added to the delegate.
      * If the function does not exist anymore or the [[general.Delegate.Delegate#dispose( )]] method has been called already,
      * no action is done.
      */
    def dispose(): Unit = {
      if (!isValid) {
        _callbacks = _callbacks filterNot (_ == this)
        _disposed = true
      }
    }
  }

  /**
    * Generates a string with all callback names.
    */
  override def toString = s"Delegate(${_callbacks map (handle => handle.name) map (str => s"\'$str\'")})"

}

/**
  * Mixin trait for every event that should be able to be cleared of its registered clients.
  */
trait ClearableDelegate extends DelegateLike {
  def clear(): Unit = _callbacks = List[Handle]()
}

/**
  * Mixin trait for enabling delegates to check if they are being called recursively.
  */
trait RecursiveCallCheck {

  @volatile private var _isProcessing = false

  def checkRecursion(): Unit = {
    if (_isProcessing) {
      LogFacility.logCurrentStackTrace("Recursive processing detected at", "Warning")
      throw new RuntimeException
    }
  }

  /**
    * Is this check instance allowed a caller to enter already?
    *
    * @return A boolean.
    */
  final def processing = _isProcessing

  /**
    * Only to be set by subclassing classes.
    *
    * @param x The new boolean value.
    */
  protected final def processing_=(x: Boolean): Unit = _isProcessing = x

}

/**
  * Mixin trait for every delegate accepting one argument.
  *
  * @tparam In The type of parameter.
  */
trait ParameterizedDelegate[In] extends DelegateLike with RecursiveCallCheck {

  override type FunType = (In) => Unit

  def apply(arg: In): Unit = {
    checkRecursion()
    if (processing) return
    processing = true
    try callbacks foreach {
      case pf: PartialFunction[In, Any] => if (pf.isDefinedAt(arg)) pf(arg) else throw new MatchError(this)
      case reg_f: ((In) => Any)         => reg_f(arg)
    }
    finally {
      processing = false
    }
  }

  def registerJava(jf: Consumer[In]) = this.register(x => jf.accept(x))

  def registerJava(key: String, jf: Consumer[In]) = this.register(key, x => jf.accept(x))

  override def registerOnce(df: (String, In => Unit)): Handle = synchronized {

    val (k, f) = df

    var handle: Handle = null

    val clearLogic = f.andThen { forwardParam =>
      handle.dispose()
      forwardParam
    }

    handle = new Handle(k, clearLogic)
    _callbacks = _callbacks ++ List(handle)
    handle
  }

  def registerOnceJava(jf: Consumer[In]): Unit = registerOnce(x => jf.accept(x))
  def registerOnceJava(key: String, jf: Consumer[In]): Unit = registerOnce(key, x => jf.accept(x))

}

/**
  * Mixin trait for every delegate accepting no arguments.
  */
trait NonParameterizedDelegate extends DelegateLike with RecursiveCallCheck {

  override type FunType = () => Unit

  def apply(): Unit = {
    checkRecursion()
    if (processing) return
    processing = true
    try callbacks foreach {
      _()
    }
    finally {
      processing = false
    }
  }

  def registerJava(jf: VoidConsumer): Unit = this += (() => jf.call())
  def registerJava(key: String, jf: VoidConsumer): Unit = this.register(key, () => jf.call())

  override def registerOnce(df: (String, () => Unit)): Handle = synchronized {
    val (k, f) = df
    var handle: Handle = null

    val clearLogic = { () =>
      f()
      handle.dispose()
    }

    handle = new Handle(k, clearLogic)
    _callbacks = _callbacks ++ List(handle)
    handle
  }

  def registerOnceJava(jf: VoidConsumer): Unit = registerOnce(() => jf.call())

}

/**
  * A standard delegate that accepts an input type and an output type.
  *
  * Since [[scala.PartialFunction]] is a subclass of (A) => B, I don't need to write a seperate
  * "PartialFunctionDelegate" class.
  *
  * For Java code, use:
  * {{{
  *   Delegate.Delegate< [Input type] > d = new Delegate.Delegate< [Input type] >();
  *   d.register(new AbstractFunction1< [Input type], BoxedUnit >() {
  *     public BoxedUnit apply( [Input type] v1 ) {
  *       // Code to execute when the delegate/listener is called...
  *       return BoxedUnit.UNIT;
  *     }
  *   };
  * }}}
  * where <code>[InputType]</code> is the respective input type of the callback function.
  *
  * @tparam In The input type of the function. For multiple values, use tuples or custom classes.
  */
class Delegate[In](callbackList: List[(In) => Unit]) extends ParameterizedDelegate[In] {

  // Auxiliary constructor for instantiating a clean delegate with no registered callbacks.
  def this() = this(List[(In) => Unit]())

  // The callbacks have to be registered
  callbackList foreach {
    register
  }
}

/**
  * An implementation of an immutable delegate. Mixin trait for [[general.Delegate.DelegateLike]].
  * <p>
  * Use this trait like so:
  * {{{
  *   val delegate = new Delegate[some type] with ImmutableDelegate
  * }}}
  * <p>
  * Immutable delegates are delegates to which no callbacks can be registered.
  * The delegate only redirects to the functions it knows and does not accept any other functions.
  * Using mutating methods of [[general.Delegate.DelegateLike]] will cause a [[java.lang.UnsupportedOperationException]] to be thrown.
  */
trait ImmutableDelegate extends DelegateLike {

  private def except = throw new UnsupportedOperationException("Delegate is immutable.")

  override def +=(f: FunType): Handle = except

  override def -=(h: Handle): Unit = except

}

/**
  * A delegate that does not receive any input parameters. <p>
  *
  * I cannot create a delegate that models a Function0 with a normal Delegate class. I had to
  * write a new one. <p>
  *
  * For Java code, use
  * {{{
  *   Delegate.Function0Delegate d = new Delegate.Function0Delegate();
  *   d.register(new AbstractFunction0< BoxedUnit >() {
  *     public BoxedUnit apply() {
  *       // Code to execute when the delegate/listener is called...
  *       return BoxedUnit.UNIT;
  *     }
  *   };
  * }}}
  */
class Function0Delegate(callbackList: List[() => Unit]) extends NonParameterizedDelegate {

  def this() = this(List[() => Unit]())

  callbackList foreach {
    register
  }
}

case class RecursiveProcessingException() extends Exception

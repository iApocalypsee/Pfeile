package general

import java.util.function.Consumer

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
  * traits, go ahead and call the constructors for the [[general.Delegate.DelegateLike]] types directly.
  */
object Delegate {

  @FunctionalInterface
  trait ProcFun0 {
    def call(): Unit
  }

  object ProcFun0 {
    private[Delegate] def toScalaFunction(procFun0: ProcFun0) = () => procFun0.call()
  }

  @inline def create[In] = new Delegate[In] with ClearableDelegate

  @inline def createZeroArity = new Function0Delegate with ClearableDelegate

  // Inner classes.
  // I made them inner classes so that these classes don't float
  // in the class world like a space ship.

  /** The base trait of all delegate types. */
  sealed trait DelegateLike {

    /** The type of function that the delegate expects. */
    type FunType

    protected var _callbacks = List[Handle]()

    /**
      * Registers a callback function to the delegate.
      *
      * @param f The function to register.
      */
    def +=(f: FunType): Handle = synchronized {
      val handle = new Handle(f)
      _callbacks = _callbacks ++ List(handle)
      handle
    }

    /**
      * Registers a callback function to the delegate. <br>
      * This method exists for Java-interop.
      *
      * @param f The function to register.
      */
    final def register(f: FunType): Handle = +=(f)

    def registerOnce(f: FunType): Handle

    /**
      * Unregisters a callback function from the delegate.
      *
      * @param f The function to unregister.
      */
    final def -=(f: FunType): Unit = synchronized {
      val search = _callbacks.find { _.function == f }
      search match {
        case Some(handle) =>
          handle.dispose()
        case None =>
        // Do nothing, the specified function has not been found.
      }
    }

    def -=(h: Handle): Unit = {
      val isHandled = _callbacks contains h
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
    def callbacks = _callbacks.collect {
      case e => e.function
    }

    /**
      * A handle which maps to the function.
      * '''Do not construct instances of this class on your own, it is considered internal to the delegate!'''
      * @param function The function to which the handle maps to.
      */
    class Handle(val function: FunType) {

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
          _callbacks = _callbacks.filterNot(_ == this)
          _disposed = true
        }
      }
    }

  }

  trait ClearableDelegate extends DelegateLike {

    def clear(): Unit = _callbacks = List[Handle]()

  }

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
      * @return A boolean.
      */
    final def processing = _isProcessing

    /**
      * Only to be set by subclassing classes.
      * @param x The new boolean value.
      */
    protected final def processing_=(x: Boolean): Unit = _isProcessing = x

  }

  trait ParameterizedDelegate[In] extends DelegateLike with RecursiveCallCheck {

    override type FunType = (In) => Unit

    def apply(arg: In): Unit = {
      checkRecursion()
      if(processing) return
      processing = true
      callbacks foreach {
        case pf: PartialFunction[In, Any] => if (pf.isDefinedAt(arg)) pf(arg) else throw new MatchError(this)
        case reg_f: ((In) => Any) => reg_f(arg)
      }
      processing = false
    }

    def registerJava(jf: Consumer[In]) = this += (x => jf.accept(x))

    override def registerOnce(f: FunType): Handle = synchronized {

      var handle: Handle = null

      val clearLogic = f.andThen { forwardParam =>
        handle.dispose()
        forwardParam
      }

      handle = new Handle(clearLogic)
      _callbacks = _callbacks ++ List(handle)
      handle
    }

    def registerOnceJava(jf: Consumer[In]): Unit = registerOnce(x => jf.accept(x))

  }

  trait NonParameterizedDelegate extends DelegateLike with RecursiveCallCheck {

    override type FunType = () => Unit

    def apply(): Unit = {
      checkRecursion()
      if(processing) return
      processing = true
      callbacks foreach {
        _()
      }
      processing = false
    }

    def registerJava(jf: ProcFun0): Unit = this += ProcFun0.toScalaFunction(jf)

    override def registerOnce(f: FunType): Handle = synchronized {

      var handle: Handle = null

      val clearLogic = { () =>
        f()
        handle.dispose()
      }

      handle = new Handle(clearLogic)
      _callbacks = _callbacks ++ List(handle)
      handle
    }

    def registerOnceJava(jf: ProcFun0): Unit = registerOnce(ProcFun0.toScalaFunction(jf))
  }

  /**
    * A standard delegate that accepts an input type and an output type. <p>
    *
    * Since [[scala.PartialFunction]] is a subclass of (A) => B, I don't need to write a seperate
    * "PartialFunctionDelegate" class. <p><p>
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

}

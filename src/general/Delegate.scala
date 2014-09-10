package general

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/** An implementation of the observer pattern.
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

  def create[In <: AnyRef] = new Delegate[In]

  def createZeroArity = new Function0Delegate

  // Inner classes.
  // I made them inner classes so that these classes don't float
  // in the class world like a space ship.

  /** The base trait of all delegate types. */
  sealed trait DelegateLike {
    /** The type of function that the delegate expects. */
    type FunType

    private var _callbacks = new mutable.ArrayBuffer[FunType]

    /** Registers a callback function to the delegate.
      *
      * @param f The function to register.
      */
    def +=(f: FunType): Unit = synchronized {
      _callbacks += f
    }

    /** Registers a callback function to the delegate. <br>
      * This method exists for Java-interop.
      *
      * @param f The function to register.
      */
    def register(f: FunType): Unit = +=( f )

    /** Unregisters a callback function from the delegate.
      *
      * @param f The function to unregister.
      */
    def -=(f: FunType): Unit = synchronized {
      _callbacks -= f
    }

    /** Unregisters a callback function from the delegate. <br>
      * This method exists for Java-interop.
      *
      * @param f The function to unregister.
      */
    def unlog(f: FunType): Unit = -=( f )

    /** Returns the callbacks of the delegate as an immutable list. */
    def callbacks = _callbacks.toList
  }

  /** A standard delegate that accepts an input type and an output type. <p>
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
    * @tparam In The input type of the function. For multiple values, use tuples.
    */
  class Delegate[In <: AnyRef](callbackList: List[(In) => Unit]) extends DelegateLike {
    override type FunType = (In) => Unit

    // Auxiliary constructor for instantiating a clean delegate with no registered callbacks.
    def this() = this( List[(In) => Unit]( ) )

    // The callbacks have to be registered
    callbackList foreach {
      register
    }

    def call(arg: In): Unit = callbacks foreach {
      case pf: PartialFunction[In, Unit] => if (pf.isDefinedAt( arg )) pf( arg ) else throw new MatchError( this )
      case reg_f: ((In) => Unit) => reg_f( arg )
    }

      /** Code in java zum ausführen der gethreaden Version:
        *
         <code> delegate.callAsync(<In>, scala.concurrent.ExecutionContext.Implicits$.MODULE$.global()); </code>*/
    def callAsync(arg: In)(implicit ec: ExecutionContext): Future[Unit] = Future {
      call( arg )
    }
  }

  /** An implementation of an immutable delegate. Mixin trait for [[DelegateLike]].
    * <p>
    * Use this trait like so:
    * {{{
    *   val delegate = new Delegate[some type] with ImmutableDelegate
    * }}}
    * <p>
    * Immutable delegates are delegates to which no callbacks can be registered.
    * The delegate only redirects to the functions it knows and does not accept any other functions.
    * Using mutating methods of [[DelegateLike]] will cause a [[UnsupportedOperationException]] to be thrown.
    */
  trait ImmutableDelegate extends DelegateLike {
    private def except = throw new UnsupportedOperationException( "Delegate is immutable." )

    override def +=(f: FunType): Unit = except

    override def register(f: FunType): Unit = except

    override def -=(f: FunType): Unit = except

    override def unlog(f: FunType): Unit = except
  }

  /** Checks the argument with a check function returning a boolean value. <p>
    * If the function returns true, the argument is good, else something's wrong.
    *
    * @tparam In The input type of the function. For multiple values, use tuples.
    */
  trait Check[In <: AnyRef] extends Delegate[In] {

    override def call(arg: In): Unit = {
      if (check( arg )) super.call( arg )
      else {
        println( s"$arg has not been accepted" +
          s" by ${check _}" )
      }
    }

    def check(arg: In): Boolean = true
  }

  /** Checks specifically if the argument is null. If true, a [[NullPointerException]] is thrown.
    *
    * @tparam In The input type of the function. For multiple values, use tuples.
    */
  trait NullCheck[In <: AnyRef] extends Check[In] {
    override def check(arg: In): Boolean = if (arg eq null) throw new NullPointerException else super.check( arg )
  }

  /** A delegate that does not receive any input parameters. <p>
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
  class Function0Delegate(callbackList: List[() => Unit]) extends DelegateLike {
    override type FunType = () => Unit

    def this() = this( List[() => Unit]( ) )

    callbackList foreach {
      register
    }

    def call(): Unit = callbacks foreach {
      _( )
    }

    def callAsync()(implicit ec: ExecutionContext): Future[Unit] = Future {
      call( )
    }
  }


}

package misc

import akka.actor.Cancellable
import general.{Delegate, Main}

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

/** Holds an object that is deallocated after a certain amount of time.
  *
  * By terms the resource is not really deallocated when the references are copied by the
  * getDirect() method. The inner reference to the resource is just set to null, making the resource
  * possibly available for garbage collection.
  */
class VolatileResource[A <: AnyRef](x: A, deallocTime: FiniteDuration = 5.seconds) {

  require(x ne null)

  // The underlying resource.
  @volatile private var _x: Option[A] = Some(x)

  private var _deallocCountdown = deallocTime

  // The thread that is executed after the deallocation countdown.
  private var _deallocThread: Cancellable = deallocCancellableObject

  /** Delegate which gets called when the underlying object is being set to null. */
  val onDispose = Delegate.create[A]

  def deallocCountdown = _deallocCountdown
  def deallocCountdown(x: FiniteDuration) = _deallocCountdown = x

  def get(): Option[A] = {

    // If a dealloc thread is still active, cancel it. I don't need it anymore.
    if(_deallocThread ne null) {
      _deallocThread.cancel()
    }
    // And schedule another thread for deallocation.
    _deallocThread = deallocCancellableObject
    // Return the option.
    _x

  }

  private def deallocCancellableObject = Main.getActorSystem.scheduler.scheduleOnce(deallocCountdown) {
    // If the _x option has a value, make a call to the dealloc delegate.
    if(_x.isDefined) onDispose call _x.get

    _x = None
    _deallocThread = null
  }

  /** Accesses the resource directly.
    *
    * By "directly" the method means that the returned value can be null.
    * Calling this method resets the dealloc counter of the volatile resource.
    */
  def getDirect = {
    val opt = get()
    if(opt.isDefined) opt.get
    else null
  }

  /** Sets the resource to a new value and resets the cancellable object.
    *
    * @param x The new value to set to.
    */
  def set(x: A) = {
    require(x ne null)
    _x = Some(x)
    _deallocThread = deallocCancellableObject
  }


}

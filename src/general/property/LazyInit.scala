package general.property

import java.util.function.Supplier

/**
  * Initializes a property lazily to a default value.
  * Be aware that the lazy behavior is only triggered once. After the property has been initialized lazily, most
  * of the setter methods in this trait will ignore the call.
  *
  * When a client is calling the set method on the property while lazy initialization has not happened yet,
  * it won't get triggered, but the lazy behavior gets tossed out.
  * @tparam A The type of property. No variance by design.
  */
trait LazyInit[A] extends PropertyBase[A] {
  self: AccessorStyle[_] =>

  private var alreadyComputed = false

  private var _lazyCompute = failLazyCompute

  private def failLazyCompute: Supplier[A] = () => throw new ExceptionInInitializerError("No lazy computation available or already used")

  def lazyCompute = _lazyCompute
  def lazyCompute_=(x: Supplier[A]) = if (!alreadyComputed) {
    require(x != null)
    _lazyCompute = x
  }

  def getLazyCompute = lazyCompute
  def setLazyCompute(x: Supplier[A]) = this.lazyCompute = x

  override def get = {
    if (isEmpty && !alreadyComputed) {
      val lazyComputation = lazyCompute.get()
      lazyCompute = failLazyCompute
      this set lazyComputation
    }
    alreadyComputed = true
    super.get
  }

  override def set(x: A) = {
    if(!alreadyComputed && x != null) alreadyComputed = true
    super.set(x)
  }
}

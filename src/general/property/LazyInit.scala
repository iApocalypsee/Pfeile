package general.property

import java.util.function.Supplier

import general.JavaInterop

/**
  * Initializes a property lazily to a default value.
  * Be aware that the lazy behavior is only used once. After the property has been initialized lazily once, most
  * of the setter methods in this trait will ignore the call.
  * @tparam A The type of property. No variance by design.
  */
trait LazyInit[A] extends PropertyBase[A] {
  self: AccessorStyle[_] =>

  throw new NotImplementedError("Lazy initialization for box object like java.lang.Integer. TODO: Optimize for Java!")

  private var alreadyComputed = false

  private var _lazyCompute: () => A = failLazyCompute

  private def failLazyCompute = () => throw new ExceptionInInitializerError("No lazy computation available or already used")

  def lazyCompute = _lazyCompute
  def lazyCompute_=(x: () => A) = if (!alreadyComputed) {
    require(x != null)
    _lazyCompute = x
  }

  def getLazyCompute = JavaInterop.asJava(lazyCompute)
  def setLazyCompute(javafun: Supplier[A]) = if (!alreadyComputed) lazyCompute = JavaInterop.asScala(javafun)

  override def get = {
    if (isEmpty && !alreadyComputed) {
      val lazyComputation = lazyCompute()
      alreadyComputed = true
      lazyCompute = failLazyCompute
      this set lazyComputation
    }
    super.get
  }
}

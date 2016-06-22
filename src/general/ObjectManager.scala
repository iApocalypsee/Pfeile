package general

import java.lang.ref.WeakReference

import scala.collection.mutable

/**
  * Manages objects in a weakly manner.
  * This class can be used to keep track of how many objects have been allocated of one class, for example.
  * @tparam A The type to watch for.
  * @see [[java.lang.ref.WeakReference]]
  */
class ObjectManager[A] {

  private val _objects = mutable.ArrayBuffer[WeakReference[A]]()
  private val onEnter = mutable.ArrayBuffer[WeakReference[A] => Unit]()

  def manage(x: A): Unit = {
    if (!exists(_ == x)) {
      val weakref = new WeakReference(x)
      _objects += weakref
      for (fun <- onEnter) fun(weakref)
    }
  }

  def exists(f: A => Boolean): Boolean = _objects.exists(asWeakAppl(f))

  def applyForEvery(f: A => Unit): Unit = _objects.foreach(asWeakAppl(f))

  def applyOnEnter(f: A => Unit): Unit = onEnter += asWeakAppl(f)

  /**
    * Transforms given function to a function accepting a weak reference object instead
    * of the object directly.
    * @param f The function to transform.
    * @tparam R The return type of the function.
    * @return The transformed function accepting a weak reference.
    */
  private def asWeakAppl[R](f: A => R) = { (weakRef: WeakReference[A]) =>
    f(weakRef.get())
    //f.compose[WeakReference[A]](_.get())
  }

  
  def weakRefObjects = _objects.toSeq
  def objects = _objects.map(_.get()).toSeq

}

/**
  * Possible access point to an object manager object. The restriction to this type of object
  * is that the underlying object manager cannot be mutated from this object. So no objects can
  * be added to the underlying object manager.
  * Purpose of this class is to prevent outside mutation of the internal object manager.
  * @param objectManager The object manager to delegate method calls to.
  * @tparam A Ditto.
  */
class ImmutableObjectManagerFacade[A](private val objectManager: ObjectManager[A]) {

  private val x = objectManager

  def applyForEvery(f: (A) => Unit): Unit = x.applyForEvery(f)

  def exists(f: (A) => Boolean): Boolean = x.exists(f)

  def applyOnEnter(f: (A) => Unit): Unit = x.applyOnEnter(f)

  def weakRefObjects: Seq[WeakReference[A]] = x.weakRefObjects

  def objects: Seq[A] = x.objects
}

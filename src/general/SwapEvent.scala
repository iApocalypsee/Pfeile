package general

/**
  * Generic event type for denoting that an object has been replaced.
  */
case class SwapEvent[A](oldObj: A, newObj: A)

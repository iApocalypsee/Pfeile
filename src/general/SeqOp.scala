package general

/**
  * Additional functions that could be helpful sometimes.
  */
class SeqOp[A] private (val from: Seq[A]) extends AnyVal {
  
  @deprecated def filterType[B](clazz: Class[B]) = from.filter(_.getClass == clazz).asInstanceOf[Seq[B]]

}

object SeqOp {

  implicit def create[A](from: Seq[A]): SeqOp[A] = new SeqOp(from)

}

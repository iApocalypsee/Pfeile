package general

/**
  * Additional functions that could be helpful sometimes.
  */
class SeqOp[A] private (from: Seq[A]) {

  /**
   * Returns every instance in the list that conforms to A.
   * @tparam A The type to conform to.
   * @return A list of objects which conform to the type parameter.
   */
  def filterType[A] = from.filter(_.isInstanceOf[A]).asInstanceOf[Seq[A]]
  
  def filterType[B](clazz: Class[B]) = from.filter(_.getClass == clazz).asInstanceOf[Seq[B]]

}

object SeqOp {

  implicit def create[A](from: Seq[A]): SeqOp[A] = new SeqOp(from)

}

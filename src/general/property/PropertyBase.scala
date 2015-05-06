package general.property

/**
  * The most basic trait of a property is the get and set branch.
  * @tparam A The type of property. No variance by design.
  */
trait PropertyBase[A] extends PropertyLike[A] with EagerEval[A] {
  // A property needs to have an accessor style. Needs to have.
  self: AccessorStyle[_] =>

  private var value: Option[A] = None

  override def get: A = option.getOrElse(throw new RuntimeException("Property is undefined. undef() call? Lazy compute triggered? Did setter accept value?"))

  override def set(x: A): Unit = x match {
    case anyRef: AnyRef =>
      if (anyRef == null) value = None
      else value = Some(x)
    case _ => value = Some(x)
  }

  override def option = value

  override def ifdef(x: A => Unit) = option.foreach(x)

}

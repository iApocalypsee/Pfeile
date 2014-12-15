package newent.pathfinding

/**
 *
 * @author Josip Palavra
 */
case class Path(steps: Seq[Path.Step]) {

  import newent.pathfinding.Path.Step

  def length = steps.size

  def append(s: Step): Unit = Path(steps ++ Seq(s))
  def prepend(s: Step): Unit = Path(Seq(s) ++ steps)

  def contains(x: Int, y: Int): Boolean = steps.foldLeft(false)({ (p, e) =>
    if(p) p
    else if(e.x == x && e.y == y) true
    else false
  })

}

object Path {

  /** A step in a more longer path. */
  case class Step(x: Int, y: Int, reqMovementPoints: Int) {

    override def hashCode() = x * y

    override def equals(obj: scala.Any): Boolean = obj match {
      case s: Step => s.x == x && s.y == y
      case _ => false
    }
  }

}

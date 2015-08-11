package newent.pathfinding

case class Path(steps: Seq[Path.Step]) {

  import newent.pathfinding.Path.Step

  def length = steps.size

  /** Append a step to the end of the path.
    *
    * @param s The step to append.
    */
  def append(s: Step): Unit = Path(steps ++ Seq(s))

  /** Puts a step at the beginning of the path.
    *
    * @param s The step to prepend.
    */
  def prepend(s: Step): Unit = Path(Seq(s) ++ steps)

  /** Checks if the specified tile lies on this very path.
    *
    * @param x The x coordinate of the tile.
    * @param y The y coordinate of the tile.
    * @return Self-evident.
    */
  def contains(x: Int, y: Int): Boolean = steps.foldLeft(false)({ (p, e) =>
    if(p) p
    else if(e.x == x && e.y == y) true
    else false
  })

}

object Path {

  /** A step in a more longer path. */
  case class Step(x: Int, y: Int, reqMovementPoints: Int) {

    override def hashCode() = x * y + x

    override def equals(obj: scala.Any): Boolean = obj match {
      case s: Step => s.x == x && s.y == y
      case _ => false
    }
  }

}

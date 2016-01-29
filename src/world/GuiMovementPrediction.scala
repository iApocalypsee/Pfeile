package world

import com.sun.istack.internal.Nullable
import newent.pathfinding.Path

/**
  * Set of methods related to displaying a proper movement prediction of a movable entity.
  * Those methods are tightly coupled to potentially private properties of IsometricPolygonTileComponent
  * for now, so it's best not to move this object to another package.
  */
object GuiMovementPrediction {

  /**
    * If a path is displayed right now, the path is going to be reflected in this field.
    */
  private var m_currentPath = Option.empty[Seq[Tile]]

  /**
    * Implementation logic of the path drawing.
    *
    * @param p The path to draw.
    * @param t The terrain to draw for.
    */
  private def display(p: Path, t: TerrainLike): Unit = synchronized {
    val optionalPath = Option(p)

    m_currentPath = optionalPath.map { primitivePath =>
      for(step <- primitivePath.steps) yield t.tileAt(step.x, step.y)
    }

    for(path <- m_currentPath;
        tileStep <- path) tileStep.component match {
      case i: IsometricPolygonTileComponent =>
        i.predictedPath = m_currentPath
    }
  }

  /**
    * Deletes the currently displayed path.
    *
    * The path itself is not going to be deleted, only the UI representation of it
    * will disappear. This method does not make any assumptions on how the path
    * object is going to be used.
    */
  def erasePath(): Unit = synchronized {
    for(path <- m_currentPath;
        step <- path) step.component match {
      case i: IsometricPolygonTileComponent =>
        i.predictedPath = Option.empty
    }

    m_currentPath = Option.empty
  }

  /**
    * Causes the world to display another path.
    *
    * The previous path is discarded in the process. Calling this method with `null` as path
    * will behave the same as calling `erasePath()`. When calling this method with `null` as
    * path, the terrain parameter can also be given a `null` argument.
    *
    * @param p The new path to display. Can be `null`.
    * @param t The terrain on which the method should operate on. Can be `null` if and
    *          only if the given path is `null`.
    */
  def replace(@Nullable p: Path, t: TerrainLike): Unit = synchronized {
    erasePath()
    display(p, t)
  }

  /**
    * If a path is currently displayed, this method will return true.
    *
    * @return Whether a path is currently displayed or not.
    */
  def isPathDisplayed = m_currentPath.isDefined

}

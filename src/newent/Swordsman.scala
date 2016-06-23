package newent

import java.awt.Point
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import comp.ImageComponent
import gui.screen.GameScreen
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{SeaTile, World}

/**
  * A swordsman. Ditto.
 *
  * @param world The world of the entity. Should not be null.
  * @param spawnPosition The position where the entity spawns.
  * @param team The team to which it should belong.
  */
class Swordsman(world: World, spawnPosition: Point, team: Team)
  extends Entity(world, spawnPosition.x, spawnPosition.y) with CombatUnit {

  override val life = new Life(90, 0, 90)

  // The pathfinder logic. The swordsman looks up the path up to 20 tiles and is
  // not allowed to walk on sea tiles.
  override val pathfinderLogic = new AStarPathfinder(20, t => !t.isInstanceOf[SeaTile])

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 3

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
 *
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new ImageComponent(0, 0, Swordsman.swordsmanTexture, GameScreen.getInstance())

  /**
    * The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning.
    */
  override protected def initialTeam = team

  override def toString: String = "Swordsman: " + name

  override val poison: Poison = new Poison(this, 0.08f)
}

object Swordsman {
  // Achieved through Cheating. Don't blame me.
  private def swordsmanTexture: BufferedImage = ImageIO.read(classOf[Swordsman].getClassLoader.getResourceAsStream("resources/gfx/entities/melee/swordsman.png"))
}

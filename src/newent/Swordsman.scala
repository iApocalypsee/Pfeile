package newent

import java.awt.Point
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import comp.ImageComponent
import gui.screen.GameScreen
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{ SeaTile, WorldLike }

/**
  * A swordsman. Ditto.
  * @param world The world of the entity. Should not be null.
  * @param spawnPosition The position where the entity spawns.
  * @param team The team to which it should belong.
  */
class Swordsman(world: WorldLike, spawnPosition: Point, team: Team)
  extends Entity(world, spawnPosition, null) with LivingEntity with VisionEntity with MoveableEntity with Combatant {

  override val life = new Life(90, 0, 90)

  // The pathfinder logic. The swordsman looks up the path up to 20 tiles and is
  // not allowed to walk on sea tiles.
  override val pathfinderLogic = new AStarPathfinder(20, t => !t.isInstanceOf[SeaTile])

  /**
    * The initial attribute object with which the entity begins recording its attributes.
    *
    * The method just gets called once to initialize an underlying field.
    */
  override protected def initialAttribute = new Attributes {

    override protected def initialCurrent(initObject: Current) = initObject

    override protected def initialLasting(initObject: Lasting) = initObject
  }

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 3

  /**
    * The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[java.lang.IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new ImageComponent(0, 0, Swordsman.swordsmanTexture, GameScreen.getInstance())

  /**
    * The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning.
    */
  override protected def initialTeam = team
}

object Swordsman {
  // Achieved through Cheating. Don't blame me.
  private def swordsmanTexture: BufferedImage = ImageIO.read(classOf[Swordsman].getClassLoader.getResourceAsStream("resources/gfx/entities/melee/swordsman.png"))
}

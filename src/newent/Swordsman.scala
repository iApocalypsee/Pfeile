package newent

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import comp.ImageComponent
import gui.GameScreen
import newent.pathfinding.AStarPathfinder
import player.Life
import world.{SeaTile, WorldLike}

class Swordsman(world: WorldLike, spawnPosition: (Int, Int), name: String)
  extends Entity(world, spawnPosition, name) with
          LivingEntity with VisionEntity with MoveableEntity with CanJoinTeam with Combatant {

  override val life = new Life(90, 0, 90)

  // The pathfinder logic. The swordsman looks up the path up to 20 tiles and is
  // not allowed to walk on sea tiles.
  override val pathfinderLogic = new AStarPathfinder(20, t => !t.isInstanceOf[SeaTile])

  /** The initial attribute object with which the entity begins recording its attributes.
    *
    * The method just gets called once to initialize an underlying field.
    */
  override protected def initialAttribute = new Attributes {

    override protected def initialCurrent(initObject: Current) = initObject

    override protected def initialLasting(initObject: Lasting) = initObject
  }

  /** The default movement points that the entity has. */
  override def defaultMovementPoints = 3

  /** The component that the representable object uses first. Method is called only once.
    *
    * The start component must not be null at first, else it will throw a [[IllegalArgumentException]].
    * @return A component object which the representable object uses first.
    */
  override protected def startComponent = new ImageComponent(0, 0, Swordsman.swordsmanTexture, GameScreen.getInstance())
}

object Swordsman {
  // Achieved through Cheating. Don't blame me.
  private def swordsmanTexture: BufferedImage = ImageIO.read(classOf[Swordsman].getClassLoader.getResourceAsStream("resources/gfx/entities/melee/swordsman.png"))
}

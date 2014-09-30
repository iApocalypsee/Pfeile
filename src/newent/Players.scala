package newent

import java.awt.{Color, Point}

import comp.Component
import newent.pathfinding.AStarPathfinder
import world.{GrassTile, WorldLike}

/** Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint, name) with MoveableEntity with TeleportableEntity with InventoryEntity {

  // Game section.

  /** The default movement points that the entity has. */
  override def defaultMovementPoints: Int = 2

  override val pathfinderLogic = new AStarPathfinder(20, { t => t.isInstanceOf[GrassTile] })

  // GUI section.

  // The draw function just draws a rectangle for now, I can add images later. Later!
  override def drawFunction = { g => g.setColor(Color.red); g.fillPolygon(bounds) }

  override val bounds = Component.createRectPolygon(0, 0, 20, 30)
}

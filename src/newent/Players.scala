package newent

import java.awt.{Color, Graphics2D, Polygon, Point}

import comp.Component
import world.WorldLike

/** Represents a player in the world.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class Player(world: WorldLike,
             spawnpoint: Point,
             name: String) extends Entity(world, spawnpoint, name) with MoveableEntity with TeleportableEntity {

  // GUI section.

  // The draw function just draws a rectangle for now, I can add images later. Later!
  override def drawFunction = { g => g.setColor(Color.red); g.fillPolygon(bounds) }

  override val bounds = Component.createRectPolygon(0, 0, 20, 30)
}

/** Represents an active player. <p>
  *
  * An active player is a player who is logged on to the machine, <b>NOT</b> the player
  * who holds turn right now.
  *
  * @param world The world of the entity. Should not be null.
  * @param spawnpoint The spawnpoint of the player.
  * @param name The name. If null, a name based on the hash code will be generated.
  */
class ActivePlayer(world: WorldLike, spawnpoint: Point, name: String) extends Player(world, spawnpoint, name) {

  // I have to recognize the active player somehow, so the player is going to be drawn blue.
  override def drawFunction = { g => g.setColor(Color.blue); g.fillPolygon(bounds) }
}

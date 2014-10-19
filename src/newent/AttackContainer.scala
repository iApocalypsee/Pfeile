package newent

import general.{Delegate, Main}
import newent.event.AttackEvent
import player.BoardPositionable

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/** An object that can take attacks. <p>
  *
  * An attack container itself is not already an aggressor, and vice versa. The combination of those traits
  * is done in [[Combatant]]. <p>
  *
  * Calculation of impact is not done in here. Instead, <b>you</b> do
  * {{{
  *   onImpact += { e => /* Impact calculation here... */ }
  * }}}
  */
trait AttackContainer extends BoardPositionable {

  private var _attackList = mutable.ArrayBuffer[AttackProgress]()

  val onAttacked = Delegate.create[AttackEvent]
  val onImpact   = Delegate.create[AttackEvent]

  def take(e: AttackEvent): Unit = {
    _attackList += new AttackProgress(e)
    onAttacked callAsync e
  }

  def queuedAttacks = _attackList.toList

  Main.getContext.onTurnEnd += { () =>
    _attackList.clone() foreach { p =>
      p.updateProgress()
      // If the attack has reached the container, do following steps
      if(p.progress >= 1.0) {
        // Remove the attack from the list, since it is impacting
        val removed = _attackList.remove(_attackList.indexOf(p))
        // And notify the callbacks about this event
        onImpact callAsync removed.event
      }
    }
  }

}

object AttackContainer {

  /** Returns all entities in the current world (in PfeileContext) that are AttackContainer objects. */
  def allACEntities(): Seq[AttackContainer] = {
    val entityList = Main.getContext.getWorld.entities.entityList
    val checkFun = { e: EntityLike => e.isInstanceOf[AttackContainer] }
    entityList.filter(checkFun).asInstanceOf[Seq[AttackContainer]]
  }

  /** Ditto. */
  def javaAllACEntities() = allACEntities().asJava

  /** Returns all attack containers in the game.
    *
    * Right now, the list of attack containers includes the entities as well as the tiles.
    */
  def allAttackContainers(): Seq[AttackContainer] = Main.getContext.getWorld.terrain.tiles ++ allACEntities()

  /** Ditto. */
  def javaAllAttackContainers() = allAttackContainers.asJava

}

class AttackProgress (val event: AttackEvent) {

  private var _progress = 0.0

  /** Updates the progress with the associated travel speed. */
  private[newent] def updateProgress(): Unit = {
    _progress += event.travelSpeed
  }

  /** Returns the progress of the attack in percent. */
  def progress = _progress / event.geographicalLength

}

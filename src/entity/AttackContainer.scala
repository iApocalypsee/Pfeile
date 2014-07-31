package entity

import player.weapon.{AttackEvent, AttackQueue, Weapon}

import scala.collection.mutable

/**
 * An implementation of the AttackContainer interface provided in a trait for easier
 * use in Scala classes.
 * @author Josip Palavra
 * @version 22.06.2014
 */
trait AttackContainer extends player.weapon.AttackContainer {

  val attackQueues = new mutable.Queue[AttackQueue]


  /**
   * @param aggressor The combatant from whom to check.
   * @return All attack queues matching with the combatant, or an empty array.
   */
  override def getAttackQueuesBy(aggressor: player.Combatant): Array[AttackQueue] =
    attackQueues.filter((a) => a.getAggressor.eq(aggressor)).toArray

  /**
   * @param aWeapon The weapon with which to check.
   * @return All attack queues matching with the combatant, or an empty array.
   */
  override def getAttackQueuesBy(aWeapon: Class[_ <: Weapon]): Array[AttackQueue] =
    attackQueues.filter((a) => a.getWeapon.getClass.eq(aWeapon)).toArray

  override def isAttackedBy(combatant: player.Combatant): Boolean =
    attackQueues.filter((a) => a.getAggressor equals combatant).nonEmpty

  override def isAttackedBy(w: Class[_ <: Weapon]): Boolean =
    attackQueues.filter((a) => a.getWeapon.getClass.eq(w)).nonEmpty

  override def isAttacked: Boolean = attackQueues.nonEmpty
  override def unregisterAttack(queue: AttackQueue): Unit = attackQueues.dequeueFirst((q) => q eq queue)
  override def unregisterAttack(w: Class[_ <: Weapon]): Unit = attackQueues.dequeueFirst((q) => q.getWeapon.getClass.eq(w))
  override def registerAttack(queue: AttackQueue): Unit = attackQueues.enqueue(queue)
  override def registerAttack(event: AttackEvent): Unit = registerAttack(evt2Queue(event))

  implicit def evt2Queue(evt: AttackEvent) = new AttackQueue(evt)
}

trait Combatant extends AttackContainer with player.Combatant {
  override def attack(event: AttackEvent): Unit
}

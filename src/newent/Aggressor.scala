package newent

import newent.event.AttackEvent

/** An object that can operate attacks on a [[AttackContainer]].
  *
  * For now, the aggressor can just attack another attack container, but that is already the quintessence
  * of an aggressor in combat.
  */
trait Aggressor {

  /** Lets the aggressor perform an attack.
    *
    * @param withEvent The event to use.
    */
  def attack(withEvent: AttackEvent): Unit = {
    assume(withEvent.destination ne null)
    withEvent.destination.take(withEvent)
  }

}

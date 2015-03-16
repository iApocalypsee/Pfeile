package newent

import newent.event.AttackEvent

/** An object that can operate attacks on a [[newent.AttackContainer]].
  *
  * For now, the aggressor can just attack another attack container, but that is already the quintessence
  * of an aggressor in combat.
  * If the object is an aggressor, then it has to belong to a team.
  */
trait Aggressor extends CanHoldTeamContract {

  /** Lets the aggressor perform an attack.
    *
    * @param withEvent The event to use.
    */
  def attack(withEvent: AttackEvent): Unit = {
    assume(withEvent.destination ne null)
    withEvent.destination.take(withEvent)
  }

}

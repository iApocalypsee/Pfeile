package newent

import general.{Main, Delegate}
import newent.event.AttackEvent
import player.BoardPositionable

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

  def queuedAttacks = _attackList.toSeq

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

class AttackProgress (val event: AttackEvent) {

  private var _progress = 0.0

  /** Updates the progress with the associated travel speed. */
  def updateProgress(): Unit = {
    _progress += event.travelSpeed
  }

  /** Returns the progress of the attack in percent. */
  def progress = _progress / event.geographicalLength

}

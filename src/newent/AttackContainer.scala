package newent

import general.JavaInterop.JavaPrimitives.JavaDouble
import general._
import newent.event.AttackEvent
import player.BoardPositionable

import scala.collection.{JavaConversions, mutable}

/**
  * An object that can take attacks. <p>
  *
  * An attack container itself is not already an aggressor, and vice versa. The combination of those traits
  * is done in [[newent.Combatant]]. <p>
  *
  * Calculation of impact is not done in here. Instead, <b>you</b> do
  * {{{
  *   onImpact += { e => /* Impact calculation here... */ }
  * }}}
  */
trait AttackContainer extends BoardPositionable {

  private var _attackList = mutable.ArrayBuffer[AttackProgress]()

  /**
    * Called when the attack container recognizes an incoming attack.
    *
    * The argument provides information on when the attack will impact.
    */
  val onAttacked = Delegate.create[AttackProgress]

  // Note: Difference between 'onImpact' and 'onDamage' unclear, since both
  //       events take an 'AttackEvent' as argument

  val onImpact = Delegate.create[AttackEvent]

  /**
    * <code>onImpact</code> calls <code>onDamage</code>, if onImpact recognizes an attack at an Entity.
    * In previous version <code>onImpact</code> and <code>onDamage</code> have been <code>onImpact</code>, however
    * this caused a not fixable bug (The part in LivingEntity that was registered after [or before] Tile wasn't
    * called even though he should.
    */
  val onDamage = Delegate.create[AttackEvent]

  /**
    * Queues given attack for later impact.
    *
    * If given attack is to impact in the very current round, the impact will/should trigger
    * after every player has completed their turns.
    *
    * @param e The attack to queue for later consideration.
    */
  def take(e: AttackEvent): Unit = {
    if(Main.isDebug) {
      LogFacility.log(s"Attempting to queue attack... (attack=$e)", "Debug")
    }

    val progress = new AttackProgress(e)
    _attackList += progress
    onAttacked(progress)

    if(Main.isDebug) {
      LogFacility.log(s"Attack successfully queued. (attack=$e)", "Debug")
    }
  }

  /**
    * Causes the attack container to suffer immediate consequences from given attack.
    *
    * @param e The attack to be considered as immediately suffering from.
    */
  def takeImmediately(e: AttackEvent): Unit = {
    onDamage(e)
  }

  def queuedAttacks = _attackList.toList
  def getQueuedAttacks = JavaConversions.seqAsJavaList(queuedAttacks)

  /**
    * Should be called at end of every round.
    * Updates data related to attack events (timers, damage, etc.)
    */
  final def updateQueues(): Unit = {
    _attackList foreach { p =>
      p.updateProgress()
      // If the attack has reached the container, do following steps
      if (p.progress >= 1.0) {
        // Remove the attack from the list, since it is impacting
        val removed = _attackList.remove(_attackList.indexOf(p))
        // And notify the callbacks about this event
        onImpact(removed.event)
      }
    }
  }
}

/**
  * The progress of an attack.
  *
  * @param event The event to keep track of.
  * @param _progress The progress of this attack, in percent.
  */
class AttackProgress(val event: AttackEvent, private var _progress: Double) {

  def this(event: AttackEvent) = this(event, 0.0)

  /**
   * Called when the attack progresses by a little bit.
   * The argument in the delegate provides the delta (how much the attack progressed).
   */
  val onProgressed = Delegate.create[JavaDouble]

  val progressPerTurn = event.travelSpeed / event.geographicalLength

  /** Updates the progress with the associated travel speed. */
  private[newent] def updateProgress(): Unit = {
    _progress += progressPerTurn
    onProgressed(progressPerTurn)
  }

  /** Returns the progress of the attack in percent. */
  def progress = _progress

  /** Returns the number of turns, the attack needs to come to it's destination. */
  def numberOfTurns = {
    Math.ceil(1.0 / progressPerTurn).asInstanceOf[Int]
  }
}

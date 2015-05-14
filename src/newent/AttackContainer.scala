package newent

import general.{Delegate, Main, PfeileContext}
import newent.event.AttackEvent
import player.BoardPositionable
import player.weapon.arrow.AbstractArrow

import scala.collection.JavaConverters._
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

  val onAttacked = Delegate.create[AttackProgress]
  val onImpact = Delegate.create[AttackEvent]

  /**
    * <code>onImpact</code> calls <code>onDamage</code>, if onImpact recognizes an attack at an Entity.
    * In previous version <code>onImpact</code> and <code>onDamage</code> have been <code>onImpact</code>, however
    * this caused a not fixable bug (The part in LivingEntity that was registered after [or before] TileLike wasn't
    * called even though he should.
    */
  val onDamage = Delegate.create[AttackEvent]

  def take(e: AttackEvent): Unit = {
    val progress = new AttackProgress(e)
    _attackList += progress
    onAttacked(progress)
  }

  def takeImmediately(e: AttackEvent): Unit = {
    onDamage(e)
  }

  def queuedAttacks = _attackList.toList
  def getQueuedAttacks = JavaConversions.seqAsJavaList(queuedAttacks)

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

object AttackContainer {

  /** Returns all entities in the current world (in PfeileContext) that are AttackContainer objects. */
  def allACEntities(): Seq[AttackContainer] = {
    val entityList = Main.getContext.getWorld.entities.entityList
    val checkFun = { e: EntityLike => e.isInstanceOf[AttackContainer] }
    entityList.filter(checkFun).asInstanceOf[Seq[AttackContainer]]
  }

  /**
    * Collects all attack container entities and tiles from this context.
    * @param context The context to retrieve all attack containers from.
    * @return A list of all attack containers in this context.
    */
  def allAttackContainers(context: PfeileContext): Seq[AttackContainer] = {
    context.world.terrain.tiles ++: allAttackContainerEntities(context)
  }

  private def allAttackContainerEntities(context: PfeileContext): Seq[AttackContainer] = {
    context.world.entities.entityList.collect { case ac: AttackContainer => ac }
  }

  /**
    * Returns all attack containers in this context in a Java iterable.
    * @param context The context to retrieve all attack containers from.
    * @return A list containing all attack containers in this context.
    */
  def getAllAttackContainers(context: PfeileContext): java.util.List[AttackContainer] = allAttackContainers(context).asJava

  /**
    * Returns all attack progresses triggered by an instance of an arrow.
    * @param acs The attack containers.
    * @return Attack progresses caused by arrow attacks.
    */
  def arrowAttackProgresses(acs: Seq[AttackContainer]) = acs.map(ac => ac.queuedAttacks.collect {
    case progress if progress.event.weapon.isInstanceOf[AbstractArrow] => progress
  })

  /** Ditto. */
  def javaAllACEntities() = allACEntities().asJava

  /**
    * Returns all attack containers in the game.
    *
    * Right now, the list of attack containers includes the entities as well as the tiles.
    */
  def allAttackContainers(): Seq[AttackContainer] = allAttackContainers(Main.getContext)

  /** Ditto. */
  def javaAllAttackContainers() = allAttackContainers().asJava

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
  val onProgressed = Delegate.create[java.lang.Double]

  private lazy val _progressPerTurn = event.geographicalLength / event.travelSpeed

  /** Updates the progress with the associated travel speed. */
  private[newent] def updateProgress(): Unit = {
    _progress += _progressPerTurn
    onProgressed(_progressPerTurn)
  }

  /** Returns the progress of the attack in percent. */
  def progress = _progress

}

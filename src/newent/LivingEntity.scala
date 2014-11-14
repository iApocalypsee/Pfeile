package newent

import general.LogFacility
import general.LogFacility.LoggingLevel
import player.Life

/** An entity that has its own life status.
  *
  * Every entity that inherits from this trait has a life attribute attached to it.
  * For listening to the life status of the entity, see the delegates in the life property.
  */
trait LivingEntity extends Entity with AttackContainer {

  /** The life of the entity. */
  val life: Life

  // Every living entity can be attacked with weapons, so every weapon
  // should have a visible effect on the living entity.

  onTurnCycleEnded += { () =>
    updateQueues()
  }

  onImpact += { e =>
    LogFacility.log(s"$e impacted.", LoggingLevel.Debug)
    life.setLife(life.getLife - e.weapon.getAttackValue)
  }

}

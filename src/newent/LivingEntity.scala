package newent

import general.LogFacility
import player.Life
import player.weapon.AbstractArrow

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

  onImpact += { event =>
     LogFacility.log(s"Impacting attack: by ${event.aggressor} to " +
        s"${event.destination.toString} with ${event.weapon.getName}", "Debug", "atkmech")

     if (event.weapon.isInstanceOf[AbstractArrow]) {
        life.setLife(life.getLife - event.weapon.asInstanceOf[AbstractArrow].getAttackValCurrent)
     } else
        life.setLife(life.getLife - event.weapon.getAttackValue)
  }

}

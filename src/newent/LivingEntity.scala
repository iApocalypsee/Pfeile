package newent

import general.{PfeileContext, LogFacility}
import player.Life
import player.weapon.{RangedWeapon, AbstractArrow}

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
     if (this.isInstanceOf[Player]) {
         if (life.getLife + life.getLifeRegeneration > Player.MAXIMUM_LIFE.get)
            life.setLife(Player.MAXIMUM_LIFE.get)
         else
             life.setLife(life.getLife + life.getLifeRegeneration)
     }
     if (this.isInstanceOf[Bot]) {
        if (life.getLife + life.getLifeRegeneration > Bot.MAXIMUM_LIFE.get)
           life.setLife(Bot.MAXIMUM_LIFE.get)
        else
           life.setLife(life.getLife + life.getLifeRegeneration)
     }
  }

  onImpact += { event =>
     LogFacility.log(s"Impacting attack: by ${event.aggressor} to " +
           s"${event.destination.toString} with ${event.weapon.getName}", "Debug", "atkmech")

     if (event.weapon.isInstanceOf[RangedWeapon])
        life.setLife(life.getLife - event.weapon.asInstanceOf[RangedWeapon].damageAt(getGridX, getGridY))
     else
        life.setLife(life.getLife - event.weapon.getAttackValue * PfeileContext.DAMAGE_MULTI.get)
  }
}

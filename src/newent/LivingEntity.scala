package newent

import general.{LogFacility, PfeileContext}
import player.Life
import player.weapon.RangedWeapon

/** An entity that has its own life status.
  *
  * Every entity that inherits from this trait has a life attribute attached to it.
  * For listening to the life status of the entity, see the delegates in the life property.
  */
trait LivingEntity extends Entity with AttackContainer {

  /** The life of the entity. */
  protected val life: Life

  /** The life of the entity. */
  def getLife = { life }

  // Every living entity can be attacked with weapons, so every weapon
  // should have a visible effect on the living entity.

  onTurnCycleEnded += { () =>
     updateQueues()
     if (this.isInstanceOf[Player]) {
         if (life.getLife + life.getLifeRegeneration > Player.MAXIMUM_LIFE.get)
            life.setLife(Player.MAXIMUM_LIFE.get)
         else
             life.setLife(life.getLife + Player.LIFE_REGENERATION.get)
     }
     if (this.isInstanceOf[Bot]) {
        if (life.getLife + life.getLifeRegeneration > Bot.MAXIMUM_LIFE.get)
           life.setLife(Bot.MAXIMUM_LIFE.get)
        else
           life.setLife(life.getLife + Bot.LIFE_REGENERATION.get)
     }
  }

  onDamage += { event =>
     var damage: Double = 0
     var defence: Double = 0

     // counting every defence value of every piece of armour together and save it in defence
     if (this.isInstanceOf[HasArmor]) {
        val combatant: HasArmor = this.asInstanceOf[HasArmor]
        combatant.armorParts.foreach { armorOption =>
           defence = defence + armorOption.get.getDefence(event.weapon.getArmingType)
        }
     }

     if (event.weapon.isInstanceOf[RangedWeapon])
        damage = event.weapon.asInstanceOf[RangedWeapon].damageAt(getGridX, getGridY)
     else
        damage = event.weapon.getAttackValue * PfeileContext.DAMAGE_MULTI.get

     // prohibiting possible healing on attack
     if (defence > damage)
        damage = 0

     // finally, the life need to be changed
     life.changeLife(- damage + defence)

     LogFacility.log(s"Impacting attack: by ${event.aggressor} to " +
           s"${event.destination.toString} with ${event.weapon.getName}. " +
           s"[Damage " + damage + " | Defence: " + defence + "]", "Debug", "Attack")
  }
}

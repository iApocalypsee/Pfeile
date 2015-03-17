package newent

import general.{Main, LogFacility, PfeileContext}
import player.Life
import player.armour.Armour
import player.weapon.{RangedWeapon, Weapon}

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


     // This counts the defence. The implementation under this part should be equal (but shorter and faster), but doesn't work
     if (this.isInstanceOf[Combatant]) {
        this.asInstanceOf[Combatant].getEquipment match {
           case armour: HasArmor =>
              armour.armorParts.foreach { armour: Option[Armour] =>
                 if (armour != None)
                    defence = defence + armour.get.getDefence(event.weapon.getArmingType)
              }
           case weapons: HasWeapons =>
              weapons.weapons.foreach { weapon: Option[Weapon] =>
                 if (weapon != None)
                    defence = defence + weapon.get.getDefence(event.weapon.getArmingType)
              }
           case _ =>
        }
     }

     // counting every defence value of every piece of armour together and save it in defence

     /* FIXME Medieval Equipment and EquipmentStrategy cause an StackOverflowException
     if (this.isInstanceOf[Combatant]) {
        val combatant: Combatant = this.asInstanceOf[Combatant]
        combatant.equipment.equippedItems.foreach { equippableItem: EquippableItem =>
           equippableItem match {
              case armour: Armour => defence = defence + armour.getDefence(event.weapon.getArmingType)
              case weapon: Weapon => defence = defence + weapon.getDefence(event.weapon.getArmingType)
              case _ =>
           }
        }
     }
     */

     if (event.weapon.isInstanceOf[RangedWeapon])
        damage = event.weapon.asInstanceOf[RangedWeapon].damageAt(getGridX, getGridY)
     else
        damage = event.weapon.getAttackValue * PfeileContext.DAMAGE_MULTI.get

     Main.getContext.getTurnSystem.teams.apply().foreach { team: Team =>
        if (team.isInTeam(event.aggressor))
            damage = damage * team.getExtraDamage
     }

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

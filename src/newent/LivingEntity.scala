package newent

import general.{LogFacility, Main, PfeileContext}
import player.Life
import player.armour.Armour
import player.item.{Item, BagOfLoots, EquippableItem}
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
         if (life.getLife + life.getLifeRegeneration > Player.maximumLife.get)
            life.setLife(Player.maximumLife.get)
         else
             life.setLife(life.getLife + Player.lifeRegeneration.get)
     }
     if (this.isInstanceOf[Bot]) {
        if (life.getLife + life.getLifeRegeneration > Bot.maximumLife.get)
           life.setLife(Bot.maximumLife.get)
        else
           life.setLife(life.getLife + Bot.lifeRegeneration.get)
     }
  }

  onDamage += { event =>
     var damage: Double = 0
     var defence: Double = 0

     // counting every defence value of every piece of armour together and save it in defence

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

     // the following code adds a bagOfLoots with the content, the Entity has carried before its death, to WorldLootList.
     val bagOfLoots: BagOfLoots = new BagOfLoots(this)

     if (this.isInstanceOf[Combatant]) {
       val combatant: Combatant = this.asInstanceOf[Combatant]
       life.onDeath.register { () =>
         combatant.equipment.equippedItems.foreach { (item: EquippableItem) =>
           bagOfLoots.add(item)
         }
       }
     }

     if (this.isInstanceOf[InventoryEntity]) {
       val inventory: InventoryLike = this.asInstanceOf[InventoryEntity].inventory
       life.onDeath.register { () =>
         inventory.items.foreach { (item: Item) =>
           bagOfLoots.add(item)
         }
       }
     }

     if (bagOfLoots.getStoredItems.size() > 0) {
       Main.getContext.getWorldLootList.add(bagOfLoots)
     }

     //*/

     if (event.weapon.isInstanceOf[RangedWeapon])
        damage = event.weapon.asInstanceOf[RangedWeapon].damageAt(getGridX, getGridY)
     else
        damage = event.weapon.getAttackValue * PfeileContext.damageMultiplicator.get

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
           s"${event.target.toString} with ${event.weapon.getName}. " +
           s"[Damage " + damage + " | Defence: " + defence + "]", "Debug", "Attack")
  }
}

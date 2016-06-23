package newent

import general.{LogFacility, Main, PfeileContext}
import gui.screen.GameScreen
import newent.event.AttackEvent
import player.Life
import player.item.Item
import player.item.loot.BagOfLoots
import player.weapon.RangedWeapon

import scala.collection.JavaConversions

/**
  * An entity that has its own life status.
  *
  * Every entity that inherits from this trait has a life attribute attached to it.
  * For listening to the life status of the entity, see the delegates in the life property.
  */
trait LivingEntity extends Entity with AttackContainer {

  /** The life of the entity. */
  val life: Life

  /** The life of the entity. */
  def getLife = life

  /**
    * Override this method to add items, which drop, if the LivingEntity dies. For example, a wolf doesn't have an
    * inventory, but should drop tooth/fur. If the method is not overridden, it returns an empty Seq.
    */
  def additionalDrops = Seq.empty[Item]

  /** Returns <code>additionalDrops</code> as JavaList.  */
  final def additionalDropsAsJava = JavaConversions.seqAsJavaList(additionalDrops)

  /** Every living entity can be poisoned. The implementation of the LivingEntity itself has to instance the value. */
  val poison: Poison

  /** This returns the class, which handles the damage taken by the LivingEntity. It also keeps track of the amount of
    * poison and the poison resistance.
    *
    * @return the Poison class of this LivingEntity
    */
  def getPoisonStat = poison

  /**
    * Puts the loot bag for this entity into the world.
    */
  private def putLootBag(): Unit = {
    val bagOfLoots = new BagOfLoots(this)

    for(item <- additionalDrops)
      bagOfLoots.add(item)

    // only drop a loot, if it has content.
    if (bagOfLoots.getStoredItems.size() > 0) {
      Main.getContext.getWorldLootList.add(bagOfLoots)
    }
  }

  // If a living entity is attacked, the poison effect must be triggered.
  onImpact += { att: AttackEvent =>
    getPoisonStat.poisoned(att.weapon.getPoisonedAmount)
  }

  onDamage += { event =>

    val rawDefense = this match {
      case c: Combatant => c.equipment.defense(event.weapon.getArmingType)
      case anythingElse => 0.0
    }

    val rawDamage = event.weapon match {
      case ranged: RangedWeapon => ranged.damageAt(getGridX, getGridY)
      case anythingElse => event.weapon.getAttackValue * PfeileContext.damageMultiplicator() * event.aggressor.belongsTo.team.getExtraDamage
    }

    val resultingDamage = if(rawDefense > rawDamage) 0.0 else rawDamage - rawDefense

    life.changeLife(-resultingDamage)

    LogFacility.log(s"Impacting attack " + name + ": by ${event.aggressor} to " +
      s"${event.target.toString} with ${event.weapon.getName}. " +
      s"[Damage " + rawDamage + " | Defence: " + rawDefense + "]", "Debug", "Attack")
  }

  life.onDeath += { () =>
    putLootBag()
    GameScreen.getInstance().setWarningMessage("Dropped Loot: " + name)
  }

  onTurnCycleEnded += { () =>
    updateQueues()
    life.updateLife()
  }

}

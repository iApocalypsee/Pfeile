package newent

import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, Vector => _, _}

import newent.event.EquipInformation
import player.armour._
import player.item.EquippableItem
import player.weapon.{ArmingType, Weapon}

import scala.beans.BeanProperty
import scala.collection.JavaConverters._
import scala.compat.java8.OptionConverters._
import scala.compat.java8._

/**
  * Class collectively used for storing equippable items and performing operations
  * on the whole set of them.
  */
abstract class EquipmentStrategy {

  /**
    * Returns all equipped items this equipment strategy is holding right now.
    */
  def equippedItems = Seq.empty[EquippableItem]

  /**
    * Applies given equipment to
    * @param x
    */
  def equip(x: EquipInformation): Boolean = {
    val canEquip = equipLogic.isDefinedAt(x)
    if (canEquip) {
      equipLogic(x)
    }
    canEquip
  }

  protected def equipLogic = PartialFunction.empty[EquipInformation, Unit]

  /**
    * Returns all equipped items this equipment strategy is holding right now.
    */
  def getEquippedItems = equippedItems.asJava

  /**
    * Calculates the total defense for every equipped item in this equipment set.
    * @param x The type of weapon for which the defense should be calculated.
    * @return The total defense for given attack type.
    */
  def defense(x: ArmingType) = equippedItems.collect({ case d: Defence => d }).foldLeft(0.0)({
    case (carry, defense) => carry + defense.getDefence(x)
  })

}

trait HasArmor extends EquipmentStrategy {

  override def equippedItems = availableArmor ++: super.equippedItems

  private var m_head = Option.empty[HeadArmour]
  private var m_body = Option.empty[BodyArmour]
  private var m_arm = Option.empty[ArmArmour]
  private var m_leg = Option.empty[LegArmour]

  def head = m_head
  def body = m_body
  def arm = m_arm
  def leg = m_leg

  def getHead = m_head.asJava
  def getBody = m_body.asJava
  def getArm = m_arm.asJava
  def getLeg = m_leg.asJava

  /**
    * Returns every piece of armor directly as their [[scala.Option]] objects.
    * This means that even though the [[scala.Option]]s are listed, it is not mandatory for
    * the associated part to exist as well.
    *
    * Use [[newent.HasArmor#availableArmor()]] instead if you want to collect every
    * piece of armor that is currently available.
    */
  def armorParts: Seq[Option[Armour]] = Vector(head, arm, body, leg)

  /**
    * @see [[newent.HasArmor#armorParts()]]
    */
  def getArmorParts: IList[Optional[Armour]] = armorParts.map(_.asJava).asJava

  /**
    * Collects every piece of armor that is not [[scala.None]].
    */
  def availableArmor: Seq[Armour] = for (part <- armorParts; if part.isDefined) yield part.get

  /**
    * @see [[newent.HasArmor#availableArmor()]]
    */
  def getAvailableArmor: IList[Armour] = availableArmor.asJava

  override protected def equipLogic = super.equipLogic.orElse {
    case EquipInformation(newHead: HeadArmour) => m_head = Option(newHead)
    case EquipInformation(newBody: BodyArmour) => m_body = Option(newBody)
    case EquipInformation(newArm: ArmArmour)   => m_arm = Option(newArm)
    case EquipInformation(newLeg: LegArmour)   => m_leg = Option(newLeg)
  }

}

trait HasWeapons extends EquipmentStrategy {

  /**
    * Returns all equipped items the equipment strategy is holding right now.
    */
  override def equippedItems: Seq[EquippableItem] = availableWeapons ++: super.equippedItems

  override protected def equipLogic = super.equipLogic.orElse {
    case EquipInformation(newWeapon: Weapon) => primaryWeapon = Option(newWeapon)
  }

  /**
    * The primary weapon that the equipment has.
    * '''Do not. Assign it null. I will shoot you.''' Assign it [[general.JavaInterop#scalaNone()]]
    * if no primary weapon exists.
    */
  @BeanProperty var primaryWeapon: Option[Weapon] = None

  /**
    * The secondary weapon that the equipment has.
    * '''Do not. Assign it null. I will shoot you.''' Assign it [[general.JavaInterop#scalaNone()]]
    * if no secondary weapon exists.
    */
  @BeanProperty var secondaryWeapon: Option[Weapon] = None

  /**
    * See definition in [[newent.HasArmor]].
    */
  def weapons: Seq[Option[Weapon]] = Seq(primaryWeapon, secondaryWeapon)

  def getWeapons: IList[Optional[Weapon]] = weapons.map(_.asJava).asJava

  /**
    * See definition in [[newent.HasArmor]].
    */
  def availableWeapons: Seq[Weapon] = for (weapon <- weapons; if weapon.isDefined) yield weapon.get

  def getAvailableWeapons: IList[Weapon] = availableWeapons.asJava

}

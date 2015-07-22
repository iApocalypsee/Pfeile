package newent

import newent.event.EquipInformation
import player.armour._
import player.item.EquippableItem
import player.weapon.Weapon

import scala.beans.BeanProperty
import scala.collection.JavaConversions

/**
  * Created by jolecaric on 03/03/15.
  */
abstract class EquipmentStrategy {

  /**
    * Returns all equipped items the equipment strategy is holding right now.
    */
  def equippedItems: Seq[EquippableItem] = Seq()

  def equip(x: EquipInformation): Unit = {}

  /** Java-interop method returning a Java list. */
  def getEquippedItems = JavaConversions.seqAsJavaList(equippedItems)

}

trait HasArmor extends EquipmentStrategy {

  override def equippedItems: Seq[EquippableItem] = availableArmor ++: super.equippedItems

  override def equip(x: EquipInformation): Unit = x.equippedItem match {
    case h: HeadArmour => head = Some(h)
    case a: ArmArmour => arm = Some(a)
    case b: BodyArmour => body = Some(b)
    case l: LegArmour => leg = Some(l)
    case _ => super.equip(x)
  }

  @BeanProperty var head: Option[HeadArmour] = None

  @BeanProperty var arm: Option[ArmArmour] = None

  @BeanProperty var body: Option[BodyArmour] = None

  @BeanProperty var leg: Option[LegArmour] = None

  /**
    * Returns every piece of armor directly as their [[scala.Option]] objects.
    * This means that even though the [[scala.Option]]s are listed, it is not mandatory for
    * the associated part to exist as well.
    *
    * Use [[newent.HasArmor#availableArmor()]] instead if you want to collect every
    * piece of armor that is currently available.
    */
  def armorParts = Seq(head, arm, body, leg)

  /**
    * @see [[newent.HasArmor#armorParts()]]
    */
  def getArmorParts = JavaConversions.seqAsJavaList(armorParts)

  /**
    * Collects every piece of armor that is not [[scala.None]].
    */
  def availableArmor: Seq[EquippableItem] = for (part <- armorParts; if part.isDefined) yield part.get

  /**
    * @see [[newent.HasArmor#availableArmor()]]
    */
  def getAvailableArmor = JavaConversions.seqAsJavaList(availableArmor)

}

trait HasWeapons extends EquipmentStrategy {

  /**
    * Returns all equipped items the equipment strategy is holding right now.
    */
  override def equippedItems: Seq[EquippableItem] = availableWeapons ++: super.equippedItems

  override def equip(x: EquipInformation): Unit = x.equippedItem match {
    case weapon: Weapon => primaryWeapon = Some(weapon)
    case _ => super.equip(x)
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
  def weapons = Seq(primaryWeapon, secondaryWeapon)

  /**
    * See definition in [[newent.HasArmor]].
    */
  def availableWeapons: Seq[Weapon] = for (weapon <- weapons; if weapon.isDefined) yield weapon.get

  def getAvailableWeapons = JavaConversions.seqAsJavaList(availableWeapons)

}

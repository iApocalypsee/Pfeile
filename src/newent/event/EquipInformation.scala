package newent.event

import player.item.EquippableItem

/**
  * Holds any information that an entity needs when it is re-equipping itself.
  * @param equippedItem The item to be equipped by the entity.
  */
case class EquipInformation(equippedItem: EquippableItem) {

  /** Java-interop. */
  def getEquippedItem = equippedItem

}

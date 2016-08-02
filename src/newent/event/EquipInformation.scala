package newent.event

import player.item.EquippableItem

/**
  * Holds any information that an entity needs when it is re-equipping itself.
  * This information might be including, but not limited to,
  *  - The item with which the entity is equipping itself
  *  - Has the item been gifted by anyone?
  *
  * @param equippedItem The item to be equipped by the entity.
  */
case class EquipInformation(equippedItem: EquippableItem) {

  def getEquippedItem = equippedItem

}

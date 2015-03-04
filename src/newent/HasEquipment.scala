package newent

trait HasEquipment {

  val equipment: EquipmentStrategy
  def getEquipment = equipment

}

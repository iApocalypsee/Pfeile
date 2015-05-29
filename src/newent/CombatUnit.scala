package newent

trait TargetUnit extends Entity with AttackContainer with InventoryEntity with LivingEntity with
  MovableEntity with VisionEntity with TeleportableEntity

trait CombatUnit extends TargetUnit with Combatant

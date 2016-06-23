package newent

/**
  * Common trait for units eligible for attacks.
  */
trait TargetUnit extends Entity with AttackContainer with InventoryEntity with LivingEntity with
  MovableEntity with VisionEntity

/**
  * Common trait for units eligible to perform attacks on targets.
  * Mixing in this trait implies that subclass can be targeted by attacks as well.
  */
trait CombatUnit extends TargetUnit with Combatant

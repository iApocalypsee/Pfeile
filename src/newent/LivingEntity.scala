package newent

import player.Life

/** An entity that has its own life status.
  *
  * Every entity that inherits from this trait has a life attribute attached to it.
  * For listening to the life status of the entity, see the delegates in the life property.
  */
trait LivingEntity extends Entity {

  /** The life of the entity. */
  val life: Life

  /** called when the entity is killed. If the entity was a player, you need to register a function to GameOverScreen or GameWonScreen */
  val onDeath = general.Delegate.createZeroArity
}

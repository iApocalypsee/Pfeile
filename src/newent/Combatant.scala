package newent

/** Represents a full combatant with all aspects of combat combined. <p>
  *
  * Well, not <b>all</b> aspects are implemented yet, but there are more to come.
  * This trait is just a standard.
  *
  */
trait Combatant extends AttackContainer with Aggressor

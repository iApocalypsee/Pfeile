package newent

/** Represents a full combatant with all aspects of combat combined. <p>
  *
  * Well, not <b>all</b> aspects are implemented yet, but there are more to come.
  * This trait is just a standard.
  */
trait Combatant extends AttackContainer with HasEquipment with Aggressor {

  // By default, every combatant incorporates a medieval equipment set.
  // We decided to do so.
  override val equipment = new MedievalEquipment

}

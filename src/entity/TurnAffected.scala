package entity

/**
 * Represents an object which state is going to change by ending
 * the turn in the game.
 * @author Josip
 */
trait TurnAffected {

  /**
   * The method which executes when the turn of the player or an entity attached to that player
   * ends.
   */
  def turnover: Unit

}

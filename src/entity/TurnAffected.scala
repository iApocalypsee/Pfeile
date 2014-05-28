package entity

/**
 * Represents an object which state is going to change by ending
 * the turn in the game.
 * @author Josip
 */
trait TurnAffected {

  def turnover: Unit

}

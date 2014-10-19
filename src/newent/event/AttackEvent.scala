package newent.event

import newent.{Aggressor, AttackContainer}
import player.weapon.Weapon
import world.TileLike

import scala.math._

/**
 *
 * @author Josip Palavra
 */
final case class AttackEvent(weapon: Weapon, departure: TileLike, destination: AttackContainer, aggressor: Aggressor,
                             travelSpeed: Double) {

  require(weapon ne null)
  require(departure ne null)
  require(destination ne null)
  require(aggressor ne null)

  lazy val geographicalLengthSq = pow(departure.latticeX - destination.getGridX, 2) +
    pow(departure.latticeY - destination.getGridY, 2)

  lazy val geographicalLength = sqrt(geographicalLengthSq)

  //lazy val lengthPerTurn = geographicalLength / travelSpeed

}

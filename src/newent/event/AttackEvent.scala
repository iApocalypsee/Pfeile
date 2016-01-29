package newent.event

import java.awt.Point

import comp.DisplayRepresentable
import newent.{Aggressor, AttackContainer}
import player.weapon.Weapon
import world.Tile

import scala.math._

/** Event representing an attack.
  *
  * @param weapon The weapon with which the attack is performed.
  * @param departure The tile from where the attack started.
  * @param target The target.
  * @param aggressor Ditto.
  * @param travelSpeed The amount of tiles that the attack progresses per turn.
  */
final case class AttackEvent(weapon: Weapon, departure: Tile, target: AttackContainer, aggressor: Aggressor,
                             travelSpeed: Double) {

  require(weapon != null)
  require(departure != null)
  require(target != null)
  require(aggressor != null)

  /** The geographical length, easured in tiles.
    *
    * The length is just squared, not square-rooted yet. Squarerooting is expensive in computer
    * programming, so I don't want to use it every time when I can just use the squared value.
    */
  lazy val geographicalLengthSq = pow(departure.getGridX - target.getGridX, 2) +
    pow(departure.getGridY - target.getGridY, 2)

  /** The squarerooted geographical length. (in Tiles) */
  lazy val geographicalLength = sqrt(geographicalLengthSq)

  /** the length in GUI values between the departure Position and the destination position */
  def lengthGUI = target match {
    // If the destination can be represented by a component, then calculate it.
    case x: DisplayRepresentable =>
      def rectBoundsOf(d: DisplayRepresentable) = d.component.getBounds.getBounds

      val depart_cx = rectBoundsOf(departure).getCenterX
      val depart_cy = rectBoundsOf(departure).getCenterY
      val dest_cx = rectBoundsOf(x).getCenterX
      val dest_cy = rectBoundsOf(x).getCenterY
      sqrt(pow(depart_cx - dest_cx, 2) + pow(depart_cy - dest_cy, 2))
    case _ => throw new UnsupportedOperationException(s"GUI length calculation not available on type ${target
      .getClass.getName}!")
  }

  def destination = new Point(target.getGridX, target.getGridY)
}

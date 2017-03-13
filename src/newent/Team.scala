package newent

import general.LogFacility
import general.JavaInterop._
import general.JavaInterop.JavaPrimitives._
import general.JavaInterop.Implicits._

import scala.collection.JavaConverters._
import scala.compat.java8._
import FunctionConverters._
import OptionConverters._
import java.util.function._
import java.util.{Collection => ICollection, Deque => IDeque, List => IList, Map => IMap, Queue => IQueue, Set => ISet, _}

/**
  * Object being able of grouping entities together which mix in the [[newent.CanHoldTeamContract]] trait.
  * @since 28.12.14
  */
sealed abstract class Team protected {

  private var m_members = new HashSet[CanHoldTeamContract]

  /** the extra damage is added as multiplier to the damage */
  private var extraDamage: Double = 1

  def members: ISet[CanHoldTeamContract] = m_members.toImmutableSet
  def getMembers: ISet[CanHoldTeamContract] = members

  /**
    * Causes the specified object to join the team.
    * @param x The object to be integrated into the team.
    */
  def integrate(x: CanHoldTeamContract): Boolean = {
    require(x != null)
    m_members.add(x)
  }

  def kick(x: CanHoldTeamContract): Boolean = !kick(_ == x).isEmpty

  /**
    * Removes all team members who satisfy given predicate, and returns these in a list.
    */
  def kick(selector: Predicate[CanHoldTeamContract]): IList[CanHoldTeamContract] = {
    val iter = m_members.asScala
    val compare = iter.clone()
    iter.retain(selector.negate.asScala)
    compare.diff(iter).asJavaCollection.toList
  }

  def isInTeam(x: CanHoldTeamContract): Boolean = m_members.contains(x)

  /** Returns true if this team is the barbarian one. (--> CommandTeam -> false)*/
  def isBarbarian: Boolean

  /**
    * Returns this team instance as a command team.
    * If this instance is not a CommandTeam instance, it will return null.
    * @return This object as a CommandTeam instance, if possible.
    */
  def asCommandTeam: CommandTeam

  /**
   * Essentially returns the same as [[asCommandTeam]], but packed in an option to reduce the
   * risk of potential NullPointerExceptions.
   * @return An option with this team as a command team, if this object really is a CommandTeam object.
   */
  def asCommandTeamOpt = Option(asCommandTeam)

  /**
    * The extra damage, that every member of this team does.
    * <code>damage = damage * (extraDamage + 1)</code>
    *
    * Notice, that <code>team.setExtraDamage(team.getExtraDamage())</code> would increase the extra damage by <code>1</code>.
    * So you changing the extra damage based on the old extra damage need to be coded like this:
    * <code>team.setExtraDamage((team.getExtraDamage - 1) + newExtraDamage)</code>
    *
    * @param extraDamage the relative extra damage like: 0.2 --> damage * 1.2
    */
  def setExtraDamage(extraDamage: Double) = {
    require(extraDamage >= -1, "Extra damage [" + extraDamage + "] must be higher than -1 to prohibit healing on attack")
    this.extraDamage = extraDamage + 1
  }

  /**
    * Every team can make extra damage because of special effects. This is the multiplier: <code>damage = damage * getExtraDamage() </code>
    *
    * @return the multiplier for the damage
    */
  def getExtraDamage = extraDamage

}

/**
  * All entities that are not part of any team are collected in this object.
  * Every member of this team considers every other entity as its enemy, regardless of
  * whether he is a member of this team or not.
  */
object BarbarianTeam extends Team {

  override def isBarbarian = true

  // Implementation of considering every other entity as hostile except for themselves...
  override def isInTeam(x: CanHoldTeamContract) = x.belongsTo.team == this

  override def asCommandTeam = null

}

/**
  * A team that is led by a player.
  * This is one of the two ways a team can exist. A player controls the team overall
  * and decides the name for it.
  * I will implement more for this class as the mechanics of the game develop.
  *
  * @param head The player that is leading the team.
  * @param name The name of the team.
  */
class CommandTeam(val head: Player, val name: String) extends Team {

  // This is the only way of injecting the player object into the members
  // list, since the overridden function does not accept players.
  super.integrate(head)

  override def isBarbarian = false

  /**
   * Causes the specified object to join the team.
   * This function __denies any players to join the team except for its head player.__
   * @param x The object to be integrated into the team. If it is
   *          an object of type [[newent.Player]], it is ignored.
   */
  override def integrate(x: CanHoldTeamContract): Boolean = {
    if (!x.isInstanceOf[Player] || x == head)
      super.integrate(x)
    else {
      LogFacility.log(x.toString + " can not be added to command team " + name, "Info")
      false
    }
  }

  def getHead = head
  def getName = name

  override def asCommandTeam = this

  override def toString: String = "CommandTeam[Head: " + head.name + "]"

}


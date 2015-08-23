package newent

import general.LogFacility

/**
  * Created by jolecaric on 28.12.14.
  */
sealed abstract class Team protected {

  private var _members = List[CanHoldTeamContract]()

  /** All members of the team. */
  def members = _members
  def getMembers = members

  /**
    * Causes the specified object to join the team.
    * @param x The object to be integrated into the team.
    */
  def integrate(x: CanHoldTeamContract): Unit = {
    require(x != null)
    _members = _members ++ List(x)
    x.onJoinedTeam(this)
  }

  /* Not yet, implementation follows later on...
  /**
   * Kicks the specified object out of the team.
   * @param x The team member to kick.
   */
  def kick(x: CanHoldTeamContract) = kick(_ == x)
  def kick(selector: CanHoldTeamContract => Boolean): Unit = {
    _members = _members filterNot selector
  }
  */

  def isInTeam(x: CanHoldTeamContract): Boolean = _members.contains(x)

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

  /** the extra damage is added as multiplier to the damage */
  private var extraDamage: Double = 1

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

  // Implementation of considering every other entity as hostile...
  override def isInTeam(x: CanHoldTeamContract) = false

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
  override def integrate(x: CanHoldTeamContract): Unit = {
    if (!x.isInstanceOf[Player] || x == head) super.integrate(x)
    else LogFacility.log(x.toString + " can not be added to team " + name, "Info")
  }

  def getHead = head
  def getName = name

  override def asCommandTeam = this

  override def toString: String = "CommandTeam[Head: " + head.name + "]"

}


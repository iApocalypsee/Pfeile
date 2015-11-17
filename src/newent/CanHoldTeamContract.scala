package newent

import general.Delegate

/**
  * Any object that can accept, decline and cancel teaming contracts.
  */
trait CanHoldTeamContract {

  /** Called when this object joined a team. */
  val onJoinedTeam = Delegate.create[Team]

  /*
  /**
    * Called when this object has left a team.
    * The corresponding event object is guaranteed to hold valid objects in its attributes.
    * When the new team is the barbarian team, it means that this entity "deserted" and does not
    * belong to any team anymore except for the barbarian team.
    */
  val onLeftTeam = Delegate.create[TeamChangeEvent]
  */

  /** The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning.
    * Defaults to the barbarian team.
    */
  protected def initialTeam: Team = BarbarianTeam

  // This field only exists becaus I want the "initialTeam" function to call only once.
  private val _initialTeamForOnceCalled = initialTeam

  private var _boundTo = TeamContract(_initialTeamForOnceCalled, this)

  join(_initialTeamForOnceCalled)

  /**
   * Controls whether this object wants to team up with a certain group.
   * If this function returns `false` for a certain team, this object will not
   * join the specified team.
   * @param team The team to check for.
   * @return A boolean value as described above.
   */
  def isJoinFeasible(team: Team): Boolean = true

  /**
    * Joins a certain team.
    *
    * @param x The object to team up with.
    */
  def join(x: Team): Unit = {
    x.integrate(this)
  }

  /** Returns the contract to which this object is bound. */
  def belongsTo = _boundTo
  def getBelongsTo = belongsTo

  // Every time the object joins a team, the contract variable has to be changed.
  onJoinedTeam += { t =>
    // Only if the team is a different one, assign a new contract
    if(_boundTo.team != t) {
      _boundTo = TeamContract(t, this)
    }
  }

}

case class TeamChangeEvent(oldTeam: Team, newTeam: Team)

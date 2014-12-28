package newent

import general.Delegate

/**
  * Any object that can accept (and decline) teaming contracts.
  *
  */
trait CanHoldTeamContract {

  private var _boundTo = TeamContract(initialTeam, this)

  join(initialTeam)

  /** The initial team with which the object begins to cooperate.
    * Can be overridden to join a different team in the beginning. */
  protected def initialTeam: Team = BarbarianTeam

  /**
   * Controls whether this object wants to team up with a certain group.
   * If this function returns `false` for a certain team, this object won't
   * join the team.
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

  /** Called when this object joined a team. */
  val onJoinedTeam = Delegate.create[Team]

  // Every time the object joins a team, the contract variable has to be changed.
  onJoinedTeam += { t =>
    // Only if the team is a different one, assign a new contract
    if(_boundTo.team != t) {
      _boundTo = TeamContract(t, this)
    }
  }

}

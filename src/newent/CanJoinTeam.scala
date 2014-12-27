package newent

/**
  * Represents an object that can only hold one team contract at a time.
  */
trait CanJoinTeam extends CanHoldTeamContract {

  private var _boundTo: TeamContract = NoTeamContract

  /** The contract to which this object is bound to. */
  def boundTo = _boundTo

  // Java interop.
  def getBoundTo = boundTo

  override protected[newent] def removeContract(x: TeamContract = boundTo): Unit = {
    require(x == boundTo, "Team contract does not correspond to the CanJoinTeam object.")
    _boundTo = NoTeamContract
  }

  /**
    * Gives the opportunity to subclasses to write the now established contract somewhere.
    * @param x The established contract.
    */
  override protected def writeContract(x: TeamContract): Unit = _boundTo = x

  override def askPermissionForContract(team: CanHoldTeamContract): Boolean = _boundTo == NoTeamContract
}

package newent

/**
  * Represents a collection of team contracts that are rooted together towards the team.
  */
class Team extends CanHoldTeamContract {

  private var _contracts = List[TeamContract]()

  override def dissolveContract(x: TeamContract): Unit = {
    require(isContractPresent(x), s"Contract $x is not listed in team $this.")
    removeContract(x)
  }

  /**
    * Checks if a contract is present at all.
    * @param x The contract to check for.
    * @return A boolean value indicating whether the contract is present in the team's contract listing.
    */
  private def isContractPresent(x: TeamContract) = _contracts contains x

  /**
    * Removes the specified contract.
    * @param x The contract to delete.
    */
  private def removeContract(x: TeamContract) = _contracts = _contracts.filterNot { _ == x }

  /**
    * Gives the opportunity to subclasses to write the now established contract somewhere.
    * @param x The established contract.
    */
  override protected def writeContract(x: TeamContract): Unit = _contracts = _contracts ++ List(x)

  /** The contracts that the team currently holds. */
  def contracts = _contracts
}

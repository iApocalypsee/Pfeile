package newent

/**
  * Represents a collection of team contracts that are rooted together towards the team.
  */
class Team(val name: String) extends CanHoldTeamContract {

  private var _contracts = List[TeamContract]()

  override protected[newent] def removeContract(x: TeamContract): Unit = {
    if (!isContractPresent(x)) return
    filterContractOut(x)
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
  private def filterContractOut(x: TeamContract) = _contracts = _contracts.filterNot { _ == x }

  /**
    * Gives the opportunity to subclasses to write the now established contract somewhere.
    * @param x The established contract.
    */
  override protected def writeContract(x: TeamContract): Unit = _contracts = _contracts ++ List(x)

  /** The contracts that the team currently holds. */
  def contracts = _contracts

  /**
    * Finds any contractors under this team that satisfy the given function and returns them
    * in a new list.
    * @param f The selector function.
    * @return The contractors that satisfy the function predicate.
    */
  def findContractors(f: CanHoldTeamContract => Boolean): List[CanHoldTeamContract] = {

    def findMatches(x: TeamContract) = {
      var returnedList: List[CanHoldTeamContract] = Nil
      if (f(x.side1)) returnedList = returnedList ++ List(x.side1)
      if (f(x.side2)) returnedList = returnedList ++ List(x.side2)
      returnedList
    }

    contracts.foldLeft(List[CanHoldTeamContract]()) { _ ++ findMatches(_) }
  }
}

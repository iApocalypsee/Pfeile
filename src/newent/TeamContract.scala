package newent

/** The teaming contract. */
case class TeamContract(side1: CanHoldTeamContract, side2: CanHoldTeamContract) {

  /**
   * Returns the opposite partner of the contract.
   * @param x The object to check for.
   * @return The opposite partner of the contract.
   */
  def viceVersaSide(x: CanHoldTeamContract): CanHoldTeamContract = {
    if(!isMentioned(x)) Null_CanHoldTeamContract
    else if(x == side1) side2
    else side1
  }

  /**
   * Returns true if the parameter is equal to either of the two signors of the contract.
   * @param x The object to check for whether it is in the contract.
   * @return A boolean value.
   */
  def isMentioned(x: CanHoldTeamContract): Boolean = x == side1 || x == side2

  def dissolve(): Unit = {
    side1.removeContract(this)
    side2.removeContract(this)
  }

}

/** Singleton representing no contract that binds the [[newent.CanJoinTeam]] */
object NoTeamContract extends TeamContract(Null_CanHoldTeamContract, Null_CanHoldTeamContract)
package newent

import general.LogFacility

/**
  * Any object that can accept (and decline) teaming contracts.
  *
  */
trait CanHoldTeamContract {

  def askPermissionForContract(team: CanHoldTeamContract): Boolean = true

  /**
    * Signs a team contract with the specified object.
    *
    * @param x The object to team up with.
    */
  def sign(x: CanHoldTeamContract): Unit = {
    // Do not even try to sign contracts with yourself.
    if (x == this) return
    // Does the opponent partner want to sign a contract at all?
    val isContractFeasible = x.askPermissionForContract(this)
    if (!isContractFeasible) return
    // Establish the teaming contract.
    val contract = TeamContract(this, x)
    this writeContract contract
    x writeContract contract
  }

  /**
    * Disables the given contract.
    * This method gives just the opportunity to subclasses to remove the contract properly.
    * It is __important__, that this method is <b>not called from inside the class!</b>
    * @param x The contract to cancel.
    */
  protected[newent] def removeContract(x: TeamContract): Unit

  /**
    * Gives the opportunity to subclasses to write the now established contract somewhere.
    * @param x The established contract.
    */
  protected def writeContract(x: TeamContract): Unit

}

/** An object that is actually not capable of binding itself to a contract. */
case object Null_CanHoldTeamContract extends CanHoldTeamContract {

  private def onCalled = LogFacility.log("Called Null object of [[CanHoldTeamContract]]")

  /**
    * Disables the given contract.
    * @param x The contract to cancel.
    */
  override protected[newent] def removeContract(x: TeamContract): Unit = onCalled

  /**
    * Gives the opportunity to subclasses to write the now established contract somewhere.
    * @param x The established contract.
    */
  override protected def writeContract(x: TeamContract): Unit = onCalled
}

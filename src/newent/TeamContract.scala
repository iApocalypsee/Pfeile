package newent

/** The teaming contract. */
case class TeamContract(side1: CanHoldTeamContract, side2: CanHoldTeamContract)

/** Singleton representing no contract that binds the [[newent.CanJoinTeam]] */
case object NoTeamContract extends TeamContract(Null_CanHoldTeamContract, Null_CanHoldTeamContract)
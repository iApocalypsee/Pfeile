package general

import newent.{CommandTeam, Team}

import scala.collection.JavaConversions

/**
  * Takes care of that the turn mechanics apply correctly to the players.
  * @constructor Creates a new turn system with the attached player list.
  * @param teams Function that returns the player list.
  *                   The player list needs to be pulled every time freshly.
  *                   This class cannot rely on a static reference, it needs to
  *                   have fresh information about who is still in the game
  *                   and who is already out.
  * @param teamToBeginIndex The index of the player to begin. Defaults to the
  *                           first player mentioned in the list (has the index 0).
  */
class TurnSystem(val teams: () => Seq[Team], teamToBeginIndex: Int = 0) {

  /**
   * Called when a turn has been ended.
   */
  val onTurnEnded = Delegate.create[Team]

  /**
   * Called when it's another player's turn.
   */
  val onTurnGet = Delegate.create[Team]

  /**
   * Called when every player completed their moves in this turn.
   */
  val onGlobalTurnCycleEnded = Delegate.createZeroArity

  /**
    * The player that is currently able to do actions.
    */
  private var _currentPlayer = teams().apply(teamToBeginIndex)

   /**
    * The list of all teams
    *
    * @return <code>JavaConversions.seqAsJavaList(teams().apply())</code>
    */
  def getTeams = JavaConversions.seqAsJavaList(teams.apply())

   /**
    * The list of teams is searched for the team of the active player.
    *
    * @return the CommandTeam of <code>Main.getContext().getActivePlayer()</code>
    */
  def getTeamOfActivePlayer: CommandTeam = {
     val activePlayer = Main.getContext.getActivePlayer
     teams.apply().foreach { team: Team =>
        if (team.isInTeam(activePlayer))
           return team.asInstanceOf[CommandTeam]
     }
     throw new RuntimeException("The team of the active player " + activePlayer.name + " could not be found!")
  }

  /**
    * The player that currently holds the turn.
    */
  def currentPlayer = _currentPlayer
  def getCurrentPlayer = currentPlayer

  /**
    * Causes the turn system to assign the next player in the cycle as
    * the new current player.
    */
  def increment(): Unit = {
    onTurnEnded(_currentPlayer)
    val (nextPlayer, turnCycleCompleted) = findNextFrom(_currentPlayer)
    _currentPlayer = nextPlayer
    if(turnCycleCompleted) onGlobalTurnCycleEnded()
    onTurnGet(_currentPlayer)
  }

  /**
    * Finds the player that follows the given player in the turn system.
    * @param x The player to look for his next.
    * @return A tuple containing the next player that should get the next turn and
    *         a boolean value indicating whether a complete turn cycle is completed or not.
    */
  private def findNextFrom(x: Team): (Team, Boolean) = {
    require(x != null)

    val list = teams()
    val searchFromIndex = list.indexOf(x)

    assume(searchFromIndex != -1, "No known player given to turn system.")

    val isOutOfRange = searchFromIndex + 1 >= list.size
    if (isOutOfRange) (list.head, true)
    else (list(searchFromIndex + 1), false)
  }

  private object TurnCycleOperations {

    var count = 0

    onGlobalTurnCycleEnded += { () =>
      count += 1
      if(count >= PfeileContext.turnsPerRound()) {
        onReachedTurnCycleThreshold()
        reset()
      }
    }

    private def reset() = {
      count = 0
    }

    def onReachedTurnCycleThreshold(): Unit = {
      // TODO Code to execute.
    }

  }

}

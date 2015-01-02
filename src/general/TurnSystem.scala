package general

import newent.Player

/**
  * Takes care of that the turn mechanics apply correctly to the players.
  * @constructor Creates a new turn system with the attached player list.
  * @param playerList Function that returns the player list.
  *                   The player list needs to be pulled every time freshly.
  *                   This class cannot rely on a static reference, it needs to
  *                   have fresh information about who is still in the game
  *                   and who is already out.
  * @param playerToBeginIndex The index of the player to begin. Defaults to the
  *                           first player mentioned in the list (has the index 0).
  */
class TurnSystem(val playerList: () => Seq[Player], playerToBeginIndex: Int = 0) {

  /**
    * The player that is currently able to do actions.
    */
  private var _currentPlayer = playerList().apply(playerToBeginIndex)

  /**
    * Called when a turn has been ended.
    */
  val onTurnEnded = Delegate.create[Player]

  /**
    * Called when it's another player's turn.
    */
  val onTurnGet = Delegate.create[Player]

  /**
    * Called when every player completed their moves in this turn.
    */
  val onGlobalTurnCycleEnded = Delegate.createZeroArity

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
  private def findNextFrom(x: Player): (Player, Boolean) = {
    require(x != null)

    val list = playerList()
    val searchFromIndex = list.indexOf(x)

    assume(searchFromIndex != -1, "No known player given to turn system.")

    val isOutOfRange = searchFromIndex + 1 >= list.size
    if (isOutOfRange) (list(0), true)
    else (list(searchFromIndex + 1), false)
  }

}

package general

import gui.screen.ArrowSelectionScreenPreSet
import newent.{CommandTeam, Team}

import scala.collection.JavaConversions
import scala.collection.JavaConverters._

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

  onTurnEnded += { team =>
    team.asCommandTeam.head.onTurnEnded
  }

  /**
    * Called when it's another player's turn.
    */
  val onTurnGet = Delegate.create[Team]

  /**
    * Called when every player completed their moves in this turn.
    */
  val onGlobalTurnCycleEnded = Delegate.createZeroArity

  /**
    * A TurnCycle ends, when every team has ended its turn; A Round ends, when several turnCycles end (defined by
    * <code>Main.getContext().turnsPerRound().get()</code> - by default 10). <p>
    * After each Round, a new RoundChest spawns and ArrowSelectionScreePreSet is entered to select new arrowsPreSet.
    */
  val roundOperations = new RoundOperations

  /**
    * A TurnCycle ends, when every team has ended its turn; A Round ends, when several turnCycles end (defined by
    * <code>Main.getContext().turnsPerRound().get()</code> - by default 10). <p>
    * After each Round, a new RoundChest spawns and ArrowSelectionScreePreSet is entered to select new arrowsPreSet.
    */
  def getRoundOperations: RoundOperations = roundOperations

  /**
    * The player that is currently able to do actions.
    */
  private var _currentPlayer = firstTeam

  /**
    * The list of all teams
    *
    * @return <code>JavaConversions.seqAsJavaList(teams().apply())</code>
    */
  def getTeams = JavaConversions.seqAsJavaList(teams())

  /**
    * The list of teams is searched for the team of the active player.
    *
    * @return the CommandTeam of <code>Main.getContext().getActivePlayer()</code>
    */
  def getTeamOfActivePlayer: CommandTeam = {
    val activePlayer = Main.getContext.getActivePlayer
    teams().collect({case x: CommandTeam => x}).find(team => team.isInTeam(activePlayer)) getOrElse {
      throw new RuntimeException("The team of the active player " + activePlayer.name + " could not be found!")
    }
  }

  def getCommandTeams = commandTeams.asJava

  /**
   * @return a List with all CommandTeams. Should have size 2 with two players...
   */
  def commandTeams = teams().collect({ case x: CommandTeam => x })

  def getHeadOfCommandTeams = headOfCommandTeams.asJava

  /** returns a sequence with every head of a commandTeam. */
  def headOfCommandTeams = commandTeams.map(team => team.head)

  /**
    * The player that currently holds the turn.
    */
  def currentTeam = _currentPlayer
  def getCurrentTeam = currentTeam

  /**
    * Causes the turn system to assign the next player in the cycle as
    * the new current player.
    */
  def increment(): Unit = {
    onTurnEnded(_currentPlayer)
    val (nextPlayer, turnCycleCompleted) = findNextFrom(_currentPlayer)
    _currentPlayer = nextPlayer
    if (turnCycleCompleted) onGlobalTurnCycleEnded()
    onTurnGet(_currentPlayer)
  }

  /**
    * Returns the next team in the list without assigning the turn to the next team.
    * @return The next team for turn.
    */
  def peekNext: Team = findNextFrom(currentTeam)._1

  /**
    * Returns the first team in the turn system.
    * @return The first team in the turn system.
    */
  def firstTeam = teams()(teamToBeginIndex)

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

  /**
    * A TurnCycle ends, when every team has ended its turn; A Round ends, when several turnCycles end (defined by
    * <code>Main.getContext().turnsPerRound().get()</code> - by default 10). <p>
    * After each Round, a new RoundChest spawns and ArrowSelectionScreePreSet is entered to select new arrowsPreSet.
    */
  class RoundOperations {

    /**
      * Called, when a round ends. A round ends, when <code>Main.getContext().turnsPerRound().get()</code> turn cycles
      * have been ended. By default it is 10.
      */
    val onRoundEnded = Delegate.createZeroArity

    var count = 0

    onGlobalTurnCycleEnded += { () =>
      count += 1
      if (count >= PfeileContext.turnsPerRound()) {
        onRoundEnded.apply()
      }
    }

    onRoundEnded += { () =>
      if (PfeileContext.arrowNumberPreSet() != 0) {
        ArrowSelectionScreenPreSet.getInstance().setActivePlayer(firstTeam.asCommandTeam.getHead)
        Main.getGameWindow.getScreenManager.requestScreenChange(ArrowSelectionScreenPreSet.SCREEN_INDEX)
      }
      reset()
    }

    private def reset() = {
      count = 0
    }

  }
}

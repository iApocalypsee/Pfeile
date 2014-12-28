package newent

import general.LogFacility

/**
 * Every entity that can earn gold should inherit from this trait.
 */
trait GoldEarner extends Entity {

  val money = Money

  object Money {

    require(initialAssets >= 0)
    require(initialGoldPerTurn >= 0)

    private var _gpt = initialGoldPerTurn
    private var _assets = initialAssets

    /** The amount of gold that the earner gets every turn. */
    def goldPerTurn = _gpt
    /** The amount of gold that the earner possesses. */
    def assets = _assets
    
    def getGoldPerTurn = goldPerTurn
    def getAssets = assets

    /**
     * Spends the specified amount of money (what it really does is it is just subtracting
     * the amount to spend from the current assets).
     * @param amount The amount to spend.
     * @return A boolean value indicating whether the amount could be spent or not.
     */
    def spend(amount: Int): Boolean = {
      if(_assets - amount < 0) false
      else {
        _assets -= amount
        true
      }
    }

    // Adds the promised gold per turn to the earner's purse
    private def mineAssets(): Unit = {
      _assets += _gpt
      LogFacility.log("Gave " + _gpt + " gold to entity " + GoldEarner.this.toString)
    }

    onTurnCycleEnded += { () =>
      mineAssets()
    }

  }

  /** The initial gold per turn amount that the earner gets. __Must not be below 0__. */
  protected def initialGoldPerTurn: Int
  /** The initial amount of gold that the earner gets. __Must not be below 0__. */
  protected def initialAssets: Int

}

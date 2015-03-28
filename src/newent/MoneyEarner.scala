package newent

import general.LogFacility
import player.item._
import player.item.coin._

import scala.collection.JavaConversions

/**
  * Every entity that can earn gold should inherit from this trait.
  * TODO Align with coin system.
  */
trait MoneyEarner extends Entity with InventoryEntity {

  val purse = Purse

  object Purse {

    require(initialMoney >= 0, s"@[[GoldEarner]]: Cannot start with debt of $initialMoney")
    require(initialMoneyPerTurn >= 0, s"@[[GoldEarner]]: Cannot start off with negative income of $initialMoneyPerTurn")

    /**
      * Calculates the total worth of the purse (all coins' values added together).
      * @return How much money the purse is worth.
      */
    def numericValue = money.foldLeft(0)((value, coin) => value + coin.getValue)

    private var _gpt = initialMoneyPerTurn
    private var _money = initialMoney

    /** The amount of gold that the earner gets every turn. */
    def moneyPerTurn = _gpt

    /**
      * Sets how much money the money earner gets per turn.
      * @param x The amount of money the earner gets per turn.
      */
    def moneyPerTurn_=(x: Int): Unit = {
      _gpt = x
      LogFacility.log(s"${this} earning ${_gpt} money per turn now", "Debug")
    }

    /** The amount of gold that the earner has. */
    def money: Seq[Coin] = inventory.items.collect {
      case c: Coin => c
    }

    def getMoneyPerTurn = moneyPerTurn
    def getMoney = JavaConversions.seqAsJavaList(money)

    /**
      * Spends the specified amount of money (what it really does is it is just subtracting
      * the amount to spend from the current assets).
      * @param amount The amount to spend.
      * @return A boolean value indicating whether the amount could be spent or not.
      */
    def spend(amount: Int): Boolean = {
      if (numericValue - amount < 0) false
      else ???
    }

    def give(amount: Int): Unit = {
      inventory.put(amount of new BronzeCoin)
      LogFacility.log(s"Gave $amount money to $this")
    }

    // Adds the promised gold per turn to the earner's purse
    private def mineAssets(): Unit = {
      LogFacility.log(s"Mining money for $this...")
      give(_gpt)
    }

    onTurnCycleEnded += { () =>
      mineAssets()
    }

  }

  /** The initial gold per turn amount that the earner gets. __Must not be below 0__. */
  protected def initialMoneyPerTurn: Int
  /** The initial amount of gold that the earner gets. __Must not be below 0__. */
  protected def initialMoney: Int

}

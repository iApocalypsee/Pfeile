package newent

import general.LogFacility
import general.LogFacility.LoggingLevel
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

    require(initialMoney >= 0, s"@[[MoneyEarner]]: Cannot start with debt of $initialMoney")
    require(initialMoneyPerTurn >= 0, s"@[[MoneyEarner]]: Cannot start off with negative income of $initialMoneyPerTurn")

    /**
     *
      * Calculates the total worth of the purse (all coins' values added together).
      * @return How much money the purse is worth.
      */
    def numericValue = CoinHelper.getValue(getMoney)

    private var _gpt = initialMoneyPerTurn
    private var _money = initialMoney

    /** The amount of gold that the earner gets every turn. */
    def moneyPerTurn = _gpt

    /**
      * Sets how much money the money earner gets per turn.
      * @param x The amount of money the earner gets per turn.
      */
    def setMoneyPerTurn(x: Int): Unit = {
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
       if (numericValue - amount < 0)
          false
       else {
          var leftToSpend = amount

          inventory.items.foreach {item: Item =>
             if (item.isInstanceOf[Coin]) {
                if (item.asInstanceOf[Coin].getValue <= leftToSpend) {
                   if (inventory.javaItems.remove(item)) {
                      leftToSpend = leftToSpend - item.asInstanceOf[Coin].getValue
                   } else
                      LogFacility.log("Cannot remove " + item.asInstanceOf[Coin] + " from the inventory of " + this, LoggingLevel.Error)
                }
             }
          }
          if (leftToSpend == 0)
             return true

          // if you still need to spend a small amount, but you only have large coins, you need to exchange these coins

          // this is the coin, that need to be exchanged to BronzeCoins
          var coin: Coin = null

          inventory.items.foreach{item: Item =>
             if (item.isInstanceOf[Coin]) {
                // That's what I mean
                if (item.asInstanceOf[Coin].getValue > leftToSpend) {
                   coin = item.asInstanceOf[Coin]
                }
             }
          }

          if (inventory.javaItems.remove(coin)) {
             leftToSpend = leftToSpend - coin.getValue
          } else
             LogFacility.log("Cannot remove " + coin + " from the inventory of " + this, LoggingLevel.Error)

          // leftToSpend is negative now. This amount need to be added to the inventory again.
          CoinHelper.getCoins(-leftToSpend).foreach { coin: BronzeCoin =>
             if(!inventory.put(coin)) {
                if (!inventory.put(new BronzeCoin()))
                   LogFacility.log("Cannot put a " + coin + " into the inventory. " + this + " lost " + coin.getValue + " money.", LoggingLevel.Error)
             }
          }

          true
       }
    }

    def give(amount: Int): Unit = {
      inventory.put(amount of new BronzeCoin)
      LogFacility.log(s"Gave $amount money to $this", LoggingLevel.Info)
    }

    def give (coins: java.util.List[Coin]): Unit = {
       for (i <- 0 until coins.size())
          inventory.put(coins.get(i))
       LogFacility.log("Gave " + CoinHelper.getValue(coins) + " money to " + this, LoggingLevel.Info)
    }

    def give (coins: Array[Coin]): Unit = {
       for(i <- 0 until coins.length)
          inventory.put(coins.apply(i))

       // FIXME @iApocalypsee: the line below works in java, but not in scala
       // LogFacility.log("Gave " + CoinHelper.getValue(coins) + " money to " + this, LoggingLevel.Info)
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

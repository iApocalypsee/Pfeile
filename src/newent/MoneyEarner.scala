package newent

import general.{Delegate, LogFacility}
import general.LogFacility.LoggingLevel
import player.item.coin._

import scala.collection.{JavaConversions, mutable}

/**
  * Every entity that can earn gold should inherit from this trait.
  * TODO Align with coin system.
  */
trait MoneyEarner extends Entity with InventoryEntity {

  /**
    * Money manager of this object.
    */
  val purse = new Purse

   /**
    * @return kind of Money manager
    */
  def getPurse = purse

  /**
    * Transaction manager of this object.
    */
  val account = new Transactions

   /**
    * @return transaction manager
    */
  def getAccount = account

  /** <b>Call this delegate</v> */
  val onMoneyChanged = Delegate.createZeroArity

  //<editor-fold desc='Money holding'>

  /**
    * Singleton managing the money of this object.
    */
  class Purse {

    require(initialMoney >= 0, s"@[[MoneyEarner]]: Cannot start with debt of $initialMoney")
    require(initialMoneyPerTurn >= 0, s"@[[MoneyEarner]]: Cannot start off with negative income of $initialMoneyPerTurn")

    /**
      *
      * Calculates the total worth of the purse (all coins' values added together).
      * @return How much money the purse is worth.
      */
    def numericValue = CoinHelper.getValue(getCoins)

    private var _gpt = initialMoneyPerTurn
    private var _money = initialMoney

    /** The amount of gold that the earner gets every turn. */
    def moneyPerTurn = _gpt
    def getMoneyPerTurn = moneyPerTurn

    /**
      * Sets how much money the money earner gets per turn.
      * @param x The amount of money the earner gets per turn.
      */
    def moneyPerTurn_=(x: Int) = {
      _gpt = x
      LogFacility.log(s"${this} earning ${_gpt} money per turn now", "Debug")
    }
    def setMoneyPerTurn(x: Int): Unit = moneyPerTurn_=(x)

    /** The amount of gold that the earner has. */
    def coins: Seq[Coin] = inventory.items.collect {
      case c: Coin => c
    }

    def getCoins = JavaConversions.seqAsJavaList(coins)

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

        coins.foreach { coin =>
          if (coin.getValue <= leftToSpend) {
            if (inventory.remove(_ == coin).isDefined) {
              leftToSpend = leftToSpend - coin.getValue
            }
            else
              LogFacility.log("Cannot remove "+coin+" from the inventory of "+this, LoggingLevel.Error)
          }
        }

        if (leftToSpend == 0) {
           onMoneyChanged.apply()
           return true
        }


        // if you still need to spend a small amount, but you only have large coins, you need to exchange these coins

        // this is the coin, that need to be exchanged to BronzeCoins
        var coin: Coin = null

        coins.foreach { iterCoin =>
          // That's what I mean
          if (iterCoin.getValue > leftToSpend) {
            coin = iterCoin
          }
        }

        if (inventory.javaItems.remove(coin)) {
          leftToSpend = leftToSpend - coin.getValue
        }
        else LogFacility.log("Cannot remove "+coin+" from the inventory of "+this, LoggingLevel.Error)

        // leftToSpend is negative now. This amount need to be added to the inventory again.
        CoinHelper.getCoins(-leftToSpend).foreach { coin: BronzeCoin =>
          if (!inventory.put(coin)) {
            if (!inventory.put(new BronzeCoin()))
              LogFacility.log("Cannot put a "+coin+" into the inventory. "+this+" lost "+coin.getValue+" money.", LoggingLevel.Error)
          }
        }
        onMoneyChanged.apply()
        true
      }
    }

    /**
      * Gives a list of coins to the money earner.
      * @param coins The list of coins to give to the money earner.
      */
    def give(coins: scala.Iterable[Coin]): Unit = if(coins.nonEmpty) {
      for (coin <- coins) inventory.put(coin)
      onMoneyChanged.apply()
      LogFacility.log("Gave "+CoinHelper.getValue(coins)+" money to "+this, LoggingLevel.Info)
    }

    /**
      * Gives `amount` bronze coins to the money earner.
      * @param amount The amount of bronze coins to give the earner.
      */
    def give(amount: Int): Unit = give(List.fill(amount)(new BronzeCoin))

    /**
      * Gives a java collection of coins to the earner. Java-interop.
      * @param coins The list of coins to give to the earner.
      */
    def give(coins: java.util.Collection[Coin]): Unit = give(JavaConversions.collectionAsScalaIterable(coins))

    /**
      * Gives an array of coins to the earner. Since Array is not bound into the Java collection type hierarchy,
      * this method exists for interop.
      * @param coins The array of coins to give to the earner.
      */
    def give(coins: Array[Coin]): Unit = give(coins.toIterable)

    // Adds the promised gold per turn to the earner's purse
    private def mineAssets(): Unit = {
      LogFacility.log(s"Mining money for $this...")
      give(_gpt)
      onMoneyChanged.apply()
    }

    onTurnCycleEnded += { () =>
      mineAssets()
    }

  }

  //</editor-fold>

  //<editor-fold desc='Monetary obligation system'>

  /**
    * Class that keeps track of the money earner's spendings and savings.
    */
  class Transactions {

    private[this] def emptyBuffer = mutable.ArrayBuffer[Transaction]()

    /**
      * Internal method for registering a transaction to the buffer.
      * @param transaction Ditto.
      */
    private[MoneyEarner] def register(transaction: Transaction): Unit = {
      emptyBuffer += transaction
    }

    /**
      * Actual logic for a successful transaction.
      * This method assumes that the payer has enough coins to fulfill the transaction.
      * @param transaction The transaction to execute.
      */
    private def handshake(transaction: Transaction): Unit = {
      assume(transaction.sender.purse.numericValue >= transaction.amount)

      val sender = transaction.sender
      val receiver = transaction.receiver

      sender.account.register(transaction)
      receiver.account.register(transaction)

      sender.purse.spend(transaction.amount)
      receiver.purse.give(transaction.amount)



    }

    /**
      * Actual logic for a transaction which cannot be made by the payer right now.
      * This method assumes that the payer has not enough coins to participate in the transaction.
      * @param transaction The transaction which cannot be made.
      */
    // TODO Implement this method.
    private def debtObligation(transaction: Transaction): Unit = ???

    /**
      * Pays an amount of coins to the receiver.
      * @param amount The numeric value of coins to transfer to the receiver.
      * @param receiver Ditto.
      * @return Was the transaction successful?
      */
    def pay(amount: Int, receiver: MoneyEarner): Boolean = {
      val moneyEarner: MoneyEarner = MoneyEarner.this
      val transaction = Transaction(moneyEarner, receiver, amount)
      val isAffordable = moneyEarner.purse.numericValue >= amount

      isAffordable match {
        case true => handshake(transaction)
        case false => debtObligation(transaction)
      }

      isAffordable
    }

  }

  //</editor-fold>

  //<editor-fold desc='MoneyEarner trait arguments'>

  /** The initial gold per turn amount that the earner gets. __Must not be below 0__. */
  protected def initialMoneyPerTurn: Int
  /** The initial amount of gold that the earner gets. __Must not be below 0__. */
  protected def initialMoney: Int

  //</editor-fold>

}

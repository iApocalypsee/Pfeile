package newent

import general.{Delegate, Property}
import player.item._
import player.item.coin._

import scala.annotation.tailrec
import scala.beans.BeanProperty
import scala.collection.JavaConversions._
import scala.collection.{JavaConversions, mutable}

/**
  * Every entity that can earn gold should inherit from this trait.
  * TODO Align with coin system.
  */
trait MoneyEarner extends Entity with InventoryEntity {

  val onMoneyChanged = Delegate.createZeroArity

  /**
    * Money manager of this object.
    */
  @BeanProperty val purse = new Purse

  require(initialMoney >= 0, s"@[[MoneyEarner]]: Cannot start with debt of $initialMoney")
  require(initialMoneyPerTurn >= 0, s"@[[MoneyEarner]]: Cannot start off with negative income of $initialMoneyPerTurn")

  purse.give(initialMoney)

  /**
    * Transaction manager of this object.
    */
  @BeanProperty val account = new Transactions
  
  //<editor-fold desc='Money holding'>

  /**
    * Singleton managing the money of this object.
    */
  class Purse {

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
      //LogFacility.log(s"${this} earning ${_gpt} money per turn now", "Debug")
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

        @tailrec
        def recur(moneyLeft: Int): Unit = {
          if(moneyLeft <= 0) return

          val sortedCoins = CoinHelper.getSortedCoins(inventory.javaItems)
          val bronzes = sortedCoins(0).toList
          val silvers = sortedCoins(1).toList
          val golds = sortedCoins(2).toList
          val platins = sortedCoins(3).toList

          val bronzePayable = if(moneyLeft < bronzes.size) moneyLeft else bronzes.size

          def cascade() = {
            if(bronzes.size == 0) {
              // Switch to silver coins
              if(silvers.size == 0) {
                // Switch to golds
                if(golds.size == 0) {
                  // Switch to platins
                  if(platins.size == 0) {
                    throw new RuntimeException("Not enough money but check still passed. New type of coin?")
                  } else {
                    val convertedGoldAmount = PlatinumCoin.VALUE / GoldCoin.VALUE
                    inventory.remove(_ == platins(0), 1)
                    inventory.put(convertedGoldAmount of new GoldCoin)
                  }
                } else {
                  val convertedSilverAmount = GoldCoin.VALUE / SilverCoin.VALUE
                  inventory.remove(_ == golds(0), 1)
                  inventory.put(convertedSilverAmount of new SilverCoin)
                }
              } else {
                val convertedBronzeAmount = SilverCoin.VALUE / BronzeCoin.VALUE
                inventory.remove(_ == silvers(0), 1)
                inventory.put(convertedBronzeAmount of new BronzeCoin)
              }
            }
          }

          cascade()
          inventory.remove(_.isInstanceOf[BronzeCoin], bronzePayable)
          recur(moneyLeft - bronzePayable)
        }

        recur(amount)
        onMoneyChanged()
        true
      }
    }

    /**
      * Gives a list of coins to the money earner.
      * @param coins The list of coins to give to the money earner.
      */
    def give(coins: scala.Iterable[Coin]): Unit = if(coins.nonEmpty) {
      for (coin <- coins) inventory.put(coin)
      onMoneyChanged()
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
      //LogFacility.log(s"Mining money for $this...")
      give(_gpt)
    }

    onTurnCycleEnded += { () =>
      mineAssets()
    }

     override def toString: String = "Purse (of " + name + ")"
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

     override def toString: String = "Transaction (by " + name + ")"
  }



  //</editor-fold>

  //<editor-fold desc='MoneyEarner trait arguments'>

   override def toString: String = "MoneyEarner: " + name

   /** The initial gold per turn amount that the earner gets. __Must not be below 0__. */
  protected def initialMoneyPerTurn: Int
  /** The initial amount of gold that the earner gets. __Must not be below 0__. */
  protected def initialMoney: Int

  //</editor-fold>

}

/**
 * Contains static final Properties defined by <code>PreWindowScreen</code> dealing with money.
 */
object MoneyValues {

   /** The money a Player earns at the beginning of the game. Set by <code>PreWindowScreen</code>.*/
   val startMoney = Property.apply[java.lang.Integer](-1)

   /** The money a Player earns after each TurnCycle. Set by <code>PreWindowScreen</code>.*/
   val moneyPerTurn = Property.apply[java.lang.Integer](-1)
}

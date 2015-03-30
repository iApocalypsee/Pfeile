package player.item

import newent.InventoryLike

/**
  * Not supposed to be used by the outside.
  * The PutItem DSL should be something like that (just an example):
  * {{{
  *   inventory.put(42 of new BronzeCoin)
  * }}}
  * @param amount How many items should be transferred to the target inventory.
  *               Note that the final stage is calling the [[player.item.PutItemAmountDSLStage1#of(scala.Function0)]]
  *               method, which then in turn produces an object the inventory can read.
  */
class PutItemAmountDSLStage1(amount: Int) {
  def of(output: => Item) = new PutItemAmount(amount, output)
}

/**
  * Part of a DSL.
  *
  * The PutItem DSL should be something like that (just an example):
  * {{{
  *   inventory.put(42 of new BronzeCoin)
  * }}}
  * Of couse, this notation can only be used in Scala. If you really need this class in Java,
  * you can instantiate this class with
  * {{{
  *   new PutItemAmount(amount, JavaInterop.toScalaFunction(() -> object to be allocated <amount> times)
  * }}}
  * But in general, I am going to provide better usages than this one, so you should not be concerned about this class.
  * @param amount How many items should be transferred to the target inventory?
  * @param output The item to be transferred to the inventory.
  *               Note that the parameter is call-by-name, so an object allocation with `new` makes
  *               a lot of sense because the object is reallocated `amount` times.
  */
class PutItemAmount(amount: Int, output: => Item) {
  def putInto(inventory: InventoryLike): Unit = for (_ <- 0 until amount) inventory.put(output)
}

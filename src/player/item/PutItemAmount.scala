package player.item

import newent.InventoryLike

/**
 * Created by jolecaric on 28/03/15.
 */
class PutItemAmountDSLStage1(amount: Int) {
  
  def of(output: => Item) = new PutItemAmount(amount, output)
  
}

class PutItemAmount(amount: Int, output: => Item) {
  def putInto(inventory: InventoryLike): Unit = for(_ <- 0 until amount) inventory.put(output)
}

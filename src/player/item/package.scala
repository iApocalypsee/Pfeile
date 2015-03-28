package player

/**
 * Created by jolecaric on 28/03/15.
 */
package object item {

  implicit def intToPutItemAmount(x: Int): PutItemAmountDSLStage1 = new PutItemAmountDSLStage1(x)

}

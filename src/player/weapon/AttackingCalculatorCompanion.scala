package player.weapon

import java.awt.Point

import geom.functions.FunctionCollectionEasing
import player.weapon.arrow.AbstractArrow

import scala.annotation.tailrec

private[weapon] object AttackingCalculatorCompanion {

  import geom.Vector2

  @tailrec def sectionEndPoint(millisec: Int, carryVector: Vector2, attackingArrow: AbstractArrow, initialArrowPosition: Point,
                         timeMulti: Int, target: Aim, distanceToCover: Double): Vector2 = {

    if(millisec >= timeMulti / attackingArrow.getSpeed) carryVector
    else {

      val centerPoint = initialArrowPosition
      val posXOldCenter = centerPoint.x
      val posYOldCenter = centerPoint.y

      val posXAimCenter = target.getPosXGui
      val posYAimCenter = target.getPosYGui

      val acc = millisec / (timeMulti / attackingArrow.getSpeed)

      val cx = FunctionCollectionEasing.quadratic_easing_inOut(
        distanceToCover * acc, 0, posXAimCenter - posXOldCenter, distanceToCover
      )

      val cy = FunctionCollectionEasing.quadratic_easing_inOut(
        distanceToCover * acc, 0, posYAimCenter - posYOldCenter, distanceToCover
      )

      sectionEndPoint(millisec + 1, Vector2((posXOldCenter + cx).asInstanceOf[Float], (posYOldCenter + cy).asInstanceOf[Float]),
        attackingArrow, initialArrowPosition, timeMulti, target, distanceToCover)
    }
  }

}

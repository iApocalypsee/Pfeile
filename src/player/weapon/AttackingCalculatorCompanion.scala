package player.weapon


import geom.functions.FunctionCollectionEasing
import player.weapon.arrow.AbstractArrow

import scala.annotation.tailrec

private[weapon] object AttackingCalculatorCompanion {

  import geom.{Vector, Point}

  @tailrec def sectionEndPoint(millisec: Int, carryVector: Point, attackingArrow: AbstractArrow, initialArrowPosition: Point,
                         timeMulti: Int, target: Aim, distanceToCover: Double): Point = {

    if(millisec >= timeMulti / attackingArrow.getSpeed) carryVector
    else {

      val centerPoint = initialArrowPosition
      val posXOldCenter = centerPoint.getX
      val posYOldCenter = centerPoint.getY

      val posXAimCenter = target.getPosXGui
      val posYAimCenter = target.getPosYGui

      val acc = millisec / (timeMulti / attackingArrow.getSpeed)

      val cx = FunctionCollectionEasing.quadratic_easing_inOut(
        distanceToCover * acc, 0, posXAimCenter - posXOldCenter, distanceToCover
      )

      val cy = FunctionCollectionEasing.quadratic_easing_inOut(
        distanceToCover * acc, 0, posYAimCenter - posYOldCenter, distanceToCover
      )

      sectionEndPoint(millisec + 1, centerPoint + new Vector(cx, cy),
        attackingArrow, initialArrowPosition, timeMulti, target, distanceToCover)
    }
  }

}

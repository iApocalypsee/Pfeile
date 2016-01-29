/**
 *
 * @author Josip Palavra
 * @version 26.06.2014
 */

package object geom {
    val EPSILON = 1e-9
    def isEqual(x: Double, y: Double) = math.isZero(x - y)
    def isZero(x: Double) = math.abs(x) < EPSILON

    implicit def toPointDef(p: PointRef) = {
        new PointDef(p.getX, p.getY)
    }

    implicit def toPointRef(p: PointDef) = {
        new PointRef(p.getX, p.getY)
    }
}

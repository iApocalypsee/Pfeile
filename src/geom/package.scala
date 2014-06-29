/**
 *
 * @author Josip Palavra
 * @version 26.06.2014
 */
package object geom {
  implicit def toPointDef(p: PointRef) = {
    new PointDef(p.getX, p.getY)
  }

  implicit def toPointRef(p: PointDef) = {
    new PointRef(p.getX, p.getY)
  }
}

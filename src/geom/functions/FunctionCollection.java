package geom.functions;

/** This contains functions, that are not part of the {@link geom.functions.FunctionCollectionEasing}
 * ({@link geom.functions.FunctionCollectionEasing$}) easing (in/out) functions with acceleration/deceleration
 */
public class FunctionCollection {

    /** This returns the angle (in radiant not in degree) between the points (x1|y1) and (x2|y2).
     * It is in the range of <code>- Math.PI</code> and <code>Math.PI</code>, while <code>0</code> is no rotation.
     * A positive value means rotation clockwise and a negative one counterclockwise.
     *
     * @param x1 the x value of the first point
     * @param y1 the y value of the first point
     * @param x2 the x value of the second point
     * @param y2 the y value of the second point
     * @return the radiant angle between these points
     */
    public static double angle (double x1, double y1, double x2, double y2) {
        if (x2 > x1 && y2 < y1) {
            return Math.atan((x2 - x1) / (y1 - y2));
        } else if (x2 > x1 && y2 > y1) {
            return Math.toRadians(90.0) + Math.atan((y2 - y1) / (x2 - x1));
        } else if (x2 < x1 && y2 > y1) {
            return - (Math.toRadians(90.0) + Math.atan((y2 - y1) / (x1 - x2)));
        } else if (x2 < x1 && y2 < y1) {
            return - Math.atan((x1 - x2) / (y1 - y2));
        } else { // the special cases, where the point is placed either on the same x-position or on the same y-position
            if (y2 > y1 && x2 == x1) // a picture would be turned around
                return Math.toRadians(180.0);
            else if (x2 - x1 > 0 && y2 == y1) // the arrow_picture needs to be rotated clockwise (horizontally on the ground)
                return Math.toRadians(90.0);
            else if (x1 - x2 < 0 && y2 == y1) // the arrow needs to be rotated counterclockwise (horizontally on the ground)
                return - Math.toRadians(90.0);
            else if (y2 < y1 && x2 == x1)// the aim is directly over the tile, so no rotation is needed.
                return 0;
            else
                throw new IllegalArgumentException("There need to be a mistake in the implementation of FunctionCollection.angle! The (x1|y1)-Position and/or the (x2|y2)-Position is not possible. firstPosition: " + "( " + x1 + " | " + y1 + " )" + " secondPosition: " + "( " + x2 + " | " + y2 + " ).");
        }
    }
}

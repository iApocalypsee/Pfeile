package geom.functions;

import java.awt.*;

/** This contains functions, that are not part of the {@link geom.functions.FunctionCollectionEasing}
 * ({@link geom.functions.FunctionCollectionEasing$}) easing (in/out) functions with acceleration/deceleration
 */
@Deprecated
public class FunctionCollection {

    /** Creates a Polygon from every point: <p>
     * <code>Polygon poly = new Polygon();</code> <p>
     *     <code>for (Point point : points)   poly.addPoint(point.x, point.y);</code> <p>
     *     <code>return poly;</code>
     *
     * @return a polygon from an undefined number of points
     */
    public static Polygon createPolygon (Point... points) {
        Polygon poly = new Polygon();
        for (Point point : points)
            poly.addPoint(point.x, point.y);
        return poly;
    }

    /** The hypotenuse (without squareRooting it!) between the points (x1|y1) and (x2|y2).
     * If you just for example want to compare what the shorter distance is, much calculating time can be saved, by just calling
     * this method instead of the square rooted version <code>FunctionCollection.distance(...)</code>.
     *
     * @param x1 the x value of the first point
     * @param y1 the y value of the first point
     * @param x2 the x value of the second point
     * @param y2 the y value of the second point
     * @return the not square rooted distance between (x1|y1) and (x2|y2)
     */
    public static double distanceWithoutSqrt (double x1, double y1, double x2, double y2) {
        double changeX = x2 - x1;
        double changeY = y2 - y1;
        return changeX * changeX + changeY * changeY;
    }


    /** This calculates the hypotenuse of the points (x1|y1) and (x2|y2).
     * It is the square root of {@code distanceWithoutSqrt}: <code>Math.sqrt(FunctionCollection.hypotenuseSqrt(x1, y1, x2, y2))</code>.
     *
     * @param x1 the x value of the first point
     * @param y1 the y value of the first point
     * @param x2 the x value of the second point
     * @param y2 the y value of the second point
     * @return The distance between the two points (x1|y1) (x2|y2)
     */
    public static double distance (double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceWithoutSqrt(x1, y1, x2, y2));
    }

    /** This returns the angle (in radiant not in degree) between the points (x1|y1) and (x2|y2).
     * It is in the range of <code>- Math.PI</code> (not included) and <code>Math.PI</code> (included), while <code>0</code> is no rotation.
     * A positive value means rotation clockwise and a negative one counterclockwise.
     *
     * @param x1 the x value of the first point
     * @param y1 the y value of the first point
     * @param x2 the x value of the second point
     * @param y2 the y value of the second point
     * @return the radiant angle between these points
     * @throws java.lang.IllegalArgumentException if the points (x1|y1) (x2|y2) are on the same position: <code>x1 == x2 && y1 == y2</code>
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
                throw new IllegalArgumentException("The points are on the same position. The angle can't be calculated! \t" +
                        "(x|y): ( " + x1 + " | " + y1 + " ).");
        }
    }

    public static int clamp(int value, int min, int max) {
        if(value >= min && value <= max) return value;
        else if(value < min) return min;
        else return max;
    }

    public static int cycleUp(int value, int cap, int step) {
        while(value + step < cap) value += step;
        return value;
    }

    public static int cycle(int value, int cap, int step) {
        boolean cycleUp = value < cap;
        if(cycleUp) {
            value = cycleUp(value, cap, step);
        } else {
            value = cycleDown(value, cap, step);
        }
        return value;
    }

    public static int cycleDown(int value, int cap, int step) {
        while(value - step > cap) value -= step;
        return value;
    }
}

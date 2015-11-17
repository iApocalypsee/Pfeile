package newent;

import comp.AbstractDisplayRepresentable;
import general.Delegate;
import newent.event.LocationChangedEvent;
import player.BoardPositionable;
import world.TileLike;
import world.WorldLike;

import java.awt.*;
import java.util.Arrays;

/**
 * Superclass for every object that can be positioned on the map with no
 * constraints on the game object's actual size on the map.
 * @since 14.11.15
 */
public abstract class GameObject extends AbstractDisplayRepresentable implements BoardPositionable {

    private final boolean isFullOccupy;
    private final WorldLike world;
    private OccupyShape occupiedTiles;
    private int deltaX;
    private int deltaY;

    /**
     * Called when the position of this game object changes.
     */
    public final Delegate.Delegate<LocationChangedEvent> onLocationChanged = new Delegate.Delegate<>();

    public final Delegate.Function0Delegate onTurnCycleEnded = Delegate.createZeroArity();

    // <editor-fold desc="Constructors">

    private GameObject(OccupyShape shape, WorldLike world, int initX, int initY, boolean isFullOccupy) {
        super();
        this.occupiedTiles = shape;
        this.world = world;
        this.isFullOccupy = isFullOccupy;
        this.deltaX = initX;
        this.deltaY = initY;

        world.entities().register(this);
    }

    /**
     * Constructs a game object occupying the specified position and only the specified position.
     * Consequently, the map bounds of this object is a rectangle with width "1" and height "1".
     * @param x Self-explanatory.
     * @param y Ditto.
     * @param world The world to place the object in.
     */
    public GameObject(int x, int y, WorldLike world) {
        this(transformPointList(new int[]{0}, new int[]{0}), world, x, y, true);
    }

    /**
     * Constructs a game object occupying multiple tiles in a rectangular form.
     *
     * Note that the game object created with this constructor claim all tiles inside
     * the specified rectangle for themselves as well.
     * @param x1 The upper left border x position.
     * @param y1 The upper left border y position.
     * @param x2 The lower right border x position.
     * @param y2 The lower right border y position.
     * @param world The world to place this game object in.
     */
    public GameObject(int x1, int y1, int x2, int y2, WorldLike world) {
        this(x1, y1, x2, y2, world, true);
    }

    public GameObject(int x1, int y1, int x2, int y2, WorldLike world, boolean isFullOccupy) {
        this(transformPointList(
                new int[]{0, world.terrain().width() - x2, world.terrain().width() - x2, 0},
                new int[]{0, 0, world.terrain().height() - y2, world.terrain().height() - y2}),
                world, x1, y1, isFullOccupy);
    }

    public GameObject(int[] boundaryPointsX, int[] boundaryPointsY, int initX, int initY, WorldLike world) {
        this(boundaryPointsX, boundaryPointsY, initX, initY, world, true);
    }

    public GameObject(int[] boundaryPointsX, int[] boundaryPointsY, int initX, int initY, WorldLike world, boolean isFullOccupy) {
        this(transformPointList(boundaryPointsX, boundaryPointsY), world, initX, initY, isFullOccupy);
    }

    // </editor-fold>

    // <editor-fold desc="Getters">

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public int deltaX() {
        return getDeltaX();
    }

    public int deltaY() {
        return getDeltaY();
    }

    @Override
    public int getGridX() {
        return getDeltaX();
    }

    @Override
    public int getGridY() {
        return getDeltaY();
    }

    public WorldLike getWorld() {
        return world;
    }

    public WorldLike world() {
        return getWorld();
    }

    public TileLike getTileLocation() {
        return world.terrain().getTileAt(deltaX, deltaY);
    }

    public TileLike tileLocation() {
        return getTileLocation();
    }

    /**
     * Does this object take up its full shape on the map or does it only take up the borders
     * of its shape?
     */
    public boolean isFullOccupy() {
        return isFullOccupy;
    }

    // </editor-fold>

    // <editor-fold desc="Additional methods">

    // <editor-fold desc="Positional methods">

    /**
     * Determines if this object is currently occupying the specified field.
     * @param x Self-explanatory.
     * @param y Ditto.
     */
    public boolean isOccupyingPosition(int x, int y) {
        int checkX = (x & (world.terrain().width() - 1)) - deltaX;
        int checkY = (y & (world.terrain().height() - 1)) - deltaY;
        if(isFullOccupy) return occupiedTiles.isOccupyingPositionFull(checkX, checkY);
        else return occupiedTiles.isOccupyingPositionBorder(checkX, checkY);
    }

    public void move(int dx, int dy) {
        int oldDeltaX = this.deltaX;
        int oldDeltaY = this.deltaY;
        this.deltaX = (this.deltaX + dx) & (world.terrain().width() - 1);
        this.deltaY = (this.deltaY + dy) & (world.terrain().height() - 1);
        onLocationChanged.apply(new LocationChangedEvent(oldDeltaX, oldDeltaY, this.deltaX, this.deltaY, this));
    }

    public void place(int x, int y) {
        int oldDeltaX = this.deltaX;
        int oldDeltaY = this.deltaY;
        this.deltaX = x & (world.terrain().width() - 1);
        this.deltaY = y & (world.terrain().height() - 1);
        onLocationChanged.apply(new LocationChangedEvent(oldDeltaX, oldDeltaY, this.deltaX, this.deltaY, this));
    }

    // </editor-fold>

    /**
     * Callback for processing the turn of the game object.
     * This method is called immediately when the game object does not wait for any
     * input provided by users.
     * When this method gets called, the next game object's <tt>processTurn()</tt> is called. <br>
     *
     * <u>This method is not used yet.</u>
     */
    public void processTurn() {
    }

    // </editor-fold>

    private static class OccupyShape {
        private final Shape shape;
        private final int[] borderPointsX;
        private final int[] borderPointsY;

        public OccupyShape(Shape shape, int[] borderPointsX, int[] borderPointsY) {
            this.shape = shape;
            this.borderPointsX = borderPointsX;
            this.borderPointsY = borderPointsY;
        }

        public Shape getShape() {
            return shape;
        }

        public int[] getBorderPointsX() {
            return borderPointsX;
        }

        public int[] getBorderPointsY() {
            return borderPointsY;
        }

        public boolean isOccupyingPositionBorder(int x, int y) {
            return Arrays.stream(borderPointsX).anyMatch(p -> p == x) && Arrays.stream(borderPointsY).anyMatch(p -> p == y);
        }

        public boolean isOccupyingPositionFull(int x, int y) {
            return shape.contains(x, y);
        }
    }

    private static OccupyShape transformPointList(int[] xpoints, int[] ypoints) {
        if(xpoints.length != ypoints.length) throw new IllegalArgumentException("xpoints array and ypoints array have to have same length");
        final Polygon polygon = new Polygon();
        for(int i = 0; i < xpoints.length; i++) {
            polygon.addPoint(xpoints[i], ypoints[i]);
        }
        return new OccupyShape(polygon, xpoints, ypoints);
    }

}

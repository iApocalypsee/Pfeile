package general;

/**
 * @author Josip
 * @version 2/9/14
 */
public class WorldViewport {

    private static final int STD_SHIFT_X = 20;
    private static final int STD_SHIFT_Y = 20;
    public static final int STD_FIELD_DIMENSION = 64;

    private int shiftX = STD_SHIFT_X;
    private int shiftY = STD_SHIFT_Y;
    private World world;

    private float zoom = 1.0f;

    public WorldViewport(World world) {
        this.world = world;
    }

    /**
     * Returns the shifting of the map in x direction.
     * @return The shifting of the map in x direction.
     */
    public int getShiftX() {
        return shiftX;
    }

    /**
     * Returns the shifting of the map in y direction.
     * @return The shifting of the map in y direction.
     */
    public int getShiftY() {
        return shiftY;
    }

    /**
     * Shifts the map relatively.
     * @param dx The relative x movement.
     * @param dy The relative y movement.
     */
    public void shiftRel(int dx, int dy) {
        shiftX += dx;
        shiftY += dy;
        world.updateGUI();
    }

    /**
     * Shifts the map absolutly. 
     * @param x - The X-Position
     * @param y - the y-position
     */
    public void shiftAbs (int x, int y) {
    	shiftX = x; 
    	shiftY = y;
		world.updateGUI();
    }
    /**
     * Returns the zoom factor.
     * @return The zoom factor.
     */
    public float getZoomFactor() {
        return zoom;
    }

    /**
     * Returns the width of every field in pixels.
     * @return The width of every field in pixels.
     */
    public int getZoom() {
        return (int) (STD_FIELD_DIMENSION * zoom);
    }

    /**
     * Zooms the map relatively.
     * @param delta_zoom The relative zoom factor to increment with.
     */
    public void zoomRel(float delta_zoom) {
        zoom *= delta_zoom;
	    Mechanics.widthStretching = zoom;
	    Mechanics.heightStretching = zoom;
        world.updateGUI();
    }

    public void zoomAbs(float zoom) {
        this.zoom = zoom;
	    Mechanics.widthStretching = zoom;
	    Mechanics.heightStretching = zoom;
        world.updateGUI();
    }
}

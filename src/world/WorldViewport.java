package world;

/**
 * @author Josip
 * @version 2/9/14
 */
public class WorldViewport {

	private static final int STD_SHIFT_X = 20;
	private static final int STD_SHIFT_Y = 100;
	public static final int STD_TILE_ISO_DIMENSION = 64;
	/**
	 * The dimension of half a tile in width in pixels.
	 */
	public static final int TILE_PX_X_DIMENSION = 30;

	/**
	 * The dimension of half a tile in height in pixels.
	 */
	public static final int TILE_PX_Y_DIMENSION = 15;

	/**
	 * The ISO dimension, calculated out of the x and y dimensions.
	 */
	public static final int TILE_ISO_DIMENSION = (int) Math.sqrt(Math.pow(TILE_PX_X_DIMENSION, 2) + Math.pow(TILE_PX_Y_DIMENSION, 2));

	private int shiftX = STD_SHIFT_X;
	private int shiftY = STD_SHIFT_Y;

	/**
	 * The rotation of the map in degrees.
	 */
	private int rotation;

	/**
	 * The angle with which the camera is positioned above the map.
	 * If 90, then map is flat.
	 * Should not be anything greater than 90 or less than 0.
	 */
	private int povAngle;

	private IWorld world;

	private float zoom = 1f;

	public WorldViewport(IWorld world) {
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
		return (int) (STD_TILE_ISO_DIMENSION * zoom);
	}

	/**
	 * Zooms the map relatively.
	 * @param delta_zoom The relative zoom factor to increment with.
	 */
	public void zoomRel(float delta_zoom) {
		zoom *= delta_zoom;
		world.updateGUI();
	}

	public void zoomAbs(float zoom) {
		this.zoom = zoom;
		world.updateGUI();
	}

	public int getPovAngle() {
		return povAngle;
	}

	public int getRotation() {
		return rotation;
	}

	public int tileIsoHeight() {
		return TILE_PX_Y_DIMENSION * (getPovAngle() / 45);
	}

	public void setPovAngle(int povAngle) {
		povAngle %= 90;
		this.povAngle = povAngle;
		world.updateGUI();
	}

	public void setRotation(int rotation) {
		// convert the degree amount to a good value
		// It is not good to calculate with angles of 1020 or something alike
		while(rotation < 0) rotation += 360;
		if(rotation >= 360) rotation %= 360;
		this.rotation = rotation;
	}
}

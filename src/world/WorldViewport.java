package world;

import java.io.Serializable;

/**
 * @author Josip Palavra
 */
public class WorldViewport implements Serializable {

	// For serialization process. Don't delete this field.
	private static final long serialVersionUID = -6179718027934810706L;
	private float shiftX = 0;
    private float shiftY = 0;

	private float zoom = 1f;

    public WorldViewport() {
    }

    public float getShiftX() {
        return shiftX;
    }

    public float getShiftY() {
        return shiftY;
    }

    public void setShiftX(float shiftX) {
        this.shiftX = shiftX;
    }

    public void setShiftY(float shiftY) {
        this.shiftY = shiftY;
    }

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}
}

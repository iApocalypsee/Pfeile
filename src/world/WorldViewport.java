package world;

/**
 * @author Josip Palavra
 */
public class WorldViewport {

    private float shiftX = 0;
    private float shiftY = 0;

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
}

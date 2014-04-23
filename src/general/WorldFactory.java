package general;

/**
 * @author Josip
 * @version 2/9/14
 */
public class WorldFactory {

    private WorldFactory() {
    }

    public static synchronized World generateDefault(int sizeX, int sizeY) {
        return new World(sizeX, sizeY);
    }

    public static synchronized World generateDefault(int sizeX, int sizeY, long seed) {
        return new World(sizeX, sizeY, seed);
    }

}

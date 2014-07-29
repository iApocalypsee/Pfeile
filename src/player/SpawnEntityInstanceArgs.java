package player;

import general.World;

/**
 * @author Josip
 * @version 2/10/14
 */
public class SpawnEntityInstanceArgs {

    private World world;
    private int spawnX;
    private int spawnY;

    public SpawnEntityInstanceArgs() {
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }
}

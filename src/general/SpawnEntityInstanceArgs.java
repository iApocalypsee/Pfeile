package general;

public class SpawnEntityInstanceArgs {
	
	private int spawnX;
	private int spawnY;
	private World world;

	public void setSpawnX(int x) {
		spawnX = x;
	}
	
	public void setSpawnY(int y) {
		spawnY = y;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public int getSpawnX() {
		return spawnX;
	}
	
	public int getSpawnY() {
		return spawnY;
	}
	
	public World getWorld() {
		return world;
	}

}

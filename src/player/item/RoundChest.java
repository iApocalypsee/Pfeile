package player.item;

/**
 * A round chest is chest, which only appears once a round. It contains the most striking weapons, but it must be
 * protected by some creeps and spawn in a fair distance between the players or nearer to the disadvantaged player.
 * However, it might also trigger some bad, world-effecting catastrophe (in far feature...).
 */
public class RoundChest extends Chest {

    public RoundChest (int gridX, int gridY, String name) {
        super(gridX, gridY, "Round Chest");
    }

}

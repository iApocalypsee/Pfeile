package player.item;

/**
 * This is the usual chest (compared to {@link player.item.RoundChest}. It can be found and opened by players and bots.
 * */
public class DefaultChest extends Chest {

    public DefaultChest (int gridX, int gridY) {
        super(gridX, gridY, "Default Chest");
    }
}

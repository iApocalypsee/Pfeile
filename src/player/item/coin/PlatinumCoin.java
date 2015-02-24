package player.item.coin;

/**
 * A platinum coin is the most valuable coin in the game.
 */
public class PlatinumCoin extends Coin {
    public PlatinumCoin () {
        super("Platinum Coin");
    }

    @Override
    public int getValue () {
        return 400;
    }
}

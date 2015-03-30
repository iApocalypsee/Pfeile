package player.item.coin;

/**
 * A platinum coin is the most valuable coin in the game.
 */
public class PlatinumCoin extends Coin {
    public PlatinumCoin () {
        super("Platinum Coin");
    }

    /** the value of a platnumCoin is equal to 400 BronzeCoins */
    public static final int VALUE = 400;

    @Override
    public int getValue () {
        return VALUE;
    }
}

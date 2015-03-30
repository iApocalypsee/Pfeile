package player.item.coin;

/**
 * A little more valuable.
 */
public class SilverCoin extends Coin {
    public SilverCoin () {
        super("Silver Coin");
    }

    /** The value of a silver coin measured in number of BronzeCoins */
    public static final int VALUE = 10;

    @Override
    public int getValue () {
        return VALUE;
    }
}

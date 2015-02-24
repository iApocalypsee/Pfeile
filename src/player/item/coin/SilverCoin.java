package player.item.coin;

/**
 * A little more valuable.
 */
public class SilverCoin extends Coin {
    public SilverCoin () {
        super("Silver Coin");
    }

    @Override
    public int getValue () {
        return 10;
    }
}

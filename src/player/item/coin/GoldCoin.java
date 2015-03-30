package player.item.coin;

/**
 * Gold is already quite valuable.
 */
public class GoldCoin extends Coin {
    public GoldCoin () {
        super("Gold coin");
    }

    /** the value of GoldCoin measured in number of BronzeCoins. It's 80. */
    public static final int VALUE = 80;

    @Override
    public int getValue () {
        return VALUE;
    }
}

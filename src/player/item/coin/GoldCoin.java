package player.item.coin;

/**
 * Gold is already quite valuable.
 */
public class GoldCoin extends Coin {
    public GoldCoin () {
        super("Gold coin");
    }

    @Override
    public int getValue () {
        return 80;
    }
}

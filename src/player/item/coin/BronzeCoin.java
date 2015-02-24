package player.item.coin;

/**
 * A coin of small value.
 */
public class BronzeCoin extends Coin {
    public BronzeCoin () {
        super("Bronze Coin");
    }

    @Override
    public int getValue () {
        return 1;
    }
}

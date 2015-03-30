package player.item.coin;

/**
 * A coin of small value.
 */
public class BronzeCoin extends Coin {
    public BronzeCoin () {
        super("Bronze Coin");
    }

    /** the value of a bronze coin is <code>1</code> */
    public final static int VALUE = 1;

    @Override
    public int getValue () {
        return VALUE;
    }
}

package player.item.coin;

import player.item.Item;

/**
 * Any coin is part of it.
 */
public abstract class Coin extends Item implements Cloneable {

    public Coin (String name) {
        super(name);
    }

    /**
     * The value of the coin is calculated in {@link player.item.coin.BronzeCoin}.
     *
     * @return the value of the coin.
     */
    public abstract int getValue ();

    @Override
    public String toString () {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Coin) {
            return ((Coin) obj).getValue() == this.getValue();
        } else {
            return super.equals(obj);
        }
    }
}

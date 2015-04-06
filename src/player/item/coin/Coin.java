package player.item.coin;

import player.item.Item;

import java.awt.image.BufferedImage;

/**
 * Any coin is part of it.
 */
public abstract class Coin extends Item {

    public Coin (String name) {
        super(name);
    }

    /**
     * The value of the coin is calculated in {@link player.item.coin.BronzeCoin}.
     *
     * @return the value of the coin.
     */
    public abstract int getValue ();

    /** The basic appearance of a coin overwritten by it's subclasses helps to draw it (for example at InventoryScreen). */
    public abstract BufferedImage getImage ();

    @Override
    public String toString () {
        return getName();
    }
}

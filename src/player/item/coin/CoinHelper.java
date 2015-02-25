package player.item.coin;

import java.util.LinkedList;
import java.util.List;

/**
 * The static methods of this class will help to change the coins into each other.
 */
public class CoinHelper {

    public static BronzeCoin[] toBronze (List<Coin> coins) {
        if (coins.isEmpty())
            return new BronzeCoin[0];

        int value = 0;
        for (Coin coin : coins)
            value = value + coin.getValue();

        BronzeCoin [] bronzeCoins = new BronzeCoin[value];
        for (int i = 0; i < value; i++)
            bronzeCoins[i] = new BronzeCoin();

        return bronzeCoins;
    }

    public static SilverCoin[] toSilver (List<Coin> coins) {
        if (coins.isEmpty())
            return new SilverCoin[0];

        int value = 0;
        for (Coin coin : coins)
            value = value + coin.getValue();

        int amount = value / new SilverCoin().getValue();

        SilverCoin[] silverCoins = new SilverCoin[amount];
        for (int i = 0; i < silverCoins.length; i++)
            silverCoins[i] = new SilverCoin();

        return silverCoins;
    }

    public static GoldCoin[] toGold (List<Coin> coins) {
        if (coins.isEmpty())
            return new GoldCoin[0];

        int value = 0;
        for (Coin coin : coins)
            value = value + coin.getValue();

        int amount = value / new GoldCoin().getValue();

        GoldCoin[] goldCoins = new GoldCoin[amount];
        for (int i = 0; i < goldCoins.length; i++)
            goldCoins[i] = new GoldCoin();

        return goldCoins;
    }

    public static PlatinumCoin[] toPlatinum (List<Coin> coins) {
        if (coins.isEmpty())
            return new PlatinumCoin[0];

        int value = 0;
        for (Coin coin : coins)
            value = value + coin.getValue();

        int amount = value / new PlatinumCoin().getValue();

        PlatinumCoin[] platinumCoins = new PlatinumCoin[amount];
        for (int i = 0; i < platinumCoins.length; i++)
            platinumCoins[i] = new PlatinumCoin();

        return platinumCoins;

    }

    /**
     * Returns an array of lists of Coins. The list of items is searched for coins and added to sortedCoins.
     * So the different types of coins are separated into their basic kinds.
     * <p>
     * <code>getSortedCoins[0]</code>: {@link player.item.coin.BronzeCoin} <p>
     * <code>getSortedCoins[1]</code>: {@link player.item.coin.SilverCoin} <p>
     * <code>getSortedCoins[2]</code>: {@link player.item.coin.GoldCoin} <p>
     * <code>getSortedCoins[3]</code>: {@link player.item.coin.PlatinumCoin}
     *
     * @param items a list containing the coins (or any other objects)
     * @return an array of lists of Coins
     *
     * @see player.item.coin.CoinHelper#getSortedCoinsLength(java.util.List)
     */
    public static List<Coin>[] getSortedCoins (List items) {
        List<Coin>[] sortedCoins = new List[4];
        sortedCoins[0] = new LinkedList<>();
        sortedCoins[1] = new LinkedList<>();
        sortedCoins[2] = new LinkedList<>();
        sortedCoins[3] = new LinkedList<>();

        for (Object item : items) {
            if (item instanceof BronzeCoin)
                sortedCoins[0].add((BronzeCoin) item);
            else if (item instanceof SilverCoin)
                sortedCoins[1].add((SilverCoin) item);
            else if (item instanceof GoldCoin)
                sortedCoins[2].add((GoldCoin) item);
            else if (item instanceof PlatinumCoin)
                sortedCoins[3].add((PlatinumCoin) item);
        }

        return sortedCoins;
    }

    /**
     * This counts the number of the available coins in the list <code>items</code> and saves them in the following order:
     * <p>
     * <code>getSortedCoinsLength[0]</code>: {@link player.item.coin.BronzeCoin} <p>
     * <code>getSortedCoinsLength[1]</code>: {@link player.item.coin.SilverCoin} <p>
     * <code>getSortedCoinsLength[2]</code>: {@link player.item.coin.GoldCoin} <p>
     * <code>getSortedCoinsLength[3]</code>: {@link player.item.coin.PlatinumCoin}
     *
     * @param items the list containing coins (or other objects)
     * @return an int[] of the length <code>4</code> where the number of coins of each type is saved.
     *
     * @see player.item.coin.CoinHelper#getSortedCoins(java.util.List)
     */
    public static int[] getSortedCoinsLength (List items) {
        int[] sortedCoinsLength = new int[4];

        for (Object item : items) {
            if (item instanceof BronzeCoin)
                sortedCoinsLength[0]++;
            else if (item instanceof SilverCoin)
                sortedCoinsLength[1]++;
            else if (item instanceof GoldCoin)
                sortedCoinsLength[2]++;
            else if (item instanceof PlatinumCoin)
                sortedCoinsLength[3]++;
        }

        return sortedCoinsLength;
    }
}

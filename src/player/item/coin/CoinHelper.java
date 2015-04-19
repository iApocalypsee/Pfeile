package player.item.coin;

import scala.Tuple2;
import scala.collection.JavaConversions;

import java.util.LinkedList;
import java.util.List;

/**
 * The static methods of this class will help to change the coins into each other.
 * Every toCoinType (expect toBronzeCoins) method returns the casted value. This means the rest money is neglected.
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

    /**
     * The value of the coins are measured ({@link player.item.coin.CoinHelper#getValue(java.util.List)}) and an array of
     * SilverCoins is created with the value of measured before. As there may be some BronzeCoins, which sum of values is
     * below the value of {@link player.item.coin.SilverCoin#VALUE}, a second array is created witch BronzeCoins.
     * The bronzeCoin array contains the rest. The silverCoin-Array and bronzeCoin-Array are put into a Tuple to be returned.
     *
     * @param coins a list of coins
     * @return a <code>scala.Tuple2< SilverCoin[], BronzeCoin[]></code>
     */
    public static Tuple2<SilverCoin[], BronzeCoin[]> toSilver (List<Coin> coins) {
        if (coins.isEmpty())
            return new Tuple2<>(new SilverCoin[0], new BronzeCoin[0]);


        int value = CoinHelper.getValue(coins);

        int amount = value / SilverCoin.VALUE;
        int amountOfBronze = value % SilverCoin.VALUE;

        SilverCoin[] silverCoins = new SilverCoin[amount];
        for (int i = 0; i < silverCoins.length; i++)
            silverCoins[i] = new SilverCoin();

        BronzeCoin[] bronzeCoins = CoinHelper.getCoins(amountOfBronze);

        return new Tuple2<>(silverCoins, bronzeCoins);
    }

    /**
     * For further description read: {@link player.item.coin.CoinHelper#toSilver(java.util.List)}
     * - just replace the word Silver with Gold
     * @param coins any list of coins
     * @return a scala.Tuple2 < GoldCoin[], BronzeCoin[] > --> the value in gold coins, the rest of the value in bronzeCoins
     */
    public static Tuple2<GoldCoin[], BronzeCoin[]> toGold (List<Coin> coins) {
        if (coins.isEmpty())
            return new Tuple2<>(new GoldCoin[0], new BronzeCoin[0]);

        int value = CoinHelper.getValue(coins);

        int amount = value / GoldCoin.VALUE;
        int amountOfBronze = value % GoldCoin.VALUE;

        GoldCoin[] goldCoins = new GoldCoin[amount];
        for (int i = 0; i < goldCoins.length; i++)
            goldCoins[i] = new GoldCoin();

        BronzeCoin[] bronzeCoins = CoinHelper.getCoins(amountOfBronze);

        return new Tuple2<>(goldCoins, bronzeCoins);
    }

    /**
     * For further description read: {@link player.item.coin.CoinHelper#toSilver(java.util.List)}
     * - just replace the word Silver with Platinum
     * @param coins any list of coins
     * @return a scala.Tuple2 < PlatinumCoin[], BronzeCoin[] > --> the value in PlatinumCoins, the rest of the value in bronzeCoins
     */
    public static Tuple2<PlatinumCoin[], BronzeCoin[]> toPlatinum (List<Coin> coins) {
        if (coins.isEmpty())
            return new Tuple2<>(new PlatinumCoin[0], new BronzeCoin[0]);

        int value = CoinHelper.getValue(coins);

        int amount = value / PlatinumCoin.VALUE;
        int amountOfBronze = value % PlatinumCoin.VALUE;

        PlatinumCoin[] platinumCoin = new PlatinumCoin[amount];
        for (int i = 0; i < platinumCoin.length; i++)
            platinumCoin[i] = new PlatinumCoin();

        BronzeCoin[] bronzeCoins = CoinHelper.getCoins(amountOfBronze);

        return new Tuple2<>(platinumCoin, bronzeCoins);
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
    public static List<Coin>[] getSortedCoins (List<?> items) {
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

    /**
     *
     *
     * @param value the value of the Coin
     * @return an Array of BronzeCoins the specifiedValue
     */
    public static BronzeCoin[] getCoins (int value) {
        if (value < 0)
            throw new IllegalArgumentException("the value " + value + " must be higher than 0.");

        BronzeCoin[] coins = new BronzeCoin[value];
        for (int i = 0; i < coins.length; i++)
            coins[i] = new BronzeCoin();

        return coins;
    }

    /**
     * It searches the list for coins, adding the value of each coin together.
     *
     * @param list any list which may contain coins.
     * @return the sum of the values in the list
     *
     * @see player.item.coin.CoinHelper#getValue(Object[])
     */
    public static int getValue (List list) {
        int value = 0;
        for (Object obj : list) {
            if (obj instanceof Coin)
                value = value + ((Coin) obj).getValue();
        }
        return value;
    }

    /**
     * Scala interop.
     * @param seq The scala seq to calculate the value for.
     * @param <A> Type parameter.
     * @return The value of the seq, if any. Defaults to 0.
     */
    public static <A> int getValue(scala.collection.Iterable<A> seq) {
        int value = 0;
        for(A a : JavaConversions.asJavaIterable(seq)) {
            if(a instanceof Coin) {
                value += ((Coin) a).getValue();
            }
        }
        return value;
    }

    /**
     * By iterating the array, every the value of the array is measured.
     *
     * @param array an array which may or may not contain coins.
     * @return the sum of the values of each coin.
     *
     * @see player.item.coin.CoinHelper#getValue(java.util.List)
     */
    public static <A> int getValue (A[] array) {
        int value = 0;
        for (Object obj : array) {
            if (obj instanceof Coin)
                value = value + ((Coin) obj).getValue();
        }
        return value;
    }
}

package player.item.coin;

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
}

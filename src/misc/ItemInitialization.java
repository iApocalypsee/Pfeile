package misc;

import general.LogFacility;
import player.item.coin.BronzeCoin;
import player.item.coin.GoldCoin;
import player.item.coin.PlatinumCoin;
import player.item.coin.SilverCoin;

/**
 * By calling {@link ItemInitialization#initialize()} the BufferedImage of every kind of <code>Coin</code>,
 * <code>Potion</code> and <code>Loot</code> is initialized by creating a new Instance.
 * ItemInitialization is a already threaded internally.
 *
 * @see misc.ArmingInitialization
 */
public class ItemInitialization {

    /** Creates a single instance for each <code>Coin</code>, <code>Potion</code> and <code>Loot</code>.
     *  This method is threaded. The priority is lower then {@link Thread#NORM_PRIORITY}, because the initialization
     *  doesn't need to be ready before using Coins, Potions or Loots.
     */
    public static void initialize () {
        Thread x = new Thread(() -> {
            initializeCoins();
            LogFacility.log("Coin images loaded.", "Info", "initprocess");

            initializePotions();
            LogFacility.log("Potion images loaded.", "Info", "initprocess");

            initializeLoots();
            LogFacility.log("Loot images loaded.", "Info", "initprocess");
        });
        x.setDaemon(true);
        x.setPriority(2);
        x.start();
    }

    /** for every coin a single instance is created to initialize the Image */
    private static void initializeCoins () {
        new BronzeCoin();
        new SilverCoin();
        new GoldCoin();
        new PlatinumCoin();
    }

    private static void initializePotions () {

    }

    private static void initializeLoots () {

    }

}

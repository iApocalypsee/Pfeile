package misc;

import general.LogFacility;
import player.item.BagOfLoots;
import player.item.DefaultChest;
import player.item.RoundChest;
import player.item.Treasure;
import player.item.coin.BronzeCoin;
import player.item.coin.GoldCoin;
import player.item.coin.PlatinumCoin;
import player.item.coin.SilverCoin;
import player.item.potion.PotionOfDamage;
import player.item.potion.PotionOfHealing;
import player.item.potion.PotionOfMovement;

/**
 * By calling {@link ItemInitialization#initialize()} the BufferedImage of every kind of <code>Coin</code>,
 * <code>Potion</code> and <code>Loot</code> is initialized by creating a new Instance.
 * ItemInitialization is a already threaded internally.
 *
 * @see misc.ArmingInitialization
 */
public class ItemInitialization {

    /** Creates a single instance for each <code>Coin</code> and <code>Potion</code>.
     *  This method is threaded. The priority is lower then {@link Thread#NORM_PRIORITY}, because the initialization
     *  doesn't need to be ready before using Coins and Potions.
     *  <b><code>Loot</code> can't be loaded during Initialization process due to references to the not-yet-existing
     *  Tile-System</b>. Loots are loaded later in the game during creating WorldLootList/LootSpawner.
     */
    public static void initialize () {
        Thread x = new Thread(() -> {
            initializeCoins();
            LogFacility.log("Coin images loaded.", "Info", "initprocess");

            initializePotions();
            LogFacility.log("Potion images loaded.", "Info", "initprocess");
        });
        x.setDaemon(true);
        x.setPriority(1);
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
        new PotionOfHealing();
        new PotionOfMovement();
        new PotionOfDamage();
    }

    /**
     * If the World has been created, this method has to be called to initialize the images of the Loots. The method is
     * threaded with a high priority (There is not much time until this method needs to be ready due to the code in
     * LoadingWorldScreen#onScreenEnter#+=).
     */
    public static void initializeLoots () {
        Thread x = new Thread("Loot initialization Thread") {
            @Override
            public void run () {
                new RoundChest(0, 0);
                new DefaultChest(0, 0);
                new BagOfLoots(0, 0);
                new Treasure(0, 0);
            }
        };
        x.setDaemon(true);
        x.setPriority(Thread.MAX_PRIORITY);
        x.start();
    }
}

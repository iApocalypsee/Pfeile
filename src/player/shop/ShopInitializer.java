package player.shop;

import player.item.loot.KeyDefaultChest;
import player.item.potion.PotionOfDamage;
import player.item.potion.PotionOfHealing;
import player.item.potion.PotionOfMovement;

/**
 * Initializes Shop and adds articles after that.
 */
public class ShopInitializer {

    /** PfeileContext must exist, if this method should work */
    public static void initalizeShop () {
        Thread shopInitializer = new Thread(() -> {
            // run-method
            addArticles();

            // this call only work, if "Main.getContext()" is available, so it is called in LoadingWorldScreen.
            // GameScreen.getInstance().getShopWindow();
        }, "Shop Initializer");
        shopInitializer.setDaemon(true);
        shopInitializer.start();
    }

    /**
     * adds Articles to Shop
     */
    private static void addArticles () {
        ShopCentral.addArticle(() -> new PotionOfMovement((byte) 1), 28);
        ShopCentral.addArticle(() -> new PotionOfMovement((byte) 0), 15);
        ShopCentral.addArticle(() -> new PotionOfDamage((byte) 1), 45);
        ShopCentral.addArticle(() -> new PotionOfHealing((byte) 0), 50);
        ShopCentral.addArticle(KeyDefaultChest::new, 200);
    }
}

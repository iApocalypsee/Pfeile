package player.item;

import comp.Circle;
import general.Main;
import general.PfeileContext;
import gui.screen.ArrowSelectionScreenPreSet;
import gui.screen.Screen;
import newent.Bot;
import newent.EntityLike;
import newent.Player;
import player.item.coin.*;
import player.item.potion.PotionOfDamage;
import player.item.potion.PotionOfHealing;
import player.item.potion.PotionOfMovement;
import player.weapon.arrow.ArrowHelper;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import world.SeaTile;
import world.TerrainLike;

import java.awt.*;
import java.util.Random;

/**
 * Every Loot spawns by this class. The loot is automatically added to {@link player.item.WorldLootList}.
 */
public class LootSpawner {
    private Random random;

    public LootSpawner () {
        random = new Random();

        ArrowSelectionScreenPreSet.getInstance().onScreenLeft.register(new AbstractFunction1<Screen.ScreenChangedEvent, BoxedUnit>() {
            @Override
            public BoxedUnit apply (Screen.ScreenChangedEvent v1) {

                spawningRoundChest();

                // Spawn 0 to 5 loots with the possible of 50% for more fun at the beginning
                for (int i = 0; i < random.nextInt(6); i++) {
                    if (random.nextBoolean())
                        spawningAnyLoot();
                }

                return BoxedUnit.UNIT;
            }
        });

        Main.getContext().getTurnSystem().onGlobalTurnCycleEnded().register(new AbstractFunction0<BoxedUnit>() {

            @Override
            public BoxedUnit apply () {

                // Just spawn 0 to 3 loots with the possible of 50%.
                for (int i = 0; i < random.nextInt(4); i++) {
                    if (random.nextBoolean())
                        spawningAnyLoot();
                }

                return BoxedUnit.UNIT;
            }
        });

    }


    /** This spawns the {@link player.item.RoundChest}. It is triggered every time <code>ArrowSelectionScreenPreSet</code>
     * is left. The position is set by {@link player.item.LootSpawner#spawnLoot(int, int)} with <code>4</code> and <code>5</code>.
     */
    private void spawningRoundChest () {
        Point spawnPoint = spawnLoot(4, 6);

        RoundChest spawnedChest = new RoundChest(spawnPoint.x, spawnPoint.y);

        // adding something. Here from everything one.
        spawnedChest.add(new PotionOfHealing((byte) (random.nextInt(3))));
        spawnedChest.add(new PotionOfMovement((byte) (random.nextInt(3))));
        spawnedChest.add(new PlatinumCoin());
        spawnedChest.add(new GoldCoin());
        spawnedChest.add(new SilverCoin());
        spawnedChest.add(new BronzeCoin());
        spawnedChest.add(ArrowHelper.instanceArrow(random.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));

        Main.getContext().getWorldLootList().add(spawnedChest);
    }

    /**
     * Spawns either a treasure or a defaultChest with the possibility of 50%
     */
    private void spawningAnyLoot () {
        Point spawnPoint = spawnLoot(3, 5);

        Loot spawnedLoot;

        if (random.nextBoolean())
            spawnedLoot = new Treasure(spawnPoint.x, spawnPoint.y);
        else
            spawnedLoot = new DefaultChest(spawnPoint.x, spawnPoint.y);


        // if it's a Treasure just add money
        if (spawnedLoot instanceof Treasure) {
            BronzeCoin[] coins = CoinHelper.getCoins(random.nextInt(250) + 1);
            for (BronzeCoin coin : coins)
                spawnedLoot.add(coin);

            // with the possibility of 15% a PotionOfDamage is added.
            if (random.nextDouble() < 0.15)
                spawnedLoot.add(new PotionOfDamage((byte) (random.nextInt(3))));

        // if it's a defaultChest add a potion and a little bit money
        } else {
            if (random.nextBoolean())
                spawnedLoot.add(new PotionOfHealing((byte) (random.nextInt(3))));
            else
                spawnedLoot.add(new PotionOfMovement((byte) (random.nextInt(3))));

            BronzeCoin[] coins = CoinHelper.getCoins(random.nextInt(50));
            for (BronzeCoin coin : coins)
                spawnedLoot.add(coin);

            // with the possibility of 10% an arrow
            if (random.nextDouble() < 0.1)
                spawnedLoot.add(ArrowHelper.instanceArrow(random.nextInt()));
        }

        Main.getContext().getWorldLootList().add(spawnedLoot);
    }

    /**
     * Selects random fields until one has been found where no entity is in the near. If there is no possible field
     * (after there has been a lot of tries (worldSizeX * worldSizeY tries) to spawn - <code>radiusTroops</code> and
     * <code>radiusPlayer</code> is reduced by one to prohibit an endless loop.
     * A loot cannot spawn, if there is a <code>SeaTile</code> or {@link player.item.LootSpawner#isEntityNear(int, int, int, int)} returns true.
     *
     * @param radiusTroops the radius where troops stops the loot from spawning.
     * @param radiusPlayers the radius where players and bots stops the loot from spawning.
     * @return the Position where the loot can spawn
     */
    private Point spawnLoot (int radiusTroops, int radiusPlayers) {
        Point spawnPoint = new Point(-1, -1);

        TerrainLike terrain = Main.getContext().getWorld().terrain();

        int count = 0;
        int maxCount = PfeileContext.WORLD_SIZE_X().get() * PfeileContext.WORLD_SIZE_Y().get();
        int distanceTroops = 0;
        int distancePlayers = 0;


        do {
            int spawnX = random.nextInt(PfeileContext.WORLD_SIZE_X().get());
            int spawnY = random.nextInt(PfeileContext.WORLD_SIZE_Y().get());

            if (terrain.tileAt(spawnX, spawnY) instanceof SeaTile)
                continue;

            if (!isEntityNear(spawnX, spawnY, radiusTroops - distanceTroops, radiusPlayers - distancePlayers)) {
                spawnPoint.setLocation(spawnX, spawnY);
            }
            count++;
            if (count > maxCount) {
                if (distanceTroops < radiusTroops)
                    distanceTroops++;
                if (distancePlayers < radiusPlayers)
                    distancePlayers++;
                count = 0;
            }
        } while (spawnPoint.x == -1 || spawnPoint.y == -1);

        return spawnPoint;
    }

    /**
     * Controls if an entity is near the position of (<code>posX</code>|<code>posY</code>). That method is part of the
     * spawn-system. <code>radiusPlayers</code> is the radius for both {@link newent.Player} and {@link newent.Bot}.
     *
     * @param posX the x-position the loot would want to spawn
     * @param posY and the y-position
     * @param radiusTroops the maximum radius troops (this means any <code>Entity</code>) can be away from the position
     *                     until this method returns <code>false</code>.
     * @param radiusPlayers the maximum radius a player (or a bot) can be away from the position until this method returns <code>false</code>
     * @return <code>true</code> - if there is any entity near the position (<code>posX</code>|<code>posY</code>) in the radius
     */
    private boolean isEntityNear (int posX, int posY, int radiusTroops, int radiusPlayers) {
        java.util.List<EntityLike> entityList = Main.getContext().getWorld().entities().javaEntityList();

        for (EntityLike entity : entityList) {
            if (entity instanceof Player || entity instanceof Bot) {
                Circle circle = new Circle(entity.getGridX(), entity.getGridY(), radiusPlayers);
                if (circle.contains(posX, posY))
                    return true;
            } else {
                Circle circle = new Circle (entity.getGridX(), entity.getGridY(), radiusTroops);
                if (circle.contains(posX, posY))
                    return true;
            }
        }

        return false;
    }
}

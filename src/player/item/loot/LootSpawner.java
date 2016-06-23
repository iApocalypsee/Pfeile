package player.item.loot;

import comp.Circle;
import general.PfeileContext;
import newent.Bot;
import newent.GameObject;
import newent.Player;
import newent.Team;
import player.item.coin.*;
import player.item.ore.CopperOre;
import player.item.ore.IronOre;
import player.item.potion.*;
import player.weapon.arrow.ArrowHelper;
import world.GrassTile;
import world.Terrain;

import java.awt.*;
import java.util.Random;


/**
 * Every Loot spawns by this class. The loot is automatically added to {@link WorldLootList}.
 * LootSpawner is created in WorldLootList. Do not create it twice.
 */
public class LootSpawner {

    /**
     * The context on which this loot spawner. is operating on.
     * For decoupling reasons. Previous implementations relied too much on initialization of global variables.
     */
    private final PfeileContext context;

    private final int numberOfSpawnsAtBeginning;

    private Random random;

    public LootSpawner(PfeileContext context) {
        this.context = context;
        random = new Random();
        // there should be at least 23 loots at the beginning of the game
        numberOfSpawnsAtBeginning = 23 + random.nextInt(20);

        context.getTurnSystem().getRoundOperations().onRoundEnded().registerJava(() -> {
            spawningRoundChest();

            // Spawn 2 to 7 loots with the possibility of 35% after the end of a round
            for (int i = 0; i < 2 + random.nextInt(5); i++) {
                if (random.nextFloat() < 0.35f)
                    spawningAnyLoot();
            }
        });

        context.getTurnSystem().onGlobalTurnCycleEnded().registerJava(() -> {
            // Just spawn one or two loots with a possibility of ?% each.
            if (random.nextFloat() < 0.25f)
                spawningAnyLoot();
            if (random.nextFloat() < 0.10f)
                spawningAnyLoot();
            if (random.nextFloat() < 0.03f)
                spawningAnyLoot();
        });

    }

    /** Spawns the start-setup of Loots. <b>Only call it once by ContexCreator!</b>. Adds also a key for a default chest
     * to every players inventory. */
    public void spawnAtBeginning () {

        // The first RoundChest should spawn at the beginning; the first rounds ends usually after 10 turnCycles.
        spawningRoundChest();

        // Spawn some loots with the possibility of 50% for more fun at the beginning
        for (int i = 0; i < numberOfSpawnsAtBeginning; i++) {
            if (random.nextBoolean())
                spawningAnyLoot();
        }

        // every player should have a key to a default chest
        for (Team team : context.getTurnSystem().getTeams()) {
            if (!team.isBarbarian())
                team.asCommandTeam().head().inventory().put(new KeyDefaultChest());
        }
    }

    /** This spawns the {@link RoundChest}. It is triggered every time <code>ArrowSelectionScreenPreSet</code>
     * is left. The position is set by {@link LootSpawner#spawnLoot(int, int)} with <code>4</code> and <code>5</code>.
     */
    private void spawningRoundChest () {
        Point spawnPoint = spawnLoot(5, 7);

        RoundChest spawnedChest = new RoundChest(spawnPoint.x, spawnPoint.y);

        // adding something. Here from everything one.
        spawnedChest.add(new PotionOfHealing((byte) (random.nextInt(3))));
        spawnedChest.add(new PotionOfMovement((byte) (random.nextInt(3))));
        if (random.nextFloat() < 0.1f)
            spawnedChest.add(new PotionOfFortune((byte) (random.nextInt(3))));
        if (random.nextFloat() < 0.035f)
            spawnedChest.add(new PotionOfPoison((byte) (random.nextInt(3))));

        if (random.nextFloat() < 0.2f)
            spawnedChest.add(new PlatinumCoin());
        for (int i = 0; i < random.nextInt(7); i++)
            spawnedChest.add(new GoldCoin());
        for (int i = 0; i < random.nextInt(10); i++)
            spawnedChest.add(new SilverCoin());
        for (int i = 0; i < random.nextInt(50) + 10; i++)
            spawnedChest.add(new BronzeCoin());

        // adding some arrows
        for (int i = 0; i < 9; i++) {
            if (random.nextBoolean())
                spawnedChest.add(ArrowHelper.instanceArrow(random.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
        }

        // adding new keys
        if (random.nextFloat() < 0.008f)
            spawnedChest.add(new KeyRoundChest());
        if (random.nextFloat() < 0.12f)
            spawnedChest.add(new KeyDefaultChest());
        if (random.nextFloat() < 0.88f)
            spawnedChest.add(new KeyDefaultChest());

        context.getWorldLootList().add(spawnedChest);
    }

    /**
     * Spawns either a treasure or a defaultChest with the possibility of 50%
     */
    private void spawningAnyLoot () {
        Point spawnPoint = spawnLoot(3, 5);

        Loot spawnedLoot;

        if (random.nextDouble() < 0.7)
            spawnedLoot = new Treasure(spawnPoint.x, spawnPoint.y);
        else
            spawnedLoot = new DefaultChest(spawnPoint.x, spawnPoint.y);


        // if it's a Treasure just add money
        if (spawnedLoot instanceof Treasure) {
            BronzeCoin[] coins = CoinHelper.getCoins(random.nextInt(200) + 20);
            for (BronzeCoin coin : coins)
                spawnedLoot.add(coin);

            if (random.nextFloat() < 0.05)
                spawnedLoot.add(new SilverCoin());

            if (random.nextFloat() < 0.01)
                spawnedLoot.add(new GoldCoin());

            if (random.nextFloat() < 0.001)
                spawnedLoot.add(new PlatinumCoin());

            // with the possibility of 15% a PotionOfDamage is added.
            if (random.nextFloat() < 0.15)
                spawnedLoot.add(new PotionOfDamage((byte) (random.nextInt(3))));

            if (random.nextFloat() < 0.03)
                spawnedLoot.add(new KeyDefaultChest());

        // if it's a defaultChest add a potion and a little bit money
        } else {
            if (random.nextFloat() < 0.3)
                spawnedLoot.add(new PotionOfHealing((byte) (random.nextInt(3))));
            else
                spawnedLoot.add(new PotionOfMovement((byte) (random.nextInt(2))));

            BronzeCoin[] coins = CoinHelper.getCoins(random.nextInt(80));
            for (BronzeCoin coin : coins)
                spawnedLoot.add(coin);

            for (int i = 0; i < random.nextInt(3); i++)
                spawnedLoot.add(new SilverCoin());

            if (random.nextFloat() < 0.15f)
                spawnedLoot.add(new PotionOfDamage((byte) (random.nextInt(3))));
            if (random.nextFloat() < 0.06f)
                spawnedLoot.add(new PotionOfPoison((byte) (random.nextInt(2))));
            if (random.nextFloat() < 0.03f)
                spawnedLoot.add(new PotionOfFortune((byte) (random.nextInt(3))));
            if (random.nextFloat() < 0.01f)
                spawnedLoot.add(new PotionOfFortune((byte) (random.nextInt(3))));

            if (random.nextFloat() < 0.7f) {
                for (int i = 0; i < random.nextInt(6); i++)
                    spawnedLoot.add(new IronOre());
            }
            if (random.nextFloat() < 0.4f)
                spawnedLoot.add(new CopperOre());

            if (random.nextFloat() < 0.15f)
                spawnedLoot.add(new KeyDefaultChest());
            if (random.nextFloat() < 0.08f)
                spawnedLoot.add(new KeyRoundChest());
        }

        context.getWorldLootList().add(spawnedLoot);
    }

    /**
     * Selects random fields until one has been found where no entity is in the near. If there is no possible field
     * (after there has been a lot of tries (worldSizeX * worldSizeY tries) to spawn - <code>radiusTroops</code> and
     * <code>radiusPlayer</code> is reduced by one to prohibit an endless loop.
     * A loot cannot spawn, if there is a <code>SeaTile</code> or {@link LootSpawner#isEntityNear(int, int, int, int)} returns true.
     *
     * @param radiusTroops the radius where troops stops the loot from spawning.
     * @param radiusPlayers the radius where players and bots stops the loot from spawning.
     * @return the Position where the loot can spawn
     */
    private Point spawnLoot (int radiusTroops, int radiusPlayers) {
        Point spawnPoint = new Point(-1, -1);

        Terrain terrain = context.getWorld().terrain();

        int count = 0;
        int maxCount = PfeileContext.worldSizeX().get() * PfeileContext.worldSizeY().get();
        int distanceTroops = 0;
        int distancePlayers = 0;


        do {
            int spawnX = random.nextInt(PfeileContext.worldSizeX().get());
            int spawnY = random.nextInt(PfeileContext.worldSizeY().get());

            if (!(terrain.tileAt(spawnX, spawnY) instanceof GrassTile))
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
        java.util.List<GameObject> entityList = context.getWorld().entities().javaEntityList();

        for (GameObject entity : entityList) {
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

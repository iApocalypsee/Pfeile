package player.item;

import general.JavaInterop;
import general.Main;
import gui.Drawable;
import newent.Player;
import newent.VisionMap;
import newent.VisionStatus;
import scala.collection.Seq;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>WorldLootList</code> is saved in PfeileContext. This class provides the information need to store all loots,
 * that are placed in world. Calling the draw method here allows the user to draw every loot saved in the List by drawing
 * it's LootUI.
 */
public class WorldLootList implements Drawable {
    private final List<Loot> lootList;

    /** The list is used to save every not {@link newent.VisionStatus#Hidden} Loot in order to speed up the drawing process */
    private final List<Loot> lootVisibleList;

    /** creating a new WorldLootList with the default size 18 [as java.util.ArrayList].
     * It also creates a new List for every visible Loot (from the view of the activePlayer) and registers
     * the {@link WorldLootList#updateVisibleLoot()} to {@link newent.Entity#onLocationChanged()} and
     * {@link general.TurnSystem#onTurnGet()}.
     * */
    public WorldLootList () {
        lootList = new ArrayList<>(18);
        lootVisibleList = new ArrayList<>(12);

        Main.getContext().turnSystem().onTurnGet().register(new AbstractFunction1<Player, BoxedUnit>() {
            @Override
            public BoxedUnit apply (Player v1) {
                updateVisibleLoot();
                return BoxedUnit.UNIT;
            }
        });

        // every time, when the location of a player has changed, the list of every not-hidden loot must update itself.
        // ==> {@link WorldLootList#updateVisibleLoot()} is registered to the "onLocationChanged"-Delegate of every Player.
        final Seq<Player> playerSeq = Main.getContext().getTurnSystem().playerList().apply();
        playerSeq.foreach(JavaInterop.asScalaFunction(player -> {
            player.onLocationChanged().register(JavaInterop.asScalaFunction(locationChangedEvent -> {
                updateVisibleLoot();
                return BoxedUnit.UNIT;
            }));
            return BoxedUnit.UNIT;
        }));

    }

    /**
     * Registers a new Loot to the lootList and updates the list of the visibleLoots (to speed the drawing process)
     *
     * @param droppedLoot the loot to be added to the list
     */
    public void add (Loot droppedLoot) {
        lootList.add(droppedLoot);
        updateVisibleLoot();
    }

    /**
     * Removes the specified Loot and if the element could be removed,
     * it updates the list of the visibleLoots {@link WorldLootList#updateVisibleLoot()}.
     *
     * @param collectedLoot the loot to be removed
     * @return <code>true</code> - if the list contained the <code>collectedLoot</code>
     */
    public boolean remove (Loot collectedLoot) {
        boolean removed = lootList.remove(collectedLoot);
        if (removed) {
            updateVisibleLoot();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the ArrayList of all Loots. Do not change the list; just use it if you want to find loots or change loots
     *
     * @return the list
     * @see player.item.WorldLootList#getVisibleLoots()
     */
    public List<Loot> getLoots () {
        return lootList;
    }

    /**
     * Returns the ArrayList of Loots, which are visible ({@link newent.VisionStatus#Revealed} or {@link newent.VisionStatus#Visible})
     * by the activePlayer. Don't change the returned lootList itself, only the loots inside.
     * Use, whenever possible the methods of WorldLootList itself.
     *
     * @return the list of every visible loot (by the {@link general.PfeileContext#getActivePlayer()})
     */
    public List<Loot> getVisibleLoots () {
        return lootVisibleList;
    }

    /**
     * Updates the {@link WorldLootList#getVisibleLoots()}.
     * If the visibleRadius of the activePlayer changes, or when the activePlayer's turn changes, this method has to be triggered
     * by a Delegate.
     * */
    public synchronized void updateVisibleLoot () {
        final VisionMap visibleMap = Main.getContext().getActivePlayer().visionMap();
        lootVisibleList.clear();

        for(Loot loot : lootList) {
            // only the loot to the visibleList, when the VisionStatus isn't Hidden
            if (visibleMap.visionStatusOf(loot.getGridX(), loot.getGridY()) != VisionStatus.Hidden)
                lootVisibleList.add(loot);

        }
    }

    /**
     * Draws every Loot, but only in the visible area of the activePlayer.
     * The VisionStatus mustn't be {@link newent.VisionStatus#Hidden}.
     *
     * @param g the graphics object used by every {@link gui.Drawable}.
     */
    @Override
    public void draw (Graphics2D g) {
        for (Loot loot : lootVisibleList) {
            loot.getLootUI().draw(g);
        }
    }

    @Override
    public String toString () {
        return "WorldLootList{ " + lootList.toString() + " }";
    }
}

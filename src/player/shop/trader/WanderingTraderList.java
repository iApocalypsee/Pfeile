package player.shop.trader;

import general.LogFacility;
import general.PfeileContext;
import gui.Drawable;
import newent.CommandTeam;
import newent.Player;
import newent.VisionMap;
import player.item.loot.WorldLootList;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to access all registered traders on the world. It provides a shortcut to draw the traders and it
 * serves as helper class for the {@link WanderingTrader} to provide e.g. a new or changing stock.
 * The value <code>numberOfInitialTraders</code> in the constructor defines how many traders there will spawn at the
 * beginning of the game.
 */
public class WanderingTraderList implements Drawable {
    /** contains all the wandering traders registered in the world */
    private final List<WanderingTrader> traders;

    /** contains only the wandering traders, which are registered and visible to the active player (subset of the set
     *  traders). It it used to draw the visible traders faster, but it need to be updated on every turn, just like the
     *  WorldLootList, which is a similar construct.
     */
    private final List<WanderingTrader> visibleTraders;

    /** for initialization perposes to avoid NullPointerExceptions */
    private final PfeileContext context;

    /** Creates a new WanderingTraderList.
     *  @param numberOfInitialTraders the number of traders, that spawn at the beginning
     *  @param context PfeileContext must be given here, since in the Initialization process, there is no way to ensure,
     *                 that there is an visionMap of the active player */
    public WanderingTraderList (int numberOfInitialTraders, PfeileContext context) {
        traders = new ArrayList<>(numberOfInitialTraders);
        visibleTraders = new ArrayList<>(4);
        this.context = context;

        // every time the turn changes, the visibleTraders list has to change.
        context.turnSystem().onTurnGet().registerJava(team -> updateVisibleTraders());

        // every time a player moves, the visibleTraders list has to change.
        context.getTurnSystem().getTeams().forEach(team -> {
            Player player = ((CommandTeam) team).getHead();
            player.onLocationChanged.registerJava(locationChangedEvent -> updateVisibleTraders());
        });
    }

    /** registers a new WanderingTrader in the list */
    public void registerTrader (WanderingTrader trader) {
        synchronized (traders) {
            traders.add(trader);
        }
        // Reduces the time needed for an updateVisibleTraders call.
        if (context.getActivePlayer().visionMap().isVisible(trader.getGridX(), trader.getGridY())) {
            synchronized (visibleTraders) {
                visibleTraders.add(trader);
            }
        }
    }

    /**
     * Removes the specified WanderingTrader trader.
     * If the element could be removed, it updates the list of the visibleLoots {@link WorldLootList#updateVisibleLoot()}.
     *
     * @param trader the trader to be removed
     * @return <code>true</code> - if the list contained the <code>collectedLoot</code>
     */
    public boolean remove (WanderingTrader trader) {
        boolean removed;

        synchronized (traders) {
            removed = traders.remove(trader);
        }

        if (removed) {
            updateVisibleTraders();
        } else {
            LogFacility.log("Can't remove trader: " + trader.toString() + " from the list: " + traders.toString(),
                    LogFacility.LoggingLevel.Error);
        }
        return removed;
    }

    /** updates the visible trader list. Adds every wandering trader on a visible field from the traders-list to the
     *  visibleTraders-list. Needs to be called, e.g. it's another players turn.
     */
    private void updateVisibleTraders () {
        VisionMap visionMap = context.getActivePlayer().visionMap();

        synchronized (visibleTraders) {
            for (WanderingTrader trader : traders) {
                // only add, if in the trader is on a visible tile.
                if (visionMap.isVisible(trader.getGridX(), trader.getGridY()))
                    visibleTraders.add(trader);
            }
        }
    }

    /**
     * Draws every WanderingTrader, but only in the visible area of the activePlayer.
     * (The VisionStatus is not {@link newent.VisionStatus#Hidden})
     *
     * @param g the graphics object used by every {@link gui.Drawable}.
     */
    @Override
    public void draw (Graphics2D g) {
        for (WanderingTrader trader: visibleTraders) {
            trader.getComponent().draw(g);
        }
    }
}

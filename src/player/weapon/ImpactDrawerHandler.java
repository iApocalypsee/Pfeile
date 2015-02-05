package player.weapon;

import newent.event.AttackEvent;

import java.awt.*;
import java.util.LinkedList;

/** Every ImpactDrawer need to be registered by the method <code>addImpactDrawer(...)</code>. Every registered impactDrawer
 * is drawn by the method {@link player.weapon.ImpactDrawerHandler#draw(java.awt.Graphics2D)} (which may be called by any draw-method in any screen).
 * Every ImpactDrawer unregisters (<code>removeAttackDrawer(AttackDrawer)</code> itself, when the Thread ended. */
public class ImpactDrawerHandler {

    private static final LinkedList<ImpactDrawer> impactDrawerList = new LinkedList<>();

    /** this adds a new ImpactDrawer. You need to add an ImpactDrawer, if you want to want to draw it.
     * This is equal to: {@link player.weapon.ImpactDrawerHandler#addImpactDrawer(ImpactDrawer)}. The animation is started automatically.*/
    public static void addImpactDrawer (AttackEvent attackEvent) {
        synchronized (impactDrawerList) {
            ImpactDrawer impactDrawer = new ImpactDrawer(attackEvent);
            impactDrawerList.add(impactDrawer);
            impactDrawer.startAnimation();
        }
    }

    /** this adds the specified impactDrawer to an existing List of ImpactDrawers. Every ImpactDrawer need to be added,
     * if it should be drawn (not only calculate). The animation doesn't start automatically. Use <code>impactDrawer.startAnimation()</code>
     * @param impactDrawer the ImpactDrawer, which need to be added.
     * @see player.weapon.ImpactDrawerHandler#addImpactDrawer(newent.event.AttackEvent)
     */
    public static void addImpactDrawer (ImpactDrawer impactDrawer) {
        synchronized (impactDrawerList) {
            impactDrawerList.add(impactDrawer);
        }
    }

    /** this removes an attackDrawer. Every AttackDrawer will not be drawn. */
    public static void removeImpactDrawer (ImpactDrawer attackDrawer) {
        synchronized (impactDrawerList) {
            impactDrawerList.remove(attackDrawer);
        }
    }

    /** nobody will instance this class */
    private ImpactDrawerHandler () {}

    /** drawing every added/registered impact with this call. */
     public static void draw (Graphics2D g) {
        for (ImpactDrawer drawer : impactDrawerList)
            drawer.draw(g);
    }
}

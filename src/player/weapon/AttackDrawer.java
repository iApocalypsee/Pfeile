package player.weapon;

import general.Main;
import gui.Drawable;
import newent.AttackContainer;
import newent.AttackProgress;
import player.weapon.arrow.AbstractArrow;
import scala.collection.JavaConversions;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** This class will draw the all attacking and later defending weapons. */
public class AttackDrawer implements Drawable {

    /** to get to the Arrows use:
     * <p>
     * <code>for (AttackProgress filteredProgress : filteredProgresses) {
     * <p>
                 AbstractArrow attackingArrow = (AbstractArrow) filteredProgress.event().weapon();
       <p>
                 // do whatever you want with the attacking Arrow(s)
       <p>
     }</code>
     *
     * @return the AttackProgress of all attacking AbstractArrows
     */
    public static List<AttackProgress> getAttackProgressesOfArrows () {
        List <AttackContainer> attackContainerList = Main.getContext().getWorld().getEntities().helper().getAllAttackContainers();
        List <AttackProgress> filteredProgresses = new LinkedList<>();

        for (AttackContainer anAttackContainerList : attackContainerList) {
            List<AttackProgress> arrowList = JavaConversions.seqAsJavaList(anAttackContainerList.queuedAttacks());
            for (AttackProgress p : arrowList) {
                if (p.event().weapon() instanceof AbstractArrow) {
                    filteredProgresses.add(p);
                }
            }
        }
        return filteredProgresses;
    }

    /** Returns an list, which contains every AbstractArrow, which is right somewhere on the world attacking.
     * Basically, it's {@link AttackDrawer#getAttackProgressesOfArrows()} filtered for AbstractArrows.
     *
     * @return list of every attacking arrow
     */
    public static List<AbstractArrow> getAttackingArrows () {
        List<AttackProgress> filteredAttacks = getAttackProgressesOfArrows();
        List<AbstractArrow> arrowList = new ArrayList<>(filteredAttacks.size());
        for (AttackProgress process : filteredAttacks)
            arrowList.add((AbstractArrow) process.event().weapon());
        return arrowList;
    }

    /**
     * Every Weapon, which currently attacks (i.e. is listed in an attackContainer and therefore an AttackProgress)
     * something somewhere in the world, is part of the returned LinkedList.
     *
     * @return a LinkedList with Weapons from AttackContainers.
     */
    public static List<Weapon> getAttackingWeapons () {
        List<Weapon> attackingWeapons = new LinkedList<>();

        final List<AttackContainer> allAttackContainers = Main.getContext().getWorld().getEntities().helper().getAllAttackContainers();

        for (AttackContainer attackContainer : allAttackContainers) {
            for (AttackProgress attackProgress : attackContainer.getQueuedAttacks()) {
                attackingWeapons.add(attackProgress.event().weapon());
            }
        }

        return attackingWeapons;
    }

    @Override
    public void draw (Graphics2D g) {
        List<AttackProgress> filteredProgresses = getAttackProgressesOfArrows();

        for (AttackProgress filteredProgress : filteredProgresses) {
            AbstractArrow attackingArrow = (AbstractArrow) filteredProgress.event().weapon();
            attackingArrow.getComponent().draw(g);
        }
    }
}

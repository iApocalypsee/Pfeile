package player.weapon;

import gui.Drawable;
import newent.AttackContainer;
import newent.AttackContainer$;
import newent.AttackProgress;
import player.weapon.arrow.AbstractArrow;
import scala.collection.JavaConversions;

import java.awt.*;
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
        List <AttackContainer> attackContainerList = AttackContainer$.MODULE$.javaAllAttackContainers();
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

    @Override
    public void draw (Graphics2D g) {
        List<AttackProgress> filteredProgresses = getAttackProgressesOfArrows();

        for (AttackProgress filteredProgress : filteredProgresses) {
            AbstractArrow attackingArrow = (AbstractArrow) filteredProgress.event().weapon();
            attackingArrow.getComponent().draw(g);
        }
    }
}

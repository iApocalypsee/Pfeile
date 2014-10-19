package player.weapon;

import gui.Drawable;
import newent.AttackContainer;
import newent.AttackContainer$;
import newent.AttackProgress;
import scala.collection.JavaConversions;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/** This class will draw the all attacking and later defending weapons. */
public class AttackDrawer implements Drawable {

    @Override
    public void draw (Graphics2D g) {
        List <AttackContainer> attackContainerList = AttackContainer$.MODULE$.javaAllAttackContainers();
        List <AttackProgress> filteredProgresses = new LinkedList<AttackProgress>();

        for (int i = 0; i < attackContainerList.size(); i++) {
            List<AttackProgress> arrowList = JavaConversions.seqAsJavaList(attackContainerList.get(i).queuedAttacks());
            for(AttackProgress p : arrowList) {
                if(p.event().weapon() instanceof AbstractArrow) {
                    filteredProgresses.add(p);
                }
            }
        }

        for (AttackProgress filteredProgress : filteredProgresses) {
            AbstractArrow attackingArrow = (AbstractArrow) filteredProgress.event().weapon();
            attackingArrow.draw(g);
        }
    }
}

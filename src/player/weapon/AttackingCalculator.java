package player.weapon;

import geom.functions.FunctionCollection;
import geom.functions.FunctionCollectionEasing;
import newent.AttackProgress;

import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class AttackingCalculator {
    private boolean[] isEveryArrowReady;
    private long milliSec;

    private class Clock extends TimerTask {
        @Override
        public void run () {
            milliSec++;
        }
    }

    public void arrowsFlying () {

        java.util.Timer timer = new java.util.Timer(true);

        List<AttackProgress> filteredProgresses = AttackDrawer.getAttackProgressesOfArrows();
        List<AbstractArrow> attackingArrows = new LinkedList<AbstractArrow>();

        for (AttackProgress filteredProgress : filteredProgresses) {
            attackingArrows.add((AbstractArrow) filteredProgress.event().weapon());
        }

        // every milliSec has to increase every millisecond
        timer.schedule(new Clock(), 0, 1);

        isEveryArrowReady = new boolean[attackingArrows.size()];

        while (!isEveryAttackReady(isEveryArrowReady)) {
            for (int i = 0; i < attackingArrows.size(); i++) {
                AbstractArrow attackingArrow = attackingArrows.get(i);
                AttackProgress attackProgress = filteredProgresses.get(i);

                AttackingThread attackingThread = new AttackingThread (attackingArrow, attackProgress, i);
                attackingThread.setDaemon(true);
                attackingThread.setPriority(3);
                attackingThread.start();
            }
        }

        // if ready stop the timer and reset the milli-seconds after beginning
        timer.cancel();
        milliSec = 0;
    }

    private boolean isEveryAttackReady (boolean[] isEveryAttackReady) {
        for (boolean anAttack : isEveryAttackReady) {
            if (!anAttack)
                return false;
        }
        return true;
    }

    /** for every attack, we need to run one thread */
    private class AttackingThread extends Thread {
        private AbstractArrow attackingArrow;
        private AttackProgress attackProgress;
        private int positionOfBooleanArray;

        AttackingThread (AbstractArrow attackingArrow, AttackProgress attackProgress, int positionOfBooleanArray) {
            this.attackingArrow = attackingArrow;
            this.attackProgress = attackProgress;
            this.positionOfBooleanArray = positionOfBooleanArray;
        }

        @Override
        public void run () {
            // alpha (radiant) is the ankle between the position of the aim and the current Point
            double alpha = FunctionCollection.angle(attackingArrow.getPosX(), attackingArrow.getPosY(), attackingArrow.getPosY(), attackingArrow.getPosY());

            // radius = geographicalLength / speed;
            double radius = attackProgress.event().lengthPerTurn();

            double accuracy = 0.005;

            // as long as MilliSec are smaller than the speed (in tiles/turn) * 500 milliSec (per turn per tile)
            while (milliSec < attackingArrow.getSpeed() * 500) {
                attackingArrow.setPosY((int) FunctionCollectionEasing.quadratic_easing_inOut(attackingArrow.getSpeed() * 500 * accuracy, attackingArrow.getPosY(), attackingArrow.getPosYAim(), radius));
                attackingArrow.setPosX((int) (attackingArrow.getPosX() + radius / accuracy));
                accuracy = accuracy + 0.005;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            // It's ready
            isEveryArrowReady[positionOfBooleanArray] = true;
        }
    }
}

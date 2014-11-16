package player.weapon;

import general.LogFacility;
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

        AttackingThread [] attackingThreads = new AttackingThread[attackingArrows.size()];

        for (int i = 0; i < attackingArrows.size(); i++) {
            AbstractArrow attackingArrow = attackingArrows.get(i);
            AttackProgress attackProgress = filteredProgresses.get(i);

            attackingThreads[i] = new AttackingThread (attackingArrow, attackProgress, Thread.currentThread(), i);
            attackingThreads[i].setDaemon(true);
            attackingThreads[i].setPriority(3);
            attackingThreads[i].start();
        }

        /*
        The upper thread need to wait.
        If the thread doesn't wait, the activePlayer will change and the arrows cannot be seen.

        synchronized (this) {
            try {
                Thread.currentThread().wait();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        */

        for (AttackingThread attackingThread : attackingThreads) {
            try {
                attackingThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
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
        private Thread headingThread;

        AttackingThread (AbstractArrow attackingArrow, AttackProgress attackProgress, Thread currentThread, int positionOfBooleanArray) {
            this.attackingArrow = attackingArrow;
            this.attackProgress = attackProgress;
            this.positionOfBooleanArray = positionOfBooleanArray;
            headingThread = currentThread;
        }

        @Override
        public void run () {
            // alpha (radiant) is the ankle between the position of the aim and the current Point
            double alpha = FunctionCollection.angle(attackingArrow.getPosX(), attackingArrow.getPosY(), attackingArrow.getPosXAim(), attackingArrow.getPosYAim());

            // radius = lengthGUI * (1 - the percentage of the progress);
            double radius = attackProgress.event().lengthGUI() * (1 - attackProgress.progress());

            int posXOld = attackingArrow.getPosX();
            int posYOld = attackingArrow.getPosY();

            System.out.println("\nRadius: " + radius);

            double timeMulti = 200;

            // as long as MilliSec are smaller than the speed (in tiles/turn) * 500 milliSec (per turn per tile)
            while (milliSec < attackingArrow.getSpeed() * timeMulti) {
                double accuracy = milliSec / (attackingArrow.getSpeed() * timeMulti);

                LogFacility.log("MilliSec: " + milliSec + "\tx_current (radius reached): " +
                        ((int) ((radius * accuracy) * 1000) / 1000.0) + "\t(x|y): ( " + attackingArrow.getPosX() + " | " + attackingArrow.getPosY() + " )", LogFacility.LoggingLevel.Debug);

                // That isn't working

                attackingArrow.setPosY(posYOld +
                        (int) FunctionCollectionEasing.quadratic_easing_inOut(radius * accuracy, attackingArrow.getPosY(), attackingArrow.getPosYAim(), radius));
                attackingArrow.setPosX((int) (posXOld + radius * accuracy));


                /*

                // (milliSec / (attackingArrow.getSpeed() * timeMulti))  => accuracy
                attackingArrow.setPosY((int) (posYOld
                        + radius * Math.sin(alpha) * (attackingArrow.getPosYAim() - attackingArrow.getPosY())
                        * accuracy));

                attackingArrow.setPosX((int) (posXOld
                        + radius * Math.cos(alpha) * (attackingArrow.getPosXAim() - attackingArrow.getPosX())
                        * accuracy));

                */

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            // It's ready
            isEveryArrowReady[positionOfBooleanArray] = true;
            LogFacility.log("Arrow arrived!", LogFacility.LoggingLevel.Info);

            /*
            //wait & notify doesn't work

            if (isEveryAttackReady(isEveryArrowReady)) {
                synchronized (this) {
                    headingThread.notifyAll();
                }
            }
            */
        }
    }
}

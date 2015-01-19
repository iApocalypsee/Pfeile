package player.weapon;

import comp.ImageComponent;
import geom.functions.FunctionCollectionEasing;
import newent.AttackProgress;

import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class AttackingCalculator {
    /** this value increases every millisecond. It counts the time, after starting the flying process */
    private long milliSec;
    /** a time multiplier in milliseconds to calculate Tiles per turn to Tiles per (milli-)second. The higher TIME_MULTI, the longer the arrows will need to fly:
     * <p> <code>TIME_MULTI / attackingArrow.getSpeed()</code>*/
    private static final int TIME_MULTI = 1200;

    private class Clock extends TimerTask {
        @Override
        public void run () {
            milliSec++;
        }
    }

    public void arrowsFlying () {
        List<AttackProgress> filteredProgresses = AttackDrawer.getAttackProgressesOfArrows();

        // if there aren't any arrows to shot, there's nothing to do.
        if (filteredProgresses.size() <= 0)
            return;

        List<AbstractArrow> attackingArrows = new LinkedList<>();

        for (AttackProgress filteredProgress : filteredProgresses) {
            attackingArrows.add((AbstractArrow) filteredProgress.event().weapon());
        }

        java.util.Timer timer = new java.util.Timer("attackCalculatorScheduler", true);
        // every milliSec has to increase every millisecond
        timer.schedule(new Clock(), 0, 1);

        AttackingThread [] attackingThreads = new AttackingThread[attackingArrows.size()];

        for (int i = 0; i < attackingArrows.size(); i++) {
            AbstractArrow attackingArrow = attackingArrows.get(i);
            AttackProgress attackProgress = filteredProgresses.get(i);

            attackingThreads[i] = new AttackingThread (attackingArrow, attackProgress);
            attackingThreads[i].setDaemon(true);
            attackingThreads[i].setPriority(3);
            attackingThreads[i].start();
        }

        // waiting for the threads to stop their activity (to let the arrows arive
        for (AttackingThread attackingThread : attackingThreads) {
            try {
                attackingThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        // wait a little bit, that the user is able to recognize what happened.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) { e.printStackTrace(); }

        // if ready stop the timer and reset the milli-seconds after beginning
        timer.cancel();
        milliSec = 0;
    }

    /** for every attack, we need to run one thread */
    private class AttackingThread extends Thread {
        private AbstractArrow attackingArrow;
        private AttackProgress attackProgress;

        AttackingThread (AbstractArrow attackingArrow, AttackProgress attackProgress) {
            this.attackingArrow = attackingArrow;
            this.attackProgress = attackProgress;
        }

        @Override
        public void run () {
            // alpha (radiant) is the ankle between the position of the aim and the current Point
            // double alpha = FunctionCollection.angle(attackingArrow.getPosX(), attackingArrow.getPosY(), attackingArrow.getPosXAim(), attackingArrow.getPosYAim());

            // radius = lengthGUI * (1 - the percentage of the progress);
            // double radius = attackProgress.event().lengthGUI() * (1 - attackProgress.progress());

            // radius = the length / (tileLength / speed in Tiles / Turn)
            double distanceToCover = attackProgress.event().lengthGUI() / attackProgress.event().travelSpeed();

            int posXOld = attackingArrow.getComponent().getX();
            int posYOld = attackingArrow.getComponent().getY();

            // System.out.println("\nRadius: " + radius + "\tDistance: " + attackProgress.event().lengthGUI() + "\tDistance/Radius: " + (attackProgress.event().lengthGUI() / radius));
            // System.out.println("countRounds: " + attackProgress.event().lengthPerTurn());

            // as long as MilliSec are smaller than (TIME_MULTI / attackingArrow.getSpeed()) (per turn per tile)
            while (milliSec < TIME_MULTI / attackingArrow.getSpeed()) {
                double accuracy = milliSec / (TIME_MULTI / attackingArrow.getSpeed());

                //LogFacility.log("MilliSec: " + milliSec + "\tx_current (radius reached): " +
                //        ((int) ((radius * accuracy) * 1000) / 1000.0) + "\t(x|y): ( " + attackingArrow.getPosX() + " | " + attackingArrow.getPosY() + " )", LogFacility.LoggingLevel.Debug);


                // The progress to stretch the normalized position to the real position
                double progress = distanceToCover / attackProgress.event().lengthGUI();

                final ImageComponent movedComponent = attackingArrow.getComponent();

                // TODO Replace the quadratic_easing_inOut with a Beziér curve?
                // I don't understand the usage of quadratic_easing_inOut anymore, because I cannot
                // imagine such complicated things.
                // Here should be the bug with the arrow not flying in the right direction.
                movedComponent.setX((int) (posXOld + Math.round(
                        FunctionCollectionEasing.quadratic_easing_inOut(distanceToCover * accuracy, 0, attackingArrow.getAim().getPosXGui() - posXOld, distanceToCover)
                                * progress)));

                movedComponent.setY((int) (posYOld + Math.round(
                        FunctionCollectionEasing.quadratic_easing_inOut(distanceToCover * accuracy, 0, attackingArrow.getAim().getPosYGui() - posYOld, distanceToCover)
                                * progress)));

                 try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

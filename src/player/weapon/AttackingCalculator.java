package player.weapon;

import comp.Component;
import comp.ImageComponent;
import general.Main;
import geom.functions.FunctionCollectionEasing;
import newent.AttackProgress;
import org.w3c.dom.css.Rect;
import player.weapon.arrow.AbstractArrow;
import world.TileLike;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class AttackingCalculator {

    /** this value increases every millisecond. It counts the time, after starting the flying process */
    private long milliSec;

    /** a time multiplier in milliseconds to calculate Tiles per turn to Tiles per (milli-)second. The higher TIME_MULTI, the longer the arrows will need to fly:
     * <p> <code>TIME_MULTI / attackingArrow.getSpeed()</code>*/
    private static final int TIME_MULTI = 1500;

    /** The threads are saved in this list. It contains all AttackingThreads, which haven't been arrived yet (progress != 1). */
    private ArrayList<Thread> attackingArrows;

    /** the singleton-instance */
    private static AttackingCalculator instance;

    /** only one AttackingCalculator can exist, because the old threads has to continue. */
    private static AttackingCalculator getInstance () {
        if (instance == null)
            instance = new AttackingCalculator();
        return instance;
    }

    public AttackingCalculator () {
        this.attackingArrows = new ArrayList<>();
    }


    public void arrowsFlying () {
        List<AttackProgress> filteredProgresses = AttackDrawer.getAttackProgressesOfArrows();

        // if there aren't any arrows to shot, there's nothing to do.
        if (filteredProgresses.isEmpty())
            return;

        List<AbstractArrow> attackingArrows = AttackDrawer.getAttackingArrows();

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

        // waiting for the threads to stop their activity (to let the arrows arrive)
        for (AttackingThread attackingThread : attackingThreads) {
            try {
                attackingThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        // if ready stop the timer and reset the milli-seconds after beginning
        timer.cancel();
        milliSec = 0;

        // wait a little bit, that the user is able to recognize what happened.
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) { e.printStackTrace(); }

    }


    /** This Timer increments <code>milliSec</code>. */
    private class Clock extends TimerTask {
        @Override
        public void run () {
            milliSec++;
        }
    }

    /** for every attack, we need to run one thread */
    private class AttackingThread extends Thread {
        private AbstractArrow attackingArrow;
        private AttackProgress attackProgress;

        /** the percentage of the progress, the position of the arrow has reached. It is the changing variable of the
         * FunctionCollectionFunctions. It's saved here because the the Thread has to run several times.
         * If the arrow has arrived progress is <code>1</code>. */
        private double progress;

        AttackingThread (AbstractArrow attackingArrow, AttackProgress attackProgress) {
            this.attackingArrow = attackingArrow;
            this.attackProgress = attackProgress;
        }

        @Override
        public void run () {
            Component comp = attackingArrow.getComponent();
            double posXOldCenter = comp.getPreciseRectangle().getCenterX();
            double posYOldCenter = comp.getPreciseRectangle().getCenterY();

            Point attackedCenter = attackingArrow.getAim().getPositionGui();
            double posXAimCenter = attackedCenter.getX();
            double posYAimCenter = attackedCenter.getY();

            // alpha (radiant) is the ankle between the position of the aim and the current Point
            // double alpha = FunctionCollection.angle(attackingArrow.getPosX(), attackingArrow.getPosY(), attackingArrow.getPosXAim(), attackingArrow.getPosYAim());


            double distanceToCover = attackProgress.event().lengthGUI();

            while (milliSec < TIME_MULTI / attackingArrow.getSpeed()) {
                double accuracy = milliSec / (TIME_MULTI / attackingArrow.getSpeed());

                double changeInX = FunctionCollectionEasing.quadratic_easing_inOut(
                        distanceToCover * accuracy, 0, posXAimCenter - posXOldCenter, distanceToCover);

                double changeInY = FunctionCollectionEasing.quadratic_easing_inOut(
                        distanceToCover * accuracy, 0, posYAimCenter - posYOldCenter, distanceToCover);






                final ImageComponent component = attackingArrow.getComponent();

                // refreshing the screen-position
                component.setLocation((int) (posXOldCenter + changeInX), (int) (posYOldCenter + changeInY));

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Rectangle2D boundsArrow = attackingArrow.getComponent().getPreciseRectangle();

            // refreshing the tile-position
            TileLike newTile = Main.getContext().getWorld().terrain().findTileJava(
                    boundsArrow.getCenterX(), boundsArrow.getCenterY());

            if (newTile != null) {
                attackingArrow.setGridX(newTile.getGridX());
                attackingArrow.setGridY(newTile.getGridY());
            }
        }
    }
}

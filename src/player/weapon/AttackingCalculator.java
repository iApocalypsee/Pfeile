package player.weapon;

import comp.Component;
import comp.ImageComponent;
import general.Main;
import geom.Vector2;
import geom.functions.FunctionCollectionEasing;
import newent.AttackProgress;
import player.weapon.arrow.AbstractArrow;
import scala.Tuple2;
import world.TileLike;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class AttackingCalculator {

    /** this value increases every millisecond. It counts the time, after starting the flying process */
    private long milliSec;

    /** a time multiplier in milliseconds to calculate Tiles per turn to Tiles per (milli-)second. The higher TIME_MULTI, the longer the arrows will need to fly:
     * <p> <code>TIME_MULTI / attackingArrow.getSpeed()</code>*/
    private static final int TIME_MULTI = 1500;

    /** The threads are saved in this list. It contains all AttackingThreads, which haven't been arrived yet (progress != 1). */
    private LinkedList<AttackingThread> attackingThreads;

    /** the singleton-instance */
    private static AttackingCalculator instance;

    /** only one AttackingCalculator can exist, because the old threads has to continue. */
    public static AttackingCalculator getInstance () {
        if (instance == null)
            instance = new AttackingCalculator();
        return instance;
    }

    public AttackingCalculator () {
        attackingThreads = new LinkedList<>();
    }

    /** <b><code>AttackingCalculator.getInstance().arrowsFlying(AttackDrawer.getAttackProgressesOfArrows());</code></b> */
    public void arrowsFlying (List<AttackProgress> filteredProgresses) {
        List<AbstractArrow> attackingArrows = AttackDrawer.getAttackingArrows();

        executeForEvery(filteredProgresses, attackingArrows);

        java.util.Timer timer = new java.util.Timer("attackCalculatorScheduler", true);
        // every milliSec has to increase every millisecond
        timer.schedule(new Clock(), 0, 1);

        for (int i = 0; i < attackingArrows.size(); i++) {
            AbstractArrow attackingArrow = attackingArrows.get(i);
            AttackProgress attackProgress = filteredProgresses.get(i);

            AttackingThread attack = new AttackingThread (attackingArrow, attackProgress);
            attack.setDaemon(true);
            attack.setPriority(3);
            attackingThreads.add(attack);
            attack.start();
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

    /** calls <code> arrowsFlying(AttackDrawer.getAttackProgressesOfArrows()) </code>. If there are no arrows, this
     * method does nothing. Use arrowsFlying(List...), because you can leave AttackingScreen directly, if there are no arrows. */
    public void arrowsFlying () {
        List<AttackProgress> filteredProgresses = AttackDrawer.getAttackProgressesOfArrows();

        if (!filteredProgresses.isEmpty())
            arrowsFlying(filteredProgresses);
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

            // it's only a part of the distance the arrow has to fly...
            //double maxDistanceToCover = attackProgress.event().lengthGUI();
            //double distanceToCover = maxDistanceToCover / attackProgress.numberOfTurns();
            double distanceToCover = attackProgress.event().lengthGUI();


            while (milliSec < TIME_MULTI / attackingArrow.getSpeed()) {
                double accuracy = (milliSec / TIME_MULTI) * attackingArrow.getSpeed();

                double changeInX = FunctionCollectionEasing.quadratic_easing_inOut(
                        distanceToCover * accuracy, 0, posXAimCenter - posXOldCenter, distanceToCover);

                double changeInY = FunctionCollectionEasing.quadratic_easing_inOut(
                        distanceToCover * accuracy, 0, posYAimCenter - posYOldCenter, distanceToCover);


                final ImageComponent component = attackingArrow.getComponent();

                // refreshing the screen-position
                component.setCenteredLocation((int) (posXOldCenter + changeInX), (int) (posYOldCenter + changeInY));

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
            if (newTile == null) {
                newTile = Main.getContext().getWorld().terrain().findTileJava(
                        boundsArrow.getCenterX() + 1, boundsArrow.getCenterY() + 1);
            }

            if (newTile != null) {
                attackingArrow.setGridX(newTile.getGridX());
                attackingArrow.setGridY(newTile.getGridY());
            }
        }
    }

    // TEST:
    // PATH PREDICTION

    //<editor-fold desc="Path prediction tests">

    private static final List<Path2D> pathList = new ArrayList<>();

    public static void drawPaths(Graphics2D g) {
        g.setColor(Color.magenta);
        pathList.forEach(g::draw);
    }

    private static Path2D[] executeForEvery(List<AttackProgress> progresses, List<AbstractArrow> arrows) {
        pathList.clear();

        List<Tuple2<AttackProgress, AbstractArrow>> tupled = new ArrayList<>();
        for(int i = 0; i < arrows.size(); ++i) {
            tupled.add(new Tuple2<>(progresses.get(i), arrows.get(i)));
        }

        final Stream<Path2D> pathStream = tupled.stream().map(tuple -> predictedPath(tuple._1(), tuple._2()));

        final Path2D[] pathArray = pathStream.toArray(Path2D[]::new);

        pathList.clear();
        Collections.addAll(pathList, pathArray);

        return pathArray;
    }

    private static Path2D predictedPath(AttackProgress progress, AbstractArrow arrow) {
        // Customizable variables.
        final double beginProgress = progress.progress();
        final double progressPerTurn = progress.progressPerTurn();
        final Path2D.Double resultingPath = new Path2D.Double();
        final Point initialCenterPosition = arrow.getComponent().center();

        // Begin of real method execution
        resultingPath.moveTo(initialCenterPosition.x, initialCenterPosition.y);

        Vector2 sectionBeginPoint = Vector2.apply(initialCenterPosition.x, initialCenterPosition.y);

        for(double simulatedProgress = beginProgress; simulatedProgress < 1.0; simulatedProgress += progressPerTurn) {
            final Vector2 sectionEndPoint = AttackingCalculatorCompanion.sectionEndPoint(
                    0, sectionBeginPoint, arrow, sectionBeginPoint.toPoint(), TIME_MULTI, arrow.getAim(), progress.event().lengthGUI());
            resultingPath.lineTo(sectionEndPoint.x(), sectionEndPoint.y());
            sectionBeginPoint = sectionEndPoint;
        }

        return resultingPath;
    }

    public static List<Path2D> getPathList() {
        return Collections.unmodifiableList(pathList);
    }

    //</editor-fold>
}

package player.weapon;

import gui.Drawable;
import newent.event.AttackEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class will draw an animation, when an arrow (<b>only Arrows!</b>)impacts. It is called by the Delegate <code>onImpact</code>, 
 * which is registered in TileLike. For Handling the draw-process use {@link player.weapon.ImpactDrawerHandler}.
 */
class ImpactDrawer implements Drawable {

    /** this value increases every millisecond. It counts the time, after starting drawingProcess. */
    private long milliSec;

    private Rectangle bounding;

    private Rectangle boundingEnd;

    private Color damageColor;

    private Timer timer;
    
    ImpactDrawer (AttackEvent event) {
        // the weapon need to be an AbstractArrow
        //assert event.weapon() instanceof AbstractArrow;

        AbstractArrow arrow = (AbstractArrow) event.weapon();

        damageColor = ArrowHelper.getUnifiedColor(arrow.getName());

        bounding = new Rectangle((int) arrow.getComponent().getBounds().getBounds().getCenterX(), (int) arrow.getComponent().getBounds().getBounds().getCenterY());

        boundingEnd = new Rectangle((int) arrow.getAim().getPosXGui(), (int) arrow.getAim().getPosYGui(), (int) arrow.getAim().getDamageRadiusGUIWidth() * 2, (int) arrow.getAim().getDamageRadiusGUIWidth() * 2 );

        milliSec = 0;

        // TODO use amazing textures
    }

    void startAnimation () {
        Thread x = new ImpactAnimationThread();
        x.setDaemon(true);
        x.setPriority(7);
        x.start();
    }
    
    
    private class ImpactAnimationThread extends Thread {
        /** the maximum milliseconds till end of animation */
        private static final int MILLI_SEC = 3000;

        @Override
        public void run () {
            timer = new java.util.Timer("impactDrawerScheduler", true);
            // every milliSec has to increase every millisecond
            timer.schedule(new Clock(), 0, 1);

            int oldLocationX = bounding.x;
            int oldLocationY = bounding.y;


            // to reassure, that some threads aren't running faster than other.
            milliSec = 0;

            while (milliSec <= MILLI_SEC) {
                // the progress is between 0 and 1
                double progress = milliSec / (double) MILLI_SEC;
                bounding.setSize((int) (boundingEnd.width * progress),(int) (boundingEnd.height  * progress));
                bounding.setLocation(oldLocationX - bounding.width / 2, oldLocationY - bounding.width / 2);

                damageColor = new Color(damageColor.getRed(), damageColor.getGreen(), damageColor.getBlue(), (int) (200 * progress));

                // it can't be drawn more often
                try {
                    Thread.sleep(12);
                } catch (InterruptedException e) { e.printStackTrace(); }
            }

            // done
            timer.cancel();

            // some time for the user to see the rest of the effect, before it disappears, when it's removed from the list in ImpactDrawerHandler
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) { e.printStackTrace(); }

            // when ready, remove this Object is no longer needed.
            ImpactDrawerHandler.removeImpactDrawer(ImpactDrawer.this);
        }
    }

    private class Clock extends TimerTask {
        @Override
        public void run () {
            milliSec++;
        }
    }

    /** Redirect: <b>This method is already called by class <code>ImpactDrawerHandler</code>. Use the static methods from
    * {@link player.weapon.ImpactDrawerHandler}</b> */
    @Override
    public void draw (Graphics2D g) {
        g.setColor(damageColor);
        g.fillOval(bounding.x, bounding.y, bounding.width, bounding.height);

        //System.out.println("DRAWING " + damageColor + "  " + bounding);
    }
}

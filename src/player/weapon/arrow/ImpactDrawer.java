package player.weapon.arrow;

import gui.Drawable;
import newent.event.AttackEvent;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class will draw an animation, when an arrow (<b>only Arrows!</b>)impacts. It is called by the Delegate <code>onImpact</code>, 
 * which is registered in Tile. For Handling the draw-process use {@link ImpactDrawerHandler}.
 */
class ImpactDrawer implements Drawable {

    /** this value increases every millisecond. It counts the time, after starting drawingProcess. */
    private long milliSec;

    /** the current boundingBox of the explosion at the impact */
    private Rectangle bounding;

    /** TODO: the boundingBox of the center of the explosion. When we draw with BufferedImage, this is no longer needed. */
    private Rectangle boundingInner;

    /** the boundingBox at the end of the explosion. [at its maximum size] */
    private Rectangle boundingEnd;

    /** the color of the impact - equal to the UNIFIED_COLOR of the arrow, with changing Alpha-value */
    private Color damageColor;

    /** the color of the center of the impact. */
    private Color damageColorInner;

    ImpactDrawer (AttackEvent event) {
        // the weapon need to be an AbstractArrow
        //assert event.weapon() instanceof AbstractArrow;

        AbstractArrow arrow = (AbstractArrow) event.weapon();

        damageColor = ArrowHelper.getUnifiedColor(arrow.getName());
        damageColorInner = damageColor;

        bounding = new Rectangle((int) arrow.getComponent().getBounds().getBounds().getCenterX(), (int) arrow.getComponent().getBounds().getBounds().getCenterY(), 0, 0);

        boundingInner = new Rectangle(bounding.x, bounding.y, 0, 0);

        boundingEnd = new Rectangle((int) arrow.getAim().getPosXGui(), (int) arrow.getAim().getPosYGui(),
                (int) arrow.getAim().getDamageRadiusGUIWidth(), (int) arrow.getAim().getDamageRadiusGUIHeight());

        // TODO use amazing textures :D
    }

    /** This will start the animation of the impact. The duration will be <code>ImpactDrawer.ImpactAnimationThread.MILLI_SEC</code> (= 3000ms)*/
    void startAnimation () {
        Thread x = new ImpactAnimationThread();
        x.setDaemon(true);
        x.setPriority(7);
        x.start();
    }

    /** This Thread draws the explosion of the impact, if <code>impactDrawer.startAnimation()</code> is called.
     * The Thread scales the texture/bounds of the impact automatically.
     */
    private class ImpactAnimationThread extends Thread {
        /** the maximum milliseconds till end of animation */
        private static final int MILLI_SEC = 3000;

        @Override
        public void run () {
            /* The timer is counting every milliSecond ["milliSec++;"] */
            Timer timer = new Timer("impactDrawerScheduler", true);
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
                bounding.setLocation(oldLocationX - bounding.width / 2, oldLocationY - bounding.height / 2);

                boundingInner.setSize((int) ((bounding.width / 4.0) * progress + 10), (int) ((bounding.height / 4.0) * progress + 10));
                boundingInner.setLocation(oldLocationX - boundingInner.width / 2, oldLocationY - boundingInner.height / 2);

                damageColor = new Color(damageColor.getRed(), damageColor.getGreen(), damageColor.getBlue(), (int) (240 * (1 - progress)));
                damageColorInner = new Color(damageColorInner.getRed(), damageColorInner.getGreen(), damageColorInner.getBlue(), (int) (140 * (1 - progress)));

                // it can't be drawn more often
                try {
                    Thread.sleep(10);
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
    * {@link ImpactDrawerHandler}</b> */
    @Override
    public void draw (Graphics2D g) {
        g.setColor(damageColor);
        g.fillOval(bounding.x, bounding.y, bounding.width, bounding.height);
        g.setColor(damageColorInner);
        g.fillOval(boundingInner.x, boundingInner.y, boundingInner.width, boundingInner.height);
    }
}

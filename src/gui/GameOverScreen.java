package gui;

import comp.Button;
import general.GameLoop;
import general.Main;
import scala.runtime.AbstractFunction0;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** This is the Screen being called at the end of the Game */
public class GameOverScreen extends Screen {
    public static final int SCREEN_INDEX = 13;
    public static final String SCREEN_NAME = "Game Over";

    private Color transparentBackground;

    private Button closeGame;
    private Font font_GameOver;
    private Font font_YouLose;
    private volatile Color lastColorOfYouLose;
    private volatile Color gameOverColor;
    private Thread calcTimeThread = null;
    private int counter = 0;
    /** the time since the Thread started in Millisecounds */
    private long timer = 0;
    private boolean runFlag = true;

    /**
     * The GameOverScreen should be called at the end of the game if the player had lost.
     * It will write "GAME OVER - You Lose" or something like that on the Screen, so the player knows what is going on.
     * Later, there will be a list with the facts and figures of the game as well.
     */
    public GameOverScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);
        transparentBackground = new Color (42, 40, 48, 0);

        lastColorOfYouLose = new Color(213, 192, 24, 0);
        gameOverColor = new Color(247, 16, 11, 26);

        font_GameOver = new Font("28 Days Later", Font.PLAIN, 300);
        if (comp.Component.isFontInstalled(font_GameOver) == false) {
            font_GameOver = new Font("ShadowedGermanica", Font.BOLD, 310);
            if (comp.Component.isFontInstalled(font_GameOver) == false)
                font_GameOver = new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 310);
        }

        font_YouLose = new Font("Aladdin", Font.BOLD, 260);
        if (comp.Component.isFontInstalled(font_GameOver) == false) {
            font_YouLose = new Font("18thCentury", Font.BOLD, 245);
            if (comp.Component.isFontInstalled(font_GameOver) == false)
                font_YouLose = new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 245);
        }


        closeGame = new Button(Main.getWindowWidth() - 150, Main.getWindowHeight() - 100, this, "Beenden");

        closeGame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased (MouseEvent e) {
                runFlag = false;
                GameLoop.setRunFlag(false);
            }
        });

        calcTimeThread = new Thread(new Runnable() {
            private long lastFrame;
            private long thisFrame;

            @Override
            public void run () {
                lastFrame = System.currentTimeMillis();
                while (runFlag) {
                    thisFrame = System.currentTimeMillis();
                    timer = timer + thisFrame - lastFrame;
                    counter++;
                    calcColor();
                    lastFrame = thisFrame;

                    try {
                        Thread.sleep((long) (1000 / 60.0));
                    } catch (InterruptedException e) { e.printStackTrace();}
                }
            }
        });
        calcTimeThread.setPriority(2);
        calcTimeThread.setDaemon(true);

        onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply () {
                calcTimeThread.start();
                return BoxedUnit.UNIT;
            }
        });
    }

    /** returns the Color of the String "You Lose!". It uses "timer", calculated by the Thread, to change the Color in
     * a way, that the String will sparkle. */
    protected void calcColor () {
        if (transparentBackground.getAlpha() < 255 && counter % 2 == 0)
            transparentBackground = new Color(transparentBackground.getRed(), transparentBackground.getGreen(), transparentBackground.getBlue(), transparentBackground.getAlpha() + 1);

        // Old Version getting black and wight
        //int red, green, blue;
        // sin(x / (0.1 * Math.PI)) sind die Nullstellen bei 0+- k * 1 mit k aus den Ganzen Zahlen
        // jede 1/60s: counter++
        // ==> bei counter % 30 : 0.5s
        // bei sin(counter/(pi*6))^2 jede Sekunde ist die Nullstelle erreicht. dh. innerhalb einer S ändert er die Farbe
        // von ganz dunkel zu ganz hell zu ganz dunkel
        double sin = Math.sin(counter/(Math.PI * 102));
        double sin2 = Math.sin(counter/(Math.PI * 51));
        //red = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //green = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //blue = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //return new Color(red , green, blue);

        // hue: einfach durch den farbkeis kreisen (blau, rot, grün,...)
        // staturation: sieht wie eine MMMMMMM formige sinusfunktion aus, bei 1 gibt es kräftige farben, bei 0 weiß(grau-schwarz)
        // brighness: immer ganz hell (bei 1), d.h. kein scharz oder dunkle farben
        Color colorTemp = new Color(Color.HSBtoRGB((float) (counter / (Math.PI * 84)), (float) ((sin * sin + sin2 * sin2) / 1.6), 1f));
        // erst nach 4s fängt YouLose an, aber dreifach so schnell wie gameOver
        if (lastColorOfYouLose.getAlpha() < 255 && timer > 3100 && (counter % 3 == 0 || counter % 4 == 0))
            lastColorOfYouLose = new Color(colorTemp.getRed(), colorTemp.getGreen(), colorTemp.getBlue(), lastColorOfYouLose.getAlpha() + 1);
        else
            lastColorOfYouLose = new Color(colorTemp.getRed(), colorTemp.getGreen(), colorTemp.getBlue(), lastColorOfYouLose.getAlpha());
        if (counter % 3 == 0 && gameOverColor.getAlpha() < 255)
            gameOverColor = new Color(gameOverColor.getRed(), gameOverColor.getGreen(), gameOverColor.getBlue(), gameOverColor.getAlpha() + 1);
    }

    @Override
    public void keyPressed (KeyEvent keyEvent) {
        // "Beenden" - Button
        if (keyEvent.getKeyCode() == KeyEvent.VK_B) {
            // this should be the same code like in closeGame.mouseReleased
            // if not, make it the same
            runFlag = false;
            GameLoop.setRunFlag(false);
        }
    }

    @Override
    public void draw (Graphics2D g) {
        if (transparentBackground.getAlpha() < 255) {
            // Draw the world and the player and the arrows, that are still flaying
            GameScreen.getInstance().getMap().draw(g);
            GameScreen.getInstance().getVisualEntity().draw(g);
            GameScreen.getInstance().getAttackDrawer().draw(g);
        }

        // and now draw a transparent background over it
        g.setColor(transparentBackground);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

        g.setColor(gameOverColor);
        g.setFont(font_GameOver);
        g.drawString("Game Over", 20, 300);

        g.setColor(lastColorOfYouLose);
        g.setFont(font_YouLose);
        g.drawString("You Lose!", 108, 590);

        System.out.println("BACKGROUND: " + transparentBackground.getAlpha());
        System.out.println("GAME OVER: " + gameOverColor.getAlpha());
        System.out.println();

        closeGame.draw(g);
    }
}

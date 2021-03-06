package gui.screen;

import comp.Button;
import comp.Component;
import general.GameLoop;
import general.GameWindow;
import general.io.FontLoader;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

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
    private double scaleGameOver;
    private Point scaleCenter;
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
        gameOverColor = new Color(209, 15, 8, 0);
        scaleGameOver = 0.0025;

        font_GameOver = FontLoader.loadFont("18thCentury", 260, Font.BOLD, FontLoader.FontType.TTF);
        font_YouLose = FontLoader.loadFont("PlainGermanica", 220, FontLoader.FontType.TTF);

        // von g.drawString("Game Over", 50 - scaleCenter.x, 300 - scaleCenter.y);
        scaleCenter = new Point((int) (50 + 0.5 * comp.Component.getTextBounds("Game Over", font_GameOver).getWidth()), (int) (300 + 0.5 * Component.getTextBounds("Game Over", font_GameOver).getHeight()));

        closeGame = new Button(GameWindow.WIDTH - 170, GameWindow.HEIGHT - 100, this, "Close Game");

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
                        Thread.sleep((long) (1000 / 80.0));
                    } catch (InterruptedException e) { e.printStackTrace();}
                }
            }
        });
        calcTimeThread.setPriority(4);
        calcTimeThread.setDaemon(true);

        onScreenEnter.registerJava(calcTimeThread:: start);
    }

    /** returns the Color of the String "You Lose!". It uses "timer", calculated by the Thread, to change the Color in
     * a way, that the String will sparkle. */
    protected void calcColor () {
        if (transparentBackground.getAlpha() < 255 && counter % 2 == 0)
            transparentBackground = new Color(transparentBackground.getRed(), transparentBackground.getGreen(), transparentBackground.getBlue(), transparentBackground.getAlpha() + 1);
        // 60mal pro Sekunde ==>
        if (scaleGameOver < 1.0) {
            scaleGameOver = scaleGameOver + 0.0025;
            if (scaleGameOver > 1.0)
                scaleGameOver = 1.0;
        }

        // Old Version getting black and wight
        //int red, green, blue;
        // sin(x / (0.1 * Math.PI)) sind die Nullstellen bei 0+- k * 1 mit k aus den Ganzen Zahlen
        // jede 1/60s: counter++
        // ==> bei counter % 30 : 0.5s
        // bei sin(counter/(pi*6))^2 jede Sekunde ist die Nullstelle erreicht. dh. innerhalb einer S ändert er die Farbe
        // von ganz dunkel zu ganz hell zu ganz dunkel
        double sin = Math.sin(counter/(Math.PI * 125));
        double sin2 = Math.sin(counter/(Math.PI * 66));
        //red = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //green = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //blue = (int) Math.round(Math.abs(sin * sin * sin * 255.0));
        //return new Color(red , green, blue);

        // hue: einfach durch den farbkeis kreisen (blau, rot, grün,...)
        // staturation: sieht wie eine MMMMMMM formige sinusfunktion aus, bei 1 gibt es kräftige farben, bei 0 weiß(grau-schwarz)
        // brighness: immer ganz hell (bei 1), d.h. kein scharz oder dunkle farben
        // colorTemp is used for ALL_COLOURS_CHANGING
        //Color colorTemp = new Color(Color.HSBtoRGB((float) (counter / (Math.PI * 92)), (float) ((sin * sin + sin2 * sin2) / 1.6), 1f));

        // erst nach 3.1s f�ngt YouLose an, aber ~2.5 so schnell wie gameOver
        if (lastColorOfYouLose.getAlpha() < 255 && timer > 4000 && counter % 3 == 0)
            /*RED_CHANGING:*/ lastColorOfYouLose = new Color((int) (Math.sin(counter / (Math.PI * 70)) * Math.sin(counter / (Math.PI * 70)) * 25 + 230),
                    (int) (Math.sin(counter/(Math.PI * 81)) * Math.sin(counter/(Math.PI * 81)) * 255),
                    (int) (Math.sin(counter / (Math.PI * 71)) * Math.sin(counter / (Math.PI * 71)) * 35), lastColorOfYouLose.getAlpha() + 1);
            //ALL_COLOURS_CHANGING:lastColorOfYouLose = new Color(colorTemp.getRed(), colorTemp.getGreen(), colorTemp.getBlue(), lastColorOfYouLose.getAlpha() + 1);
        else
            /*RED_CHANGING:*/ lastColorOfYouLose = new Color((int) (Math.sin(counter / (Math.PI * 70)) * Math.sin(counter / (Math.PI * 70)) * 25 + 230),
                    (int) (Math.sin(counter/(Math.PI * 81)) * Math.sin(counter/(Math.PI * 81)) * 255),
                    (int) (Math.sin(counter / (Math.PI * 71)) * Math.sin(counter / (Math.PI * 71)) * 35), lastColorOfYouLose.getAlpha());
            //ALL_COLOURS_CHANGING: lastColorOfYouLose = new Color(colorTemp.getRed(), colorTemp.getGreen(), colorTemp.getBlue(), lastColorOfYouLose.getAlpha());

        if (gameOverColor.getAlpha() < 255)
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
            AttackingScreen.getInstance().getAttackDrawer().draw(g);
        }

        // and now draw a transparent background over it
        g.setColor(transparentBackground);
        g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);

        g.setColor(gameOverColor);
        g.setFont(font_GameOver);
        AffineTransform transform = g.getTransform();
        g.translate(scaleCenter.x, scaleCenter.y);
        g.scale(scaleGameOver, scaleGameOver);
        g.drawString("Game Over", 50 - scaleCenter.x, 300 - scaleCenter.y);

        g.setTransform(transform);
        g.setColor(lastColorOfYouLose);
        g.setFont(font_YouLose);
        g.drawString("You Lose!", 108, 590);

        g.setFont(Component.STD_FONT);

        closeGame.draw(g);
    }
}

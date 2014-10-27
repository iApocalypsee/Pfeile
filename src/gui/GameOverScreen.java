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
import java.util.Random;

/** This is the Screen being called at the end of the Game */
public class GameOverScreen extends Screen {
    public static final int SCREEN_INDEX = 13;
    public static final String SCREEN_NAME = "Game Over";

    private final Color TRANSPAREND_BACKGROUND = new Color (60, 60, 87, 96);

    private Button closeGame;
    private Font font_GameOver;
    private Font font_YouLose;
    private Color lastColorOfYouLose;
    private Thread calcTimeThread = null;
    private int counter = 0;
    private boolean runFlag = true;

    /**
     * The GameOverScreen should be called at the end of the game if the player had lost.
     * It will write "GAME OVER - You Lose" or something like that on the Screen, so the player knows what is going on.
     * Later, there will be a list with the facts and figures of the game as well.
     */
    public GameOverScreen () {
        super(SCREEN_NAME, SCREEN_INDEX);

        lastColorOfYouLose = new Color(213, 192, 24);

        font_GameOver = new Font("28 Days Later", Font.BOLD, 380);
        if (comp.Component.isFontInstalled(font_GameOver) == false) {
            font_GameOver = new Font("ShadowedGermanica", Font.BOLD, 380);
            if (comp.Component.isFontInstalled(font_GameOver) == false)
                font_GameOver = new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 380);
        }
        // TODO: Schriftarten wählen
        font_YouLose = new Font("Aladdin", Font.BOLD, 320);
        if (comp.Component.isFontInstalled(font_GameOver) == false) {
            font_YouLose = new Font("18thCentury", Font.BOLD, 300);
            if (comp.Component.isFontInstalled(font_GameOver) == false)
                font_YouLose = new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 300);
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

            /** current Time from calcTimeThread in MilliSecounds*/
            private long timer = 0;

            @Override
            public void run () {
                lastFrame = System.currentTimeMillis();
                while (runFlag) {
                    thisFrame = System.currentTimeMillis();
                    timer = timer + (int) (thisFrame - lastFrame);
                    counter++;
                    lastColorOfYouLose = calcColor();
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
    public Color calcColor () {
        new Color (243, 197, 26);
        int red, green, blue;
        red = lastColorOfYouLose.getRed();
        green = lastColorOfYouLose.getGreen();
        blue = lastColorOfYouLose.getBlue();

        // every 1/8 secound
        if (counter % (60 * 0.125)== 0) {
            red = (int) (Math.abs(Math.sin(counter)) * 240);
        }
        // every 1/4 secound
        if (counter % (60 * 0.25) == 0) {
            green = (int) (Math.abs(Math.sin(counter - Math.PI / 4)) * 240);
        }
        // every 1/2 secound
        if (counter % (60 * 0.5) == 0) {
            blue = (int) (Math.abs(Math.sin(counter - Math.PI)) * 240);
        }

        return new Color(red , green, blue);
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
        // Draw the world and the player and the arrows, that are still flaying
        GameScreen.getInstance().getMap().draw(g);
        GameScreen.getInstance().getVisualEntity().draw(g);
        GameScreen.getInstance().getAttackDrawer().draw(g);
        // and now draw a transparent background over it
        g.setColor(TRANSPAREND_BACKGROUND);
        g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());

        g.setColor(new Color(247, 16, 11));
        g.setFont(font_GameOver);
        g.drawString("Game Over", 20, 30);

        g.setColor(lastColorOfYouLose);
        g.setFont(font_YouLose);
        g.drawString("You Lose!", 60, 600);

        closeGame.draw(g);
    }
}

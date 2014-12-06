package gui;

import animation.AnimatedLine;
import comp.Button;
import general.Main;
import newent.Player;
import newent.VisionStatus;
import newent.event.AttackEvent;
import player.weapon.AbstractArrow;
import player.weapon.ArrowHelper;
import player.weapon.Item;
import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import world.TileLike;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AimSelectionScreen extends Screen {
	
	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;

	/** X-Position des Ausgew�hlten Feldes 
	 * * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1)*/
	private volatile int posX_selectedField;
	
	/** Y-Position des Ausgew�hlten Feldes 
	 * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1) */
	private volatile int posY_selectedField;
	
	/** Best�tigen-Button */
	private Button confirm;
	
	// These are only for the warning Message
	private String warningMessage = "";
	private float transparencyWarningMessage = 0;
	private Point positionWarningMessage = new Point (40, Main.getWindowHeight() - 105);

    /** the animated line from the player to the aim */
    private AnimatedLine animatedLine;
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt die Variabelnwerte nach der Initialisierung; start den thread of <code> FieldSelector </code> */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		setPosX_selectedField(-1);
		setPosY_selectedField(-1);
		
		confirm = new Button (1178, 491, this, "Bestätigen");

        animatedLine = new AnimatedLine(0,0,0,0,Color.RED);
        animatedLine.setWidth(3.0f);

        onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply () {
                animatedLine.setStartX((int) Main.getContext().getActivePlayer().getComponent().getBounds().getBounds().getCenterX());
                animatedLine.setStartY((int) Main.getContext().getActivePlayer().getComponent().getBounds().getBounds().getCenterY());
                animatedLine.setColor(ArrowHelper.getUnifiedColor(ArrowSelectionScreen.getInstance().getSelectedIndex()));
                transparencyWarningMessage = 0.0f;
                return BoxedUnit.UNIT;
            }
        });
		
		// MouseListener f�r confirm-Button
		confirm.addMouseListener ( new MouseAdapter () {
			@Override
			public void mouseReleased (MouseEvent e) {
				if (confirm.getSimplifiedBounds().contains(e.getPoint())) {
					if (posX_selectedField == -1|| posY_selectedField == -1) {
						warningMessage = "Kein Zielfeld ausgewählt";
						transparencyWarningMessage = 1f;
					} else {
						onLeavingScreen(AimSelectionScreen.this, GameScreen.SCREEN_INDEX);

						// deliver the attack message to the specified tile
						// assuming that the thread is done updating the values
						DeliverShootThread msg = new DeliverShootThread();
						msg.setDaemon(true);
						msg.start();

						transparencyWarningMessage = 1f;
					}
				}
			}
		});
		confirm.setRoundBorder(true);
		confirm.setVisible(true);
		confirm.acceptInput();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);

        // "Bestätigen" Button
        if (event.getKeyCode() == KeyEvent.VK_B) {
            // the code inside hier should be the same like confirm.mouseListener
            // if not make, make sure it is
            if (posX_selectedField == -1|| posY_selectedField == -1) {
                warningMessage = "Kein Zielfeld ausgewählt";
                transparencyWarningMessage = 1f;
            } else {
                onLeavingScreen(AimSelectionScreen.this, GameScreen.SCREEN_INDEX);

                // deliver the attack message to the specified tile
                // assuming that the thread is done updating the values
                DeliverShootThread msg = new DeliverShootThread();
                msg.setDaemon(true);
                msg.start();

                transparencyWarningMessage = 1f;
            }
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		// Send async message to compute new target position
		// and let the thread save the computation in the variables
		FieldSelectActor actor = new FieldSelectActor(e);
		actor.setDaemon(true);
		actor.start();
	}

	@Override
	public void draw (Graphics2D g) {
		// Background will be drawn
		super.draw(g);

        // Draw the world and the player
        GameScreen.getInstance().getMap().draw(g);
        GameScreen.getInstance().getAttackDrawer().draw(g);
		
		// draw the selectedField 
		if (posX_selectedField >= 0 && posY_selectedField >= 0) {
            TileLike selectedTile = (TileLike) (Main.getContext().getWorld().terrain().tileAt(posX_selectedField, posY_selectedField));
			g.setColor(new Color(255, 4, 3, 161));
			g.fill(selectedTile.getComponent().getBounds());
            g.setColor(new Color (255, 34, 0, 255));
            g.fill(selectedTile.getComponent().getBounds());
            animatedLine.updateOffset(- 0.5);
            animatedLine.draw(g);
		}

        // TODO: inizalizise and create TimeLifeBox
		// TimeLifeBox.draw(g);
        // TODO: create a new Field infoBox
		// Field.infoBox.draw(g);
		Main.getContext().getTimeClock().draw(g);
		
		confirm.draw(g);
		
		// Finally, draw the waringMessage
		g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
		g.setFont(new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 26));
		g.drawString(warningMessage, positionWarningMessage.x, positionWarningMessage.y);
		
		transparencyWarningMessage = transparencyWarningMessage - 0.013f;
		
		if (transparencyWarningMessage < 0) 
			transparencyWarningMessage = 0;
	}
	
	
	// ###############
	// GETTER & SETTER
	// ###############
	
	public int getPosX_selectedField() {
		return posX_selectedField;
	}

	private void setPosX_selectedField(int posX) {
		this.posX_selectedField = posX;
	}

	public int getPosY_selectedField() {
		return posY_selectedField;
	}

	private void setPosY_selectedField(int posY) {
		this.posY_selectedField = posY;
	}

	// #######
	// THREADS
	// #######

	/**
	 * Only performs the computation once, no need for an infinite loop.
	 * This thread is restarted every time when the screen receives
	 * a mouseReleased event.
	 */
	private class FieldSelectActor extends Thread {

		private MouseEvent evt;
		private boolean stopFlag = false;

		public FieldSelectActor(MouseEvent evt) {
			this.evt = evt;
		}

		@Override
		public void run() {
			super.run();
            if(!stopFlag) {
                for (TileLike tileWrapper : Main.getContext().getWorld().terrain().javaTiles()) {
                    if(!tileWrapper.getComponent().getBounds().contains(evt.getPoint()))
                        continue;

	                int tileX = tileWrapper.latticeX();
	                int tileY = tileWrapper.latticeY();

	                int playerX = Main.getContext().getActivePlayer().getGridX();
	                int playerY = Main.getContext().getActivePlayer().getGridY();
	                VisionStatus status = Main.getContext().getActivePlayer().visionMap().visionStatusOf(tileX, tileY);

	                if(playerX == tileWrapper.latticeX() && playerY == tileWrapper.latticeY()) {
		                warningMessage = "Selbstangriff nicht möglich!";
		                transparencyWarningMessage = 1f;
	                } else if(status == VisionStatus.Hidden) {
		                // Don't do anything, the player should not even notice that he did not
		                // reveal that tile yet. That's why it's called Fog of War...
		                return;
	                } else {
		                setPosX_selectedField(tileX);
		                setPosY_selectedField(tileY);
		                animatedLine.setEndX((int) tileWrapper.getComponent().getBounds().getBounds().getCenterX());
		                animatedLine.setEndY((int) tileWrapper.getComponent().getBounds().getBounds().getCenterY());
		                stopFlag = true;
	                }
                }
            }
		}
	}

	/**
	 * Sends a message to specified tile that it is being attacked.
	 * It retrieves the data from ArrowSelectionScreen.
	 */
	private class DeliverShootThread extends Thread {
		@Override
		public void run() {
			super.run();

			Player active = Main.getContext().activePlayer();
			TileLike target = (TileLike) active.world().terrain().tileAt(getPosX_selectedField(), getPosY_selectedField());

			final AbstractArrow arrow = ArrowHelper.instanceArrow(ArrowSelectionScreen.getInstance().getSelectedIndex());

			Option<Item> opt = active.inventory().remove(new AbstractFunction1<Item, Object>() {
				@Override
				public Object apply(Item v1) {
					return v1.getName().equals(arrow.getName());
				}
			});

			if(opt.isDefined()) {
                arrow.getAim().setGridX(getPosX_selectedField());
                arrow.getAim().setGridY(getPosY_selectedField());

                arrow.setGridX(active.getGridX());
                arrow.setGridY(active.getGridY());

                // the center of the player is the center of the arrow
                arrow.setPosX((int) (active.getComponent().getBounds().getBounds().getCenterX() - 0.5 * arrow.getImage().getWidth()));
                arrow.setPosY((int) (active.getComponent().getBounds().getBounds().getCenterY() - 0.5 * arrow.getImage().getHeight()));

                arrow.calculateRotation();

				target.take(new AttackEvent(arrow, (TileLike) active.tileLocation(), target, active, arrow.getSpeed()));
			} else {
                throw new RuntimeException("The selected arrow doesn't exit. He can't be shot");
			}
        }
	}
}

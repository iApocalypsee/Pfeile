package gui;

import entity.VisionState;
import general.Main;
import general.World;
import general.field.Field;
import comp.Button;
import comp.Component;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import player.weapon.AbstractArrow;
import player.weapon.AttackEvent;
import scala.Tuple2;
import scala.collection.mutable.HashMap;
import world.BaseTile;
import world.IBaseTile;
import world.IWorld;
import world.ScaleWorld;


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
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt die Variabelnwerte nach der Initialisierung; start den thread of <code> FieldSelector </code> */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		setPosX_selectedField(-1);
		setPosY_selectedField(-1);
		
		confirm = new Button (1178, 491, this, "Bestätigen");
		
		
		// MouseListener f�r confirm-Button
		confirm.addMouseListener ( new MouseAdapter () {
			@Override
			public void mouseReleased (MouseEvent e) {
				if (confirm.getSimplifiedBounds().contains(e.getPoint())) {
					if (posX_selectedField == -1|| posY_selectedField == -1) {
						warningMessage = "Kein Zielfeld ausgewählt";
						transparencyWarningMessage = 1f;
					} else {
						onLeavingScreen(AimSelectionScreen.this, NewWorldTestScreen$.MODULE$.SCREEN_INDEX);

						// deliver the attack message to the specified tile
						// assuming that the thread is done updating the values
						DeliverShootThread msg = new DeliverShootThread(getPosX_selectedField(), getPosY_selectedField());
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
	public void keyPressed(KeyEvent arg0) {
		super.keyPressed(arg0);
		NewWorldTestScreen.keyPressed(arg0);
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
		
		// include the world
		IWorld w = NewWorldTestScreen.getWorld();
		
		// The World will be drawn 
		w.draw(g);
		
		for (entity.Player player: w.getPlayers()) {
			player.draw(g);
		}
		
		// draw the selectedField 
		if (posX_selectedField >= 0 && posY_selectedField >= 0) {
			g.setColor(new Color (255, 227, 227, 250));
			g.drawPolygon(((BaseTile) (NewWorldTestScreen.getWorld().getTileAt(posX_selectedField, posY_selectedField))).getBounds());
			g.setColor(new Color (240, 37, 47, (int) (255 * 0.7)));
			g.fillPolygon(((BaseTile) (NewWorldTestScreen.getWorld().getTileAt(posX_selectedField, posY_selectedField))).getBounds());
		}
		
		// TODO: auf die neue World-Klasse ändern
		World.timeLifeBox.draw(g);
		Field.infoBox.draw(g);
		Main.timeObj.draw(g);
		GameScreen.getInstance().getWorld().getActivePlayer().drawLife(g);
		
		confirm.draw(g);
		
		// Finally, draw the waringMessage
		g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
		g.setFont(new Font(Component.STD_FONT.getFontName(), Font.BOLD, 26));
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
			IWorld w = NewWorldTestScreen.getWorld();
			HashMap<Tuple2<Object, Object>, VisionState> map = ((ScaleWorld) (w)).getActivePlayer().visionMap();
			for(int x = 0; x < w.getSizeX(); x++) {
				for(int y = 0; y < w.getSizeY(); y++) {
					if(!stopFlag) {
						BaseTile comp = (BaseTile) w.getTileAt(x, y);

						if(!comp.getBounds().contains(evt.getPoint())) continue;

						if(!map.apply(new Tuple2<Object, Object>(x, y)).equals(VisionState.Unrevealed)) {
							if (((ScaleWorld) w).getActivePlayer().getGridX() == x && ((ScaleWorld) w).getActivePlayer().getGridY() == y) {
								// TODO Make warning messages async (transparency included)
								warningMessage = "Selbstangriff ist nicht m�glich";
								transparencyWarningMessage = 1f;
							} else {
								setPosX_selectedField(x);
								setPosY_selectedField(y);
								stopFlag = true;
							}
						}

					}
				}
			}
		}
	}

	/**
	 * Sends a message to specified tile that it is being attacked.
	 * It retrieves the data from ArrowSelectionScreen and constructor params.
	 */
	private class DeliverShootThread extends Thread {

		private int x, y;

		public DeliverShootThread(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			super.run();
			BaseTile at = (BaseTile) NewWorldTestScreen.getWorld().getTileAt(x, y);
			ArrowSelectionScreen s = ArrowSelectionScreen.getInstance();
			Class<? extends AbstractArrow> a = s.getSelectedIndex();
			AttackEvent evt = null;
			try {
				evt = new AttackEvent(x, y, a.newInstance(), ((ScaleWorld) at.getWorld()).getActivePlayer());
			} catch (Exception e) {
				throw new ClassCastException("Casting of arrow failed.");
			}

			if(evt != null) {
				at.registerAttack(evt);
			}
		}
	}
	
	/** Thread for testing, if there was a click and at which field it has been set */
	@Deprecated
	private class FieldSelector implements Runnable {
		
		/** point, describing the last click */
		private Point lastSavedClickPosition = getLastClickPosition();
		
		
		// Finally: let's run the Thread 
		@Override
		public void run() {
			while (true) {
				
				// Let's start the testing loop
				while (getManager().getActiveScreenIndex() == AimSelectionScreen.SCREEN_INDEX) {
					
					// only run, if there was another click
					if (lastSavedClickPosition.x == getLastClickPosition().x && 
							lastSavedClickPosition.y == getLastClickPosition().y) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {e.printStackTrace();}
						
						continue;
					}
					
					// This is the world
					IWorld w = NewWorldTestScreen.getWorld();
					
					HashMap<Tuple2<Object, Object>, VisionState> map = ((ScaleWorld) (w)).getActivePlayer().visionMap();
					
					// Let's find the selectedField
					LOOPxPosition: for (int x = 0; x < w.getSizeX(); x++) {
						for (int y = 0; y < w.getSizeY(); y++) {
							// Is the click on the tile?
							BaseTile tile = (BaseTile) w.getTileAt(x, y);
							if (tile.getBounds().contains(getLastClickPosition())) {
								// Is the field visible?
								if (map.apply(new Tuple2 <Object, Object> (x,y)).equals(VisionState.Unrevealed) == false) {
									// is the player attacking the field of the himself
									if(((ScaleWorld) w).getActivePlayer().getGridX() == x && ((ScaleWorld) w).getActivePlayer().getGridY() == y) {
										warningMessage = "Selbstangriff ist nicht m�glich";
										transparencyWarningMessage = 1f;
									} else {
										setPosX_selectedField(x);
										setPosY_selectedField(y);
										lastSavedClickPosition = getLastClickPosition();
									}
								}
								
								break LOOPxPosition;
							}
						}
					} 
					
					// now, sleep a bit 
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
				
				// if 'isRunning == false': sleep longer
				try {
					Thread.sleep(480);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}
}

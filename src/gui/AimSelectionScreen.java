package gui;

import animation.AnimatedLine;
import comp.Button;
import general.Main;
import general.PfeileContext;
import newent.Player;
import newent.VisionMap;
import newent.VisionStatus;
import newent.event.AttackEvent;
import player.weapon.AbstractArrow;
import player.weapon.ArrowHelper;
import player.weapon.Item;
import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import world.TerrainLike;
import world.TileLike;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


public class AimSelectionScreen extends Screen {
	
	public static final String SCREEN_NAME = "AimSelection";
	
	public static final int SCREEN_INDEX = 4;

	/** X-Position des Ausgewählten Feldes
	 * * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1)*/
	private static volatile int posX_selectedField;
	
	/** Y-Position des Ausgewählten Feldes
	 * (wenn noch nie auf <code> AimSelectionScreen </code> gecklickt wurde, ist der Wert -1) */
	private static volatile int posY_selectedField;

    /** This contains and draws all tiles, which are affected by the damage radius, in different transparent
     * UNIFIED_COLOURS (of the selected arrow) */
    private FieldContainer fieldContainer;
	
	/** the button to confirm the decision to shoot an arrow at the selected field */
	private Button confirm;
	
	// These are only for the warning Message
	private String warningMessage = "";
	private float transparencyWarningMessage = 0;
	private Point positionWarningMessage = new Point (40, Main.getWindowHeight() - 105);

    /** the animated line from the player to the aim */
    private AnimatedLine animatedLine;

    private static final Color damageRadiusColor = new Color (255, 73, 15, 173);

    /** the bounds of the oval, which is showing the damage radius. The oval fits in the Rectangle. This is why I've
     * chosen a Rectangle. */
    private Rectangle boundsOvalDamageRadius;

    /** To draw the line of the damageRadius bigger than 1px, set the value of this basicStroke. Right now, it's 2.5f.*/
    private BasicStroke strokeOvalDamageRadius;

    /** this is the font of the warning message. It's saved here for speeding up the draw method. */
    private static final Font fontWarningMessage = new Font(comp.Component.STD_FONT.getFontName(), Font.BOLD, 26);
	
	/** Konstrucktor von AimSelectionScreen: ruft super(...) auf und setzt die Variabelnwerte nach der Initialisierung; start den thread of <code> FieldSelector </code> */
	public AimSelectionScreen() {
		super (SCREEN_NAME, SCREEN_INDEX);
		
		posX_selectedField = -1;
		posY_selectedField = -1;
		
		confirm = new Button ((int) (0.86 * Main.getWindowWidth()), (int) (0.36 * Main.getWindowHeight()), this, "Bestätigen");

        animatedLine = new AnimatedLine(0,0,0,0,Color.RED);
        animatedLine.setWidth(3.0f);

        boundsOvalDamageRadius = new Rectangle (0, 0, 0, 0);
        strokeOvalDamageRadius = new BasicStroke(2.5f);

        fieldContainer = new FieldContainer();

        onScreenEnter.register(new AbstractFunction0<BoxedUnit>() {
            @Override
            public BoxedUnit apply () {
                final AbstractArrow arrow = ArrowHelper.instanceArrow(ArrowSelectionScreen.getInstance().getSelectedIndex());

                posX_selectedField = -1;
                posY_selectedField = -1;

                animatedLine.setStartX((int) Main.getContext().getActivePlayer().getComponent().getBounds().getBounds().getCenterX());
                animatedLine.setStartY((int) Main.getContext().getActivePlayer().getComponent().getBounds().getBounds().getCenterY());
                animatedLine.setColor(ArrowHelper.getUnifiedColor(ArrowSelectionScreen.getInstance().getSelectedIndex()));
                transparencyWarningMessage = 0.0f;

                // a new Rectangle for a new arrow, because of different damageRadius
                boundsOvalDamageRadius = new Rectangle(boundsOvalDamageRadius.x, boundsOvalDamageRadius.y,
                        (int) arrow.getAim().getDamageRadiusGUIWidth(), (int) arrow.getAim().getDamageRadiusGUIHeight());
                return BoxedUnit.UNIT;
            }
        });
		
		// MouseListener f�r confirm-Button
		confirm.addMouseListener ( new MouseAdapter () {
			@Override
			public void mouseReleased (MouseEvent e) {
				triggerConfirmButton();
			}
		});
		confirm.setRoundBorder(true);
		confirm.setVisible(true);
		confirm.acceptInput();
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		super.keyPressed(event);

        // "Bestätigen" Button --> confirmButton
        if (event.getKeyCode() == KeyEvent.VK_B) {
            triggerConfirmButton();
        }
	}

    private void triggerConfirmButton () {
        if (posX_selectedField == -1|| posY_selectedField == -1) {
            warningMessage = "Kein Zielfeld ausgewählt";
            transparencyWarningMessage = 1f;
        } else {
            // deliver the attack message to the specified tile
            // assuming that the thread is done updating the values
            DeliverShootThread msg = new DeliverShootThread();
            msg.setDaemon(true);
            msg.setPriority(4);
            msg.start();

            transparencyWarningMessage = 1f;

            onLeavingScreen(AimSelectionScreen.this, ArrowSelectionScreen.SCREEN_INDEX);
        }
    }

	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		// Send async message to compute new target position
		// and let the thread save the computation in the variables
		FieldSelectActor actor = new FieldSelectActor(e);
		actor.setDaemon(true);
        actor.setPriority(8);
		actor.start();
	}
	
	
	// ###############
	// GETTER & SETTER
	// ###############

    // empty for now

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

	                if(status == VisionStatus.Hidden) {
		                // Don't do anything, the player should not even notice that he did not
		                // reveal that tile yet. That's why it's called Fog of War...
		                return;
	                } if(playerX == tileWrapper.latticeX() && playerY == tileWrapper.latticeY()) {
                        warningMessage = "Selbstangriff nicht möglich!";
                        transparencyWarningMessage = 1f;
                    } else {
		                posX_selectedField = tileX;
		                posY_selectedField = tileY;

                        Rectangle bounds = tileWrapper.getComponent().getBounds().getBounds();

		                animatedLine.setEndX((int) bounds.getCenterX());
		                animatedLine.setEndY((int) bounds.getCenterY());

		                boundsOvalDamageRadius.setLocation((int) (bounds.getCenterX() - boundsOvalDamageRadius.getWidth() / 2), (int) (bounds.getCenterY() - boundsOvalDamageRadius.getHeight() / 2));

                        fieldContainer.updateFields(tileWrapper);

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
			TileLike target = (TileLike) active.world().terrain().tileAt(posX_selectedField, posY_selectedField);

			final AbstractArrow arrow = ArrowHelper.instanceArrow(ArrowSelectionScreen.getInstance().getSelectedIndex());

			Option<Item> opt = active.inventory().remove(new AbstractFunction1<Item, Object>() {
				@Override
				public Object apply(Item v1) {
					return v1.getName().equals(arrow.getName());
				}
			});

			if(opt.isDefined()) {
                arrow.getAim().setGridX(posX_selectedField);
                arrow.getAim().setGridY(posY_selectedField);

                arrow.setGridX(active.getGridX());
                arrow.setGridY(active.getGridY());

                // the center of the player is the center of the arrow
                arrow.getComponent().setX((int) (active.getComponent().getPreciseRectangle().getCenterX() - 0.5 * arrow.getImage().getWidth()));
                arrow.getComponent().setY((int) (active.getComponent().getPreciseRectangle().getCenterY() - 0.5 * arrow.getImage().getHeight()));

                arrow.calculateRotation();

				target.take(new AttackEvent(arrow, (TileLike) active.tileLocation(), target, active, arrow.getSpeed()));

                ArrowSelectionScreen.getInstance().updateInventoryList();
			} else {
                throw new RuntimeException("The selected arrow doesn't exit. He can't be shot");
			}
        }
	}

    /** The instance if this class contains all {@link gui.AimSelectionScreen.FieldContainer.ContainedObject} in an ArrayList.
     * They are used to draw the damageRadius and the selectedTile. Use updateFields(TileLike selectedTile) to reset the damageRadius.
     * Use the draw-method of FieldContainer to draw the damageRadius as well as the selectedTile. */
    private class FieldContainer implements Drawable {

        /** The list of all tile-shapes and their colors, that need to be drawn. */
        private final List<ContainedObject> containedObjects;

        /** the Color of the border of the selectedField */
        private final Color selectedTileOutLineColor = new Color(255, 73, 15, 173);

        /** this allows to draw a line with the color {@link gui.AimSelectionScreen.FieldContainer#selectedTileOutLineColor}
         * and with the width <code>strokeOfSelectedTile.getLineWidth()</code> around the bounds of the selectedTile. */
        private final BasicStroke strokeOfSelectedTile = new BasicStroke(2f);

        /** the extended bounds of the selected field. It's 2px bigger then the normal one, to be able to draw an border */
        private Shape boundsSelectedTileExtended;

        FieldContainer () {
            containedObjects = new ArrayList<>();
            boundsSelectedTileExtended = new Polygon();
        }

        /** This updates the damageRadius triggered by {@link gui.AimSelectionScreen.FieldSelectActor}.
         *  The specified TileLike tileWrapper is the selectedTile. This method is quite performance intensive, so
         *  use it in an thread. The methods are already synchronized (over the field "containedObjects" from type List<ContainedObject>.*/
        synchronized void updateFields (TileLike tileWrapper) {
            //Rectangle bounds = tileWrapper.component().getBounds().getBounds();

            //boundsSelectedTileExtended = new Polygon();
            //boundsSelectedTileExtended.addPoint(bounds.x - 1, bounds.y + IsometricPolygonTile.TileHalfHeight());
            //boundsSelectedTileExtended.addPoint(bounds.x + IsometricPolygonTile.TileHalfWidth(), bounds.y - 1);
            //boundsSelectedTileExtended.addPoint(bounds.x + IsometricPolygonTile.TileWidth() + 1, bounds.y + IsometricPolygonTile.TileHalfHeight());
            //boundsSelectedTileExtended.addPoint(bounds.x + IsometricPolygonTile.TileHalfWidth(), bounds.y + IsometricPolygonTile.TileHeight() + 1);

            // the outer bounds of the selectedTile
            boundsSelectedTileExtended = tileWrapper.getComponent().getBounds();

            //  preparing... getting all values for later calculations in order to access these values faster and to be better clearly structured.
            final AbstractArrow arrow = ArrowHelper.instanceArrow(ArrowSelectionScreen.getInstance().getSelectedIndex());
            arrow.getAim().setGridX(posX_selectedField);
            arrow.getAim().setGridY(posY_selectedField);

            final Color unifiedArrowColor = ArrowHelper.getUnifiedColor(arrow.getName());
            final TerrainLike terrain = Main.getContext().world().terrain();
            final VisionMap visibleMap = Main.getContext().getActivePlayer().visionMap();

            // controls all tiles and if they are visible and the arrow would make damage, a new ContainedObject is added to the list to be drawn.
            // The list has to be cleared at the beginning.
            synchronized (containedObjects) {
                containedObjects.clear();

                for (int x = 0; x < PfeileContext.WORLD_SIZE_X().get(); x++) {
                    for (int y = 0; y < PfeileContext.WORLD_SIZE_Y().get(); y++) {
                        if (visibleMap.visionStatusOf(x, y) == VisionStatus.Hidden)
                            continue;
                        if (arrow.damageAt(x, y) != 0)
                            containedObjects.add(new ContainedObject((TileLike) terrain.tileAt(x, y), arrow, unifiedArrowColor));
                    }
                }
            }
        }

        /** The private class ContainedObject holds information of the bound and of the color
         * (unifiedArrowColor with alpha-values from the percentage of damage). All ContainedObject are saved in an ArrayList
         * at {@link gui.AimSelectionScreen.FieldContainer}, which also calls the draw-method for each ContainedObject. */
        private class ContainedObject implements Drawable {
            private volatile Color impactingColor;
            private volatile Shape bounds;

            private ContainedObject (TileLike tile, AbstractArrow arrow, Color unifiedArrowColor) {
                bounds = tile.getComponent().getBounds();

                // the color is unifiedArrowColor with the alpha value: damage [relative to the maximum damage]
                impactingColor = new Color(unifiedArrowColor.getRed() / 255f, unifiedArrowColor.getGreen() / 255f, unifiedArrowColor.getBlue() / 255f,
                        (float) (arrow.damageAt(tile.latticeX(), tile.latticeY()) / (arrow.getAttackValue() * PfeileContext.DAMAGE_MULTI().get())));
            }

            @Override
            public void draw (Graphics2D g) {
                g.setColor(impactingColor);
                g.fill(bounds);
            }
        }


        @Override
        public void draw (Graphics2D g) {
            synchronized (containedObjects) {
                for (ContainedObject tileDrawer : containedObjects)
                    tileDrawer.draw(g);
            }

            g.setColor(selectedTileOutLineColor);
            g.setStroke(strokeOfSelectedTile);
            g.draw(boundsSelectedTileExtended);
        }
    }


    @Override
    public void draw (Graphics2D g) {
        // Background will be drawn
        super.draw(g);

        // Draw the world and the player
        GameScreen.getInstance().getMap().draw(g);

        // draw the selected field and the damage radius
        if (posX_selectedField >= 0 && posY_selectedField >= 0) {
            fieldContainer.draw(g);

            // drawing the damage radius twice, that the line is thicker
            g.setColor(damageRadiusColor);
            g.setStroke(strokeOvalDamageRadius);
            g.drawOval(boundsOvalDamageRadius.x, boundsOvalDamageRadius.y, boundsOvalDamageRadius.width, boundsOvalDamageRadius.height);

            animatedLine.updateOffset(- 0.5);
            animatedLine.draw(g);
        } else {
            // when you've selected an arrow you don't need to see all the others
            GameScreen.getInstance().getAttackDrawer().draw(g);
        }

        // TODO: inizalizise and create TimeLifeBox
        // TimeLifeBox.draw(g);
        // TODO: create a new Field infoBox
        // Field.infoBox.draw(g);
        Main.getContext().getTimeClock().draw(g);

        confirm.draw(g);

        // Finally, draw the waringMessage
        if (transparencyWarningMessage > 0) {
            g.setColor(new Color(1f, 0f, 0f, transparencyWarningMessage));
            g.setFont(fontWarningMessage);
            g.drawString(warningMessage, positionWarningMessage.x, positionWarningMessage.y);

            transparencyWarningMessage = transparencyWarningMessage - 0.013f;
            if (transparencyWarningMessage < 0)
                transparencyWarningMessage = 0;
        }
    }
}

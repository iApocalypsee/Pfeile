package gui.screen;

import comp.Component;
import comp.Component.ComponentStatus;
import comp.ImageLike;
import comp.SolidColor;
import general.Delegate;
import general.Function0Delegate;
import general.Main;
import geom.functions.FunctionCollection;
import gui.Drawable;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Hauptklasse für Screens.
 *
 * <p>24.2.2014</p>
 * <ul>
 *     <li>Screens können jetzt Components selbst zeichnen und deren Zeichenreihenfolge bestimmen.
 *     Diese Funktion ist aber noch nicht getestet worden!</li>
 * </ul>
 * 
 * 
 * @author Josip
 * @version 24.2.2014
 * 
 */
public abstract class Screen implements Drawable, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {

	private static Point mousePosition = new Point(0, 0);

	/**
	 * The last position of a click.
	 */
	private static Point lastClickPosition = new Point(0, 0);

	protected String name;
	protected ScreenManager manager = Main.getGameWindow().getScreenManager();

    public final Function0Delegate onScreenEnter = new Function0Delegate();
    public final Delegate<ScreenChangedEvent> onScreenLeft = new Delegate<>();

	private List<Component> components = new CopyOnWriteArrayList<>();
	public final int SCREEN_INDEX;
	
	/**
	 * Whether the screen draws its components by itself or lets the user control the drawing.
	 */
	private boolean preprocessedDrawingEnabled = false;

	private ImageLike background = new SolidColor(Color.black);

    private boolean boundsDrawEnabled = false;
    private Stroke boundsDrawStroke = new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4, new float[]{4}, 0);
    private Font boundsDrawFont = new Font(Font.MONOSPACED, Font.PLAIN, 10);
    private final Color boundsDrawWhite = new Color(255, 255, 255, 155),
            boundsDrawRed = new Color(255, 0, 0, 155),
            boundsDrawOrange = new Color(255, 200, 0, 155),
            boundsDrawFillString = new Color(0, 100, 155, 255);

    private Robot robot;

	protected static boolean isLeftMousePressed;
	protected static boolean isRightMousePressed;

	/**
	 * Erstellt eine neue Screen mit angegebenen Parametern.
	 * @param n Name des Screens
	 * @param i Der Index des Screens
	 */
	public Screen(String n, int i) {
		SCREEN_INDEX = i;
		name = n;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            throw new RuntimeException("AWT robot could not be constructed.");
        }
        manager.getScreens().put(SCREEN_INDEX, this);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the manager
	 */
	public ScreenManager getManager() {
		return manager;
	}

	/**
	 * @param manager
	 *            the manager to set
	 */
	void setManager(ScreenManager manager) {
		this.manager = manager;
	}

	/**
	 * @return the mousePosition
	 */
	public static Point getMousePosition() {
		return mousePosition;
	}

	/**
	 * Returns the last position of a click (using MouseReleased) on the screen.
	 * @return The last click position.
	 */
	public static Point getLastClickPosition() {
		return lastClickPosition;
	}

	public static boolean isLeftMousePressed() {
		return isLeftMousePressed;
	}

	protected static void setLeftMousePressed(boolean isMousePressed) {
		Screen.isLeftMousePressed = isMousePressed;
	}
	
	public static boolean isRightMousePressed() {
		return isRightMousePressed;
	}
	
	protected static void setRightMousePressed(boolean isMousePressed) {
		Screen.isRightMousePressed = isMousePressed;
	}

	public ImageLike getBackground() {
		return background;
	}

	public void setBackground(ImageLike background) {
		if(background == null) throw new NullPointerException();
		this.background = background;
	}

	/**
	 * Returns a <b>copy</b> of the component list. This copied list is not to be used
	 * for adding components. This should be done with the method ???
	 * @return A copy of all components that screen currently holds. Notice that the
	 * components won't ge updated.
	 */
	public synchronized List<Component> getComponents() {
		return components;
	}

    public void drawChecked(Graphics2D g) {
        draw(g);
        drawBounds(g);
    }

	/**
	 * Zeichnet den Screen mit dem Graphics2D Objekt.
	 * @param g Der Grafikkontext
	 */
	public void draw(Graphics2D g) {
		//g.setColor(Color.black);
		//g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		//background.drawImage(g, 0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		if(preprocessedDrawingEnabled) {
			getComponents().forEach(c -> c.drawChecked(g));
		}
	}

    private void drawBounds(Graphics2D g) {
        if(boundsDrawEnabled) {
            g.setStroke(boundsDrawStroke);
            final Font previousFont = g.getFont();
            g.setFont(boundsDrawFont);
            final Component[] mouseFocused = {null};
            getComponents().forEach(component -> {
                if (component.isVisible()) g.setColor(boundsDrawWhite);
                else g.setColor(boundsDrawRed);
                if (component.isMouseFocused()) g.setColor(boundsDrawOrange);
                g.draw(component.getBounds());
                if (component.isMouseFocused()) {
                    mouseFocused[0] = component;
                }
                final Point center = component.center();
                g.drawString(component.getName(), center.x, center.y);
            });

            if(mouseFocused[0] != null) {
                final Point center = mouseFocused[0].center();
                final Dimension textBounds = Component.getTextBounds(mouseFocused[0].getName(), boundsDrawFont);
                final Color prevColor = g.getColor();
                g.setColor(boundsDrawFillString);
                g.fillRect(center.x, center.y - textBounds.height, textBounds.width, textBounds.height);
                g.setColor(prevColor);
                g.drawString(mouseFocused[0].getName(), center.x, center.y);
            }
            g.setFont(previousFont);
        }
    }

    /**
	 * Wird aufgerufen, wenn der Screen verlassen wird. Sollte man selbst aufrufen, wenn man 
	 * den Screen verlassen will und zu einem anderen wechseln will. Der Index des Zielscreens
	 * sollte in Screen.{@link #SCREEN_INDEX} gespeichert sein.
     * 
	 * @param toScreen Der Index des zu betretenden Screens
	 */
	public void onLeavingScreen(int toScreen) {
        manager.requestScreenChange(toScreen);
	}

	public void mousePressed(MouseEvent e) {
		mousePosition = e.getPoint();
		if(e.getButton() == 1) {
			isLeftMousePressed = true;
		}
		if(e.getButton() == 3) {
			isRightMousePressed = true;
		}
		for(int i = components.size() - 1; i >= 0; i--) {
			Component c = components.get(i);
            if(c.isVisible()) {
                if(c.isAcceptingInput()) {
	                if(c.getPreciseRectangle().contains(e.getPoint())) {
		                if (c.getBounds().contains(e.getPoint())) {
			                for (MouseListener m : c.getMouseListeners()) {
				                m.mousePressed(e);
			                }
			                break;
		                } else {
			                if (c.getStatus() != ComponentStatus.NO_MOUSE) {
				                c.setStatus(ComponentStatus.NO_MOUSE);
			                }
		                }
	                } else {
		                if(c.getStatus() != ComponentStatus.NO_MOUSE) {
			                c.setStatus(ComponentStatus.NO_MOUSE);
		                }
	                }
                } else {
                    if(c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
                        c.setStatus(ComponentStatus.NOT_AVAILABLE);
                    }
                }
            }
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1) {
			isLeftMousePressed = false;
		}
		if(e.getButton() == 3) {
			isRightMousePressed = false;
		}

		lastClickPosition = e.getPoint();

		for(int i = components.size() - 1; i >= 0; i--) {
			Component c = components.get(i);
			if (c.isAcceptingInput() && c.isVisible()) {
				if(c.getPreciseRectangle().contains(e.getPoint())) {
					if (c.getBounds().contains(e.getPoint())) {
						for (MouseListener m : c.getMouseListeners()) {
							m.mouseReleased(e);
						}
						// A component already received the release event, no other component
						// should receive this event anymore.
						if(!c.isListenerTransparent()) break;
					} else {
						if (c.getStatus() != ComponentStatus.NO_MOUSE) {
							c.setStatus(ComponentStatus.NO_MOUSE);
						}
					}
				} else {
					if(c.getStatus() != ComponentStatus.NO_MOUSE) {
						c.setStatus(ComponentStatus.NO_MOUSE);
					}
				}
			} else {
				if (c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
					c.setStatus(ComponentStatus.NOT_AVAILABLE);
				}
			}
		}
	}
	
	@Override
	public final void mouseClicked(MouseEvent e) {
		// As of 20.1.2014 mouseClicked cannot be used and shall not be used anymore
	}
	
	@Override
	public final void mouseEntered(MouseEvent e) {
		// do nothing
	}
	
	@Override
	public final void mouseExited(MouseEvent e) {
		// do nothing
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition = e.getPoint();

		Component focusedComponent = null;

		for(int i = components.size() - 1; i >= 0; i--) {
			Component c = components.get(i);
			if(c.isAcceptingInput() && c.isVisible()) {
				if(c.getPreciseRectangle().contains(e.getPoint()) && focusedComponent == null) {
					if(c.getBounds().contains(e.getPoint())) {
						if (c.getStatus() != ComponentStatus.CLICK) {
							for (MouseListener m : c.getMouseListeners()) {
								m.mouseEntered(e);
							}
                            c.setStatus(ComponentStatus.CLICK);
						}
						for (MouseMotionListener m : c.getMouseMotionListeners()) {
							m.mouseDragged(e);
						}
						focusedComponent = c;
					} else {
						if(c.getStatus() != ComponentStatus.NO_MOUSE) {
							for (MouseListener m : c.getMouseListeners()) {
								m.mouseExited(e);
							}
						}
					}
				} else {
					if(c.getStatus() != ComponentStatus.NO_MOUSE) {
						for (MouseListener m : c.getMouseListeners()) {
							m.mouseExited(e);
						}
					}
				}
			} else {
				if(c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
					c.setStatus(ComponentStatus.NOT_AVAILABLE);
				}
			}
		}
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint();

		Component focusedComponent = null;

		// Traverse in reverse; last components in list are drawn on top of the first components in list
		for(int i = components.size() - 1; i >= 0; i--) {
			Component c = components.get(i);
			if(c.isAcceptingInput() && c.isVisible()) {
				if(c.getPreciseRectangle().contains(e.getPoint()) && focusedComponent == null) {
					if(c.getBounds().contains(e.getPoint())) {
						if (c.getStatus() == ComponentStatus.NO_MOUSE) {
							for (MouseListener m : c.getMouseListeners()) {
								m.mouseEntered(e);
							}
						}
						for (MouseMotionListener m : c.getMouseMotionListeners()) {
							m.mouseMoved(e);
						}
						focusedComponent = c;
					} else {
						if(c.getStatus() != ComponentStatus.NO_MOUSE) {
							for(MouseListener m : c.getMouseListeners()) {
								m.mouseExited(e);
							}
						}
					}
				} else {
					if(c.getStatus() != ComponentStatus.NO_MOUSE) {
						for (MouseListener m : c.getMouseListeners()) {
							m.mouseExited(e);
						}
					}
				}
			} else {
				if(c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
					c.setStatus(ComponentStatus.NOT_AVAILABLE);
				}
			}
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		for (Component c : components) {
			if(c.isAcceptingInput()) {
				if(c.getBounds().contains(e.getPoint())) {
					for (MouseWheelListener m : c.getMouseWheelListeners()) {
						m.mouseWheelMoved(e);
					}
				}
			}
		}
	}
	
	public void keyDown(KeyEvent e) {
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	public void add(Component c) {
	    add(components.size(), c);
	}

	public void add(int index, Component c) {
		components.add(index, c);
	}
	
	/**
	 * Entfernt ein Steuerelement aus der Auflistung.
	 * @param c Das Steuerelement, das entfernt werden soll.
	 */
	public final void remove(Component c) {
		if(components.contains(c)) {
            c.unparent();
			components.remove(c);
		}
	}

	/**
	 * Returns the layer number of the specified component.
	 * @param c The component.
	 * @return The layer number of the component.
	 */
	public int getLayerNumber(Component c) {
		return components.indexOf(c);
	}

	/**
	 * Pushes the specified component one layer into the background.
	 * @param component The component to push into the background.
	 */
	public void pushBack(Component component) {
		if(!components.contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.pushBack(Component)");
			return;
		}

		int oldIndex = components.indexOf(component);
		components.remove(component);
		components.add(oldIndex - 1, component);
	}

	/**
	 * Forces the specified component into the last layer.
	 * @param component The component to push into the background.
	 */
	public void forcePushBack(Component component) {
		if(!components.contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.forcePushBack(Component)");
			return;
		}

		components.remove(component);
		components.add(0, component);
	}

	/**
	 * Pulls the specified component one layer into the foreground.
	 * @param component The component to pull into the foreground.
	 */
	public void pullFront(Component component) {
		if(!components.contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.pullFront(Component)");
			return;
		}

		int oldIndex = components.indexOf(component);
		components.remove(component);
		components.add(oldIndex + 1, component);
	}

	/**
	 * Forces the specified component into the foreground
	 * @param component The component to pull into the foreground.
	 */
	public void forcePullFront(Component component) {
		if(!components.contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.forcePullFront(Component)");
			return;
		}

		components.remove(component);
		components.add(component);
	}

	public Screen putBefore(Component beforeWhat, Component move) {
		if(components.contains(move)) components.remove(move);
		final int beforeIndex = FunctionCollection.clamp(components.indexOf(beforeWhat), 0, components.size());
		components.add(beforeIndex, move);
		return this;
	}

	public Screen putAfter(Component afterWhat, Component move) {
		if(components.contains(move)) components.remove(move);
		components.add(components.indexOf(afterWhat) + 1, move);
		return this;
	}
	
	protected boolean isPreprocessedDrawingEnabled() {
		return preprocessedDrawingEnabled;
	}
	
	protected void setPreprocessedDrawingEnabled(boolean preprocessedDrawingEnabled) {
		this.preprocessedDrawingEnabled = preprocessedDrawingEnabled;
	}

    public boolean isBoundsDrawEnabled()
    {
        return boundsDrawEnabled;
    }

    public void setBoundsDrawEnabled(boolean boundsDrawEnabled)
    {
        this.boundsDrawEnabled = boundsDrawEnabled;
    }

    public static final class ScreenChangedEvent {
        public final int toIndex;
        public ScreenChangedEvent(int toIndex) {
            this.toIndex = toIndex;
        }

	    @Override
	    public String toString() {
		    return "gui.screen.Screen.ScreenChangedEvent{" +
				    "toIndex=" + toIndex +
				    '}';
	    }
    }
}

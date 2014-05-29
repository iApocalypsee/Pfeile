package gui;

import general.Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import comp.Component;
import comp.Component.ComponentStatus;

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

	/**
	 * Die Mausposition des Zeigers.
	 */
	private static Point mousePosition = new Point(0, 0);

	/**
	 * The last position of a click.
	 */
	private static Point lastClickPosition = new Point(0, 0);

	/**
	 * Der Name des Screens.
	 */
	protected String name;
	protected ScreenManager manager = Main.getGameWindow().getScreenManager();
	
	/**
	 * Die Components, die der Screen hält.
	 */
	private LinkedList<Component> components = new LinkedList<Component>();
	public final int SCREEN_INDEX;
	
	/**
	 * Sagt aus, ob Components automatisch vom Screen gezeichnet werden oder nicht.
	 * Auf <code>false</code> lassen, die Funktion ist buggy!
	 */
	private boolean preprocessedDrawingEnabled = false;

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
	public static synchronized Point getLastClickPosition() {
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

	/**
	 * Returns a <b>copy</b> of the component list. This copied list is not to be used
	 * for adding components. This should be done with the method {@link Screen#add(com.github.pfeile.comp.Component)}
	 * @return A copy of all components that screen currently holds. Notice that the
	 * components won't ge updated.
	 */
	@SuppressWarnings("unchecked")
	synchronized LinkedList<Component> getComponents() {
		return (LinkedList<Component>) components.clone();
	}

	/**
	 * Zeichnet den Screen mit dem Graphics2D Objekt.
	 * @param g Der Grafikkontext
	 */
	public void draw(Graphics2D g) {
		// clear screen
		g.setColor(Color.black);
		g.fillRect(0, 0, Main.getWindowWidth(), Main.getWindowHeight());
		
		if(preprocessedDrawingEnabled) {
			for (Component c : getComponents()) {
				if(c != null) {
					if(c.isVisible()) {
	                    if(c.getBounds().intersects(0, 0, Main.getWindowWidth(), Main.getWindowHeight())) {
	                        c.draw(g);
	                    }
					}
				}
			}
		}
	}

	/**
	 * Wird aufgerufen, wenn der Screen betreten wird.
	 */
	public void onEnteringScreen(Object sender) {

	}

	/**
	 * Wird aufgerufen, wenn der Screen verlassen wird. Sollte man selbst aufrufen, wenn man 
	 * den Screen verlassen will und zu einem anderen wechseln will. Der Index des Zielscreens
	 * sollte in Screen.{@link #SCREEN_INDEX} gespeichert sein.
	 * 
	 * @param sender Das Objekt, das die Aktion initiiert hat
	 * @param toScreen Der Index des zu betretenden Screens
	 */
	public void onLeavingScreen(Object sender, int toScreen) {
		manager.setActiveScreen(toScreen);
		manager.setLastScreenChange(new Date());
	}
	
	
	

	public void mousePressed(MouseEvent e) {
		mousePosition = e.getPoint();
		if(e.getButton() == 1) {
			isLeftMousePressed = true;
		}
		if(e.getButton() == 3) {
			isRightMousePressed = true;
		}
		for (Component c : getComponents()) {
            if(c.isVisible()) {
                if(c.isAcceptingInput()) {
                    if(c.getBounds().contains(e.getPoint())) {
                        for (MouseListener m : c.getMouseListeners()) {
                            // hier ist eigentlicher Aufruf des Listeners
                            m.mousePressed(e);
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

		Iterator<Component> iterator = getComponents().iterator();
		while(iterator.hasNext()) {
			Component c = iterator.next();
			if (c.isAcceptingInput()) {
				if (c.getBounds().contains(e.getPoint())) {
					for (MouseListener m : c.getMouseListeners()) {
						// hier ist eigentlicher Aufruf des Listeners
						m.mouseReleased(e);
					}
				} else {
					if (c.getStatus() != ComponentStatus.NO_MOUSE) {
						c.setStatus(ComponentStatus.NO_MOUSE);
					}
				}
			} else {
				if (c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
					c.setStatus(ComponentStatus.NOT_AVAILABLE);
				}
			}
		}
		/*
		for (Component c : components) {
			if (c.isAcceptingInput()) {
				if (c.getBounds().contains(e.getPoint())) {
					for (MouseListener m : c.getMouseListeners()) {
						// hier ist eigentlicher Aufruf des Listeners
						m.mouseReleased(e);
					}
				} else {
					if (c.getStatus() != ComponentStatus.NO_MOUSE) {
						c.setStatus(ComponentStatus.NO_MOUSE);
					}
				}
			} else {
				if (c.getStatus() != ComponentStatus.NOT_AVAILABLE) {
					c.setStatus(ComponentStatus.NOT_AVAILABLE);
				}
			}
		}
		*/
		
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
		if(!isLeftMousePressed) {
			isLeftMousePressed = true;
		}
		for (Component c : getComponents()) {
			if(c.isAcceptingInput()) {
				if(c.getBounds().contains(e.getPoint())) {
					if(c.getStatus() != ComponentStatus.MOUSE) {
						for (MouseListener m : c.getMouseListeners()) {
							m.mouseEntered(e);
						}
					}
					for (MouseMotionListener m : c.getMouseMotionListeners()) {
						m.mouseDragged(e);
					}
				} else {
					if(c.getStatus() != ComponentStatus.NO_MOUSE) {
						c.setStatus(ComponentStatus.NO_MOUSE);
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
		for (Component c : getComponents()) {
			if(c.isAcceptingInput()) {
				if(c.getBounds().contains(e.getPoint())) {
					if(c.getStatus() == ComponentStatus.NO_MOUSE) {
						for (MouseListener m : c.getMouseListeners()) {
							m.mouseEntered(e);
						}
					}
					for (MouseMotionListener m : c.getMouseMotionListeners()) {
						m.mouseMoved(e);
					}
				} else {
					if(c.getStatus() != ComponentStatus.NO_MOUSE) {
						c.setStatus(ComponentStatus.NO_MOUSE);
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
		for (Component c : getComponents()) {
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
	
	/**
	 * Fügt ein Steuerelement der Auflistung hinzu.
	 * @param c Das Steuerelement, das hinzugefügt werden soll.
	 */
	public final void add(Component c) {
		components.add(c);
	}
	
	/**
	 * Entfernt ein Steuerelement aus der Auflistung.
	 * @param c Das Steuerelement, das entfernt werden soll.
	 */
	public final void remove(Component c) {
		if(components.contains(c)) {
			components.remove(c);
		}
	}

	/**
	 * Returns the layer number of the specified component.
	 * @param c The component.
	 * @return The layer number of the component.
	 */
	public int getLayerNumber(Component c) {
		return getComponents().indexOf(c);
	}

	/**
	 * Pushes the specified component one layer into the background.
	 * @param component The component to push into the background.
	 */
	public void pushBack(Component component) {
		if(!getComponents().contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.pushBack(Component)");
			return;
		}

		int oldIndex = getComponents().indexOf(component);
		getComponents().remove(component);
		getComponents().add(oldIndex - 1, component);
	}

	/**
	 * Forces the specified component into the last layer.
	 * @param component The component to push into the background.
	 */
	public void forcePushBack(Component component) {
		if(!getComponents().contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.forcePushBack(Component)");
			return;
		}

		getComponents().remove(component);
		getComponents().add(0, component);
	}

	/**
	 * Pulls the specified component one layer into the foreground.
	 * @param component The component to pull into the foreground.
	 */
	public void pullFront(Component component) {
		if(!getComponents().contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.pullFront(Component)");
			return;
		}

		int oldIndex = getComponents().indexOf(component);
		getComponents().remove(component);
		getComponents().add(oldIndex + 1, component);
	}

	/**
	 * Forces the specified component into the foreground
	 * @param component The component to pull into the foreground.
	 */
	public void forcePullFront(Component component) {
		if(!getComponents().contains(component)) {
			System.out.println("Component " + component.getName() + " could not be found.");
			System.out.println("Message from Screen.forcePullFront(Component)");
			return;
		}

		getComponents().remove(component);
		getComponents().add(component);
	}
	
	public boolean isPreprocessedDrawingEnabled() {
		return preprocessedDrawingEnabled;
	}
	
	public void setPreprocessedDrawingEnabled(boolean preprocessedDrawingEnabled) {
		this.preprocessedDrawingEnabled = preprocessedDrawingEnabled;
	}
}

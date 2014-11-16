package comp;

import gui.Screen;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * A standard implementation of a component.
 */
public abstract class Component implements IComponent {

	/**
	 * Zeigt den Status an, in welchem das Steuerelement sich befindet.
	 *
	 * @version 2.10.2013
	 */
	public enum ComponentStatus {
		NO_MOUSE, MOUSE, CLICK, NOT_AVAILABLE
	}

	private ComponentStatus status = ComponentStatus.NO_MOUSE;

	private Screen backingScreen;

	/**
	 * Das umfassende Polygon um die Komponente.
	 */
	private Shape bounds = new Polygon();

	/**
	 * Der Name des Steuerelements. Wird hauptsächlich für {@link Component#children} benötigt.
	 */
	private String name;

	/**
	 * Zeigt an, ob die Component willig ist, Input zu akzeptieren.
	 * Standardmäßig auf true gesetzt.
	 *
	 * @see Component#acceptInput()
	 * @see Component#declineInput()
	 */
	private boolean acceptingInput = true;

	/**
	 * Zeigt an, ob das Steuerelement sichtbar ist.
	 * Wenn nicht, akzeptiert es automatisch auch keinen Input.
	 */
	private boolean visible = true;

	/**
	 * Zeigt an, ob die Koordinaten des Steuerelements verändert werden können.
	 * Kann mittels {@link #chain()} und {@link #unchain()} gesteuert werden.
	 */
	private boolean chained = false;

	/**
	 * Die Liste der MouseListener.
	 */
	private List<MouseListener> mouseListeners;

	/**
	 * Die Liste der MouseMotionListener.
	 */
	private List<MouseMotionListener> mouseMotionListeners;

	/**
	 * Die Liste der MouseWheelListener.
	 */
	private List<MouseWheelListener> mouseWheelListeners = new LinkedList<MouseWheelListener>();

	/**
	 * Die Steuerelemente, die von diesem hier abhängen. Die Koordinatenangaben der
	 * untergeordneten Elemente werden relativ zu diesem hier angegeben.
	 */
	private Hashtable<String, Component> children = new Hashtable<String, Component>();

	/**
	 * Die Farbgebung innen und außen.
	 */
	private Border border;

	/**
	 * Indicates whether the mouse is inside the components' bounds or not.
	 */
	private boolean mouseFocused = false;

	/**
	 * Die Standardschriftart.
	 */
	public static final Font STD_FONT = new Font("Consolas", Font.PLAIN, 13);

	public static final Insets STD_INSETS = new Insets(5, 5, 5, 5);

	/**
	 * Erstellt eine Component, deren Daten nicht bekannt sind.
	 */
	public Component() {

		mouseListeners = new LinkedList<MouseListener>();
		mouseMotionListeners = new LinkedList<MouseMotionListener>();

		bounds = new Rectangle();

		border = new Border();
		border.setComponent(this);

		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent arg0) {
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				if (status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
			}
		});

		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (status != ComponentStatus.NOT_AVAILABLE && status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
				mouseFocused = true;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if (status != ComponentStatus.NOT_AVAILABLE && status != ComponentStatus.NO_MOUSE) {
					status = ComponentStatus.NO_MOUSE;
				}
				mouseFocused = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (status != ComponentStatus.CLICK) {
					status = ComponentStatus.CLICK;
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
			}

		});

		this.status = ComponentStatus.NO_MOUSE;
		this.name = Integer.toString(this.hashCode());
	}

	/**
	 * Nur zu Debug-Zwecken. Gibt den Status einer Component auf die Konsole aus.
	 *
	 * @param c The component of which status to print.
	 */
	protected static void printStatus(final Component c) {
		switch (c.getStatus()) {
			case NO_MOUSE:
				System.out.println(c.name + ": NO_MOUSE");
				break;
			case MOUSE:
				System.out.println(c.name + ": MOUSE");
				break;
			case CLICK:
				System.out.println(c.name + ": CLICK");
				break;
			case NOT_AVAILABLE:
				System.out.println(c.name + ": NOT_AVAILABLE");
				break;
			default:
				break;
		}
	}

	/**
	 * Eine Component. Fügt diese Component auch sofort zu der Auflistung von
	 * Komponenten des angegebenen Screens hinzu.
	 *
	 * @param x       Die x position.
	 * @param y       Die y position.
	 * @param width   Die Breite.
	 * @param height  Die Höhe.
	 * @param backing Der dahinter liegende Screen, der die Component verwaltet.
	 */
	public Component(int x, int y, int width, int height, Screen backing) {

		this();

		bounds = new Rectangle(x, y, width, height);

		setBackingScreen(backing);

	}

	/**
	 * Legt fest, dass die Koordinaten des Steuerelements nicht verändern werden dürfen. Ist für die
	 * Parent-Children-Beziehung in Component gedacht, kann aber auch so verwendet werden.
	 * Wenn diese Methode auf einem Steuerelement aufgerufen wird, welches ein Parent-Steuerelement
	 * hat, dann ändert sich die <b>relative</b> Position zum Parent-Steuerelement nicht.
	 * Dafür kann dieses Steuerelement auch nicht individuell verschoben werden, bis
	 * {@link #unchain()} aufgerufen wird.
	 *
	 * @see #unchain()
	 */
	public void chain() {
		if (!chained) {
			chained = true;
		}
	}

	/**
	 * Legt fest, dass die Koordinaten des Steuerelements sich verändern dürfen. Dieser
	 * Zustand kann von {@link #chain()} wieder aufgelöst werden.
	 *
	 * @see #chain()
	 */
	public void unchain() {
		if (chained) {
			chained = false;
		}
	}

	/**
	 * @return the status
	 */
	public ComponentStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ComponentStatus status) {
		this.status = status;
	}

	/**
	 * @return the backingScreen
	 */
	public Screen getBackingScreen() {
		return backingScreen;
	}

	/**
	 * @param backingScreen the backingScreen to set
	 */
	public void setBackingScreen(Screen backingScreen) {
		if (this.backingScreen != null) {
			this.backingScreen.remove(this);
		}
		this.backingScreen = backingScreen;
		this.backingScreen.add(this);
	}

	public List<MouseListener> getMouseListeners() {
		return mouseListeners;
	}

	public void addMouseListener(MouseListener m) {
		mouseListeners.add(m);
	}

	public void removeMouseListener(MouseListener m) {
		if (mouseListeners.contains(m)) {
			mouseListeners.remove(m);
		}
	}

	public List<MouseMotionListener> getMouseMotionListeners() {
		return mouseMotionListeners;
	}

	public void addMouseMotionListener(MouseMotionListener e) {
		mouseMotionListeners.add(e);
	}

	public void removeMouseMotionListener(MouseMotionListener m) {
		if (mouseMotionListeners.contains(m)) {
			mouseMotionListeners.remove(m);
		}
	}

	public List<MouseWheelListener> getMouseWheelListeners() {
		return mouseWheelListeners;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return getSimplifiedBounds().x;
	}

	/**
	 * Setzt die x Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
	 * <code>true</code> zurückgibt.
	 *
	 * @param x Die neue x Position des Steuerelements.
	 */
	public void setX(int x) {
		if (!chained) {
			AffineTransform transform = AffineTransform.getTranslateInstance(x - getX(), 0);
			bounds = transform.createTransformedShape(bounds);
		}
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return getSimplifiedBounds().y;
	}

	/**
	 * Setzt die y Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
	 * <code>true</code> ist.
	 *
	 * @param y Die neue y Position des Steuerelements.
	 */
	public void setY(int y) {
		if (!chained) {
			AffineTransform transform = AffineTransform.getTranslateInstance(0, y - getY());
			bounds = transform.createTransformedShape(bounds);
		}
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return getSimplifiedBounds().width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		if(getWidth() != 0) {
			Shape old = bounds;
			AffineTransform centerTransform = AffineTransform.getTranslateInstance(-getX(), -getY());
			AffineTransform resetTransform = AffineTransform.getTranslateInstance(getX(), getY());

			Shape transforming = centerTransform.createTransformedShape(old);

			AffineTransform transform = AffineTransform.getScaleInstance((double) width / getWidth(), 1);

			transforming = transform.createTransformedShape(transforming);
			bounds = resetTransform.createTransformedShape(transforming);
		} else {
			bounds = new Rectangle(getX(), getY(), width, getHeight());
		}
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return getSimplifiedBounds().height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		if(getHeight() != 0) {
			Shape old = bounds;
			AffineTransform centerTransform = AffineTransform.getTranslateInstance(-getX(), -getY());
			AffineTransform resetTransform = AffineTransform.getTranslateInstance(getX(), getY());

			Shape transforming = centerTransform.createTransformedShape(old);

			AffineTransform transform = AffineTransform.getScaleInstance(1, (double) height / getHeight());

			transforming = transform.createTransformedShape(transforming);
			bounds = resetTransform.createTransformedShape(transforming);
		} else {
			bounds = new Rectangle(getX(), getY(), getWidth(), height);
		}

	}

	/**
	 * Streckt {@link #getBounds()}, sodass {@link #getBounds()} mit {@link #getWidth()} und
	 * {@link #getHeight()} wieder übereinstimmen.
	 *
	 * @param x Der Streckfaktor in x Richtung
	 * @param y Der Streckfaktor in y Richtung
	 */
	public void scale(double x, double y) {
		throw new NotImplementedException();
	}

	/**
	 * Diese Methode nimmt an, dass die {@link #bounds} rechteckig gemacht werden sollen.
	 *
	 * @param x width
	 * @param y height
	 */
	@Deprecated
	void assumeRect(int x, int y) {
		throw new NotImplementedException();
	}

	/**
	 * @return the bounds
	 */
	public Shape getBounds() {
		return bounds;
	}

	/**
	 * Setzt die Grenzen des Steuerelements neu. Methode sollte noch nicht verwendet werden, da sie
	 * den Mausinput durcheinander bringen kann.
	 *
	 * @param bounds Das neue Polygonobjekt.
	 */
	protected final void setBounds(Shape bounds) {
		this.bounds = bounds;
	}

	/**
	 * Erstellt eine neue Instanz eines Rechtecks. In diesen werden Position,
	 * Breite und Höhe vereinfacht zusammengefasst. Mit jedem Aufruf dieser
	 * Methode wird eine neue Instanz eines Rechtecks erstellt.
	 *
	 * @return Ein neues Rechteck mit der vereinfachten BoundingBox.
	 */
	public Rectangle getSimplifiedBounds() {
		return getBounds().getBounds();
	}

	/**
	 * Berechnet das umgebende Rechteck eines auf dem Display darstellbaren
	 * Textes in Pixeln.
	 *
	 * @param text Der Text, der benutzt werden soll.
	 * @param f    Die Schriftart.
	 * @return Das umgebende Rechteck des Texts in Pixel.
	 */
	public static Dimension getTextBounds(String text, Font f) {
		AffineTransform affinetransform = new AffineTransform();
		FontRenderContext frc = new FontRenderContext(affinetransform, true,
				true);
		return new Dimension((int) (f.getStringBounds(text, frc)).getWidth(),
				(int) (f.getStringBounds(text, frc).getHeight()));
	}

	/**
	 * Vergleicht, ob die gewählte Schriftart im System installiert ist
	 * <p/>
	 * Comment (Josip): This method is working fine, however, it does not look for fonts in custom
	 * directories. I don't know how to fix that problem.
	 *
	 * @param f - Die Schriftart die verglichen werden soll
	 */
	public static boolean isFontInstalled(Font f) {
		for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			if (font.getFontName().equals(f.getFontName()))
				return true;
		}
		return false;
	}

	/**
	 * Veranlasst das Steuerelement, wieder Input zu akzeptieren.
	 */
	public void acceptInput() {
		status = ComponentStatus.NO_MOUSE;
		acceptingInput = true;
	}

	/**
	 * Veranlasst das Steuerelement, keinen Input mehr zu akzeptieren.
	 */
	public void declineInput() {
		status = ComponentStatus.NOT_AVAILABLE;
		acceptingInput = false;
	}

	public void remove(Component c) {
		if (children.containsKey(c.getName()) && children.containsValue(c)) {
			children.remove(c.getName());
		}
	}

	/**
	 * Passt den Sichtbarkeitswert zurück.
	 *
	 * @return Den Sichtbarkeitswert.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Setzt die Sichtbarkeit des Steuerelements. Wenn die neue Sichtbarkeit false ist, dann
	 * akzeptiert das Steuerelement keinen Input mehr. Wäre auch unlogisch, wenn ein unsichtbares
	 * Steuerelement Input akzeptieren würde.
	 *
	 * @param vvvvvv Der neue Sichtbarkeitswert.
	 */
	public void setVisible(boolean vvvvvv) {
		visible = vvvvvv;
		if (vvvvvv) {
			acceptInput();
		} else {
			declineInput();
		}
	}

	/**
	 * Zeigt an, ob die Koordinaten des Steuerelements verändert werden können.
	 * Kann mittels {@link #chain()} und {@link #unchain()} gesteuert werden.
	 *
	 * @return Ob die Koordinaten des Steuerelements verändert werden können.
	 */
	public boolean isChained() {
		return chained;
	}

	public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen neu.
	 *
	 * @param name Der neue Name der Component.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gibt den Wert zurück, ob das Steuerelement Input akzeptiert.
	 *
	 * @return Ob die Component Input akzeptiert.
	 */
	public boolean isAcceptingInput() {
		return acceptingInput;
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border border) {
		this.border = border;
	}

	public void updateGUI() {
	}

	/**
	 * Returns <code>true</code> if, and only if, the mouse is in the components' bounds.
	 *
	 * @return <code>true</code> if, and only if, the mouse is in the components' bounds.
	 */
	public boolean isMouseFocused() {
		return mouseFocused;
	}

	/**
	 * Triggers all registered listeners to be executed with a specified mouse event.
	 *
	 * @param event The event to pass to the listeners.
	 */
	public void triggerListeners(MouseEvent event) {
		for (MouseListener listener : mouseListeners) {
			listener.mouseReleased(event);
		}
	}

	/**
	 * Calculates the center point of the component's bounding box.
	 * For now, the simplified bounds will be used for calculation.
	 *
	 * @return The center point of the component's simplified bounding box.
	 */
	public Point center() {
		Rectangle r = getSimplifiedBounds();
		return new Point(r.x + r.width / 2, r.y + r.height / 2);
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.remove(mouseWheelListener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.add(mouseWheelListener);
	}

	// Some static helper methods...

	/**
	 * Creates a rectangular polygon.
	 *
	 * @param x      The x position of the polygon.
	 * @param y      The y position of the polygon.
	 * @param width  The width of the rectangle.
	 * @param height Ditto.
	 * @return A polygon with a rectangular shape.
	 */
	public static Polygon createRectPolygon(int x, int y, int width, int height) {
		Polygon ret = new Polygon();
		ret.addPoint(x, y);
		ret.addPoint(x, y + height);
		ret.addPoint(x + width, y + height);
		ret.addPoint(x + width, y);
		return ret;
	}

	public static Polygon createRectPolygon(Point p1, Point p2, Point p3, Point p4) {
		Polygon ret = new Polygon();
		ret.addPoint(p1.x, p1.y);
		ret.addPoint(p2.x, p2.y);
		ret.addPoint(p3.x, p3.y);
		ret.addPoint(p4.x, p4.y);
		return ret;
	}
}

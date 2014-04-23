package comp;

import general.Main;
import gui.Drawable;
import gui.Screen;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
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
 * Übergeordnete Basisklasse von allen klickbaren Steuerelementen.
 * 
 * @author Josip
 * @version 24.2.2014
 * @see Button
 * @see gui.Screen
 *
 */
public abstract class Component implements Drawable {

	/**
	 * Zeigt den Status an, in welchem das Steuerelement sich befindet.
	 * 
	 * @version 2.10.2013
	 * 
	 */
	public enum ComponentStatus {
		NO_MOUSE, MOUSE, CLICK, NOT_AVAILABLE
	}

	private int x, y;

	private int width, height;

	private ComponentStatus status = ComponentStatus.NO_MOUSE;

	private Screen backingScreen;

	/**
	 * Das umfassende Polygon um die Komponente.
	 */
	private Polygon bounds = new Polygon();
	
	/**
	 * Die vereinfachte BoundingBox, die anstelle des Polygons auftritt.
	 */
	private Rectangle simplifiedBounds = new Rectangle();
	
	/**
	 * Der Name des Steuerelements. Wird hauptsächlich für {@link Component#children} benötigt.
	 */
	private String name;
	
	/**
	 * Zeigt an, ob die Component willig ist, Input zu akzeptieren. 
	 * Standardmäßig auf true gesetzt.
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
	 * Das Steuerelement, auf das sich dieses Element bezieht. Wenn nicht null, sind die
	 * Koordinatenangaben dieses Steuerelements relativ auf das des {@link Component#parent}
	 * bezogen.
	 */
	private Component parent;
	
	/**
	 * Die Farbgebung innen und außen.
	 */
	private Border border;

    /**
     * Indicates whether the mouse is inside the components' bounds or not.
     */
    private boolean mouseFocused = false;
	
	/**
	 * Sagt aus, ob die Polygon-Bounds oder die {@link #getSimplifiedBounds()}
	 * als Standard benutzt werden.
	 */
	private static final boolean USING_POLYGON = true;

	/**
	 * Die Standardschriftart.
	 */
	public static final Font STD_FONT = new Font("Consolas", Font.PLAIN, 12);
	
	/**
	 * Die Anzahl an Punkten, die je in {@link Polygon#xpoints} und {@link Polygon#ypoints}
	 * gespeichert werden können sollen.
	 */
	public static final int POLYGON_BUFFER_CAPACITY = 20;
	
	public static final Insets STD_INSETS = new Insets(5, 5, 5, 5);

	/**
	 * Erstellt eine Component, deren Daten nicht bekannt sind.
	 */
	public Component() {
		
		mouseListeners = new LinkedList<MouseListener>();
		mouseMotionListeners = new LinkedList<MouseMotionListener>();
		
		bounds.xpoints = new int[POLYGON_BUFFER_CAPACITY];
		bounds.ypoints = new int[POLYGON_BUFFER_CAPACITY];
		
		border = new Border();
		border.setComponent(this);

        setX(0);
        setY(0);
        assumeRect(0, 0);
		
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
				if(status != ComponentStatus.NOT_AVAILABLE && status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				if(status != ComponentStatus.NOT_AVAILABLE && status != ComponentStatus.NO_MOUSE) {
					status = ComponentStatus.NO_MOUSE;
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if(status != ComponentStatus.CLICK) {
					status = ComponentStatus.CLICK;
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
			}

		});
		
		this.status = ComponentStatus.NO_MOUSE;
		this.name = Integer.toString(this.hashCode());
	}
	
	/**
	 * Nur zu Debug-Zwecken. Gibt den Status einer Component auf die Konsole aus.
	 * @param c The component of which status to print.
	 */
	protected static final void printStatus(final Component c) {
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
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param backing
	 * 
	 */
	public Component(int x, int y, int width, int height, Screen backing) {
		
		this();
		
//		setAbsoluteX(x);
//		setAbsoluteY(y);
//		
//		setWidth(width);
//		setHeight(height);
		
//		bounds.xpoints[0] = x;
//		bounds.xpoints[1] = x + width;
//		bounds.xpoints[2] = x + width;
//		bounds.xpoints[3] = x;
//		
//		bounds.ypoints[0] = y;
//		bounds.ypoints[1] = y;
//		bounds.ypoints[2] = y + height;
//		bounds.ypoints[3] = y + height;
//		bounds.invalidate();
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		bounds.addPoint(x, y);
		bounds.addPoint(x + width, y);
		bounds.addPoint(x + width, y + height);
		bounds.addPoint(x, y + height);
		
		setBackingScreen(backing);
		
	}
	
	/**
	 * Erstellt eine Component. Der Screen wird vom parent bezogen.
	 * @param x Die x position.
	 * @param y Die y position.
	 * @param width Die Breite.
	 * @param height Die Höhe.
	 * @param parent Das übergeordnete Steuerelement.
	 */
	public Component(int x, int y, int width, int height, Component parent) {		
		
		this(x + parent.x, y + parent.y, width, height, parent.backingScreen);
//		bounds.translate(parent.x, parent.y);
		
		makeChildrenOf(parent);
	}
	
	/**
	 * Reiht dieses Steuerelement unter das angegebene ein und macht alle Koordinatenangaben
	 * relativ zur parent. Wenn das übergebene Steuerelement <code> == null</code>, ist
	 * das Steuerelement an kein anderes mehr gebunden, seine Koordinaten sind dann wieder
	 * absolut anzugeben.
	 * @param p Das neue Eltern-Steuerelement, oder null.
	 */
	public void makeChildrenOf(Component p) {
		if(hasParent()) {
			if(parent.children.contains(this)) {
				parent.children.remove(name);
			}
			
			// normalisiert die Koordinaten erst
			
			x += parent.x;
			y += parent.y;
		}
		
		parent = p;
		
		if(hasParent()) {
			
			p.children.put(name, this);

			// relativiert die Koordinaten wieder
			x -= parent.x;
			y -= parent.y;

		}
	}
	
	/**
	 * Macht genau dasselbe wie {@link #makeChildrenOf(Component)}, führt aber keine
	 * Änderungen an den Koordinaten durch.
	 * <br><br>
	 * <code>
	 * relative_x = absolute_x;
	 * <br>
	 * relative_y = absolute_y;
	 * </code>
	 * @param p Die Component zu der diese Component relativ gemacht werden soll.
	 * @see #makeChildrenOf(Component)
	 */
	void defaultMakeChildrenOf(Component p) {
		if(parent != null) {
			if(parent.children.contains(this)) {
				parent.children.remove(name);
			}
		}
		
		parent = p;
		
		if(p != null) {
			p.children.put(name, this);
		}
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
		if(!chained) {
			chained = true;
		}
	}
	
	/**
	 * Legt fest, dass die Koordinaten des Steuerelements sich verändern dürfen. Dieser
	 * Zustand kann von {@link #chain()} wieder aufgelöst werden.
	 * @see #chain()
	 */
	public void unchain() {
		if(chained) {
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
	 * @param status
	 *            the status to set
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
	 * @param backingScreen
	 *            the backingScreen to set
	 */
	public void setBackingScreen(Screen backingScreen) {
		if(this.backingScreen != null) {
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
		if(mouseListeners.contains(m)) {
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
		if(mouseMotionListeners.contains(m)) {
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
		return x;
	}

	/**
	 * Setzt die x Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
	 * <code>true</code> zurückgibt.
	 * @param x Die neue x Position des Steuerelements.
	 */
	public void setX(int x) {
		if(!chained) {
			if(USING_POLYGON) {
				bounds.translate(x - this.x, 0);
				bounds.invalidate();
			}
			this.x = x;
			refreshSimplifiedBounds();
		}
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * Setzt die y Position des Steuerelements. Kann nicht verändert werden, solange {@link #isChained()}
	 * <code>true</code> ist.
	 * @param y Die neue y Position des Steuerelements.
	 */
	public void setY(int y) {
		if(!chained) {
			if(USING_POLYGON) {
				bounds.translate(0, y - this.y);
				bounds.invalidate();
			}
			this.y = y;
			refreshSimplifiedBounds();
		}
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(int width) {
		if(USING_POLYGON) {
			if(this.width != 0) {
				scale((double) width / this.width, 0);
			} else {
				assumeRect(width, 0);
			}
		}
		this.width = width;
		refreshSimplifiedBounds();
	}
	
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(int height) {
		if(USING_POLYGON) {
			if(this.height != 0) {
				scale(0, (double) height / this.height);
			} else {
				assumeRect(0, height);
			}
		}
		this.height = height;
		refreshSimplifiedBounds();
	}
	
	/**
	 * Streckt {@link #getBounds()}, sodass {@link #getBounds()} mit {@link #getWidth()} und
	 * {@link #getHeight()} wieder übereinstimmen.
	 * @param x Der Streckfaktor in x Richtung
	 * @param y Der Streckfaktor in y Richtung
	 */
	public void scale(double x, double y) {
		
		// ausgehen, dass nicht mit 0 multipliziert werden soll
		if(x == 0) {
			x = 1;
		}
		// ausgehen, dass nicht mit 0 multipliziert werden soll
		if(y == 0) {
			y = 1;
		}
//		// setzt die Transformationsmatrix auf
//		transform.setToScale(x, y);
//		// berechnet neue Koordinaten anhand der Matrix
//		bounds = (Polygon) transform.createTransformedShape(bounds);
//		// stellt die Einheitsmatrix wieder her
//		transform.setToIdentity();
		
		// die gespeicherte Translation für später
		Dimension orig_translation = new Dimension(bounds.getBounds().x, bounds.getBounds().y);
		// transliiert auch das Polygon (wird später zurückgesetzt)
		bounds.translate(-orig_translation.width, -orig_translation.height);
		
		if(x != 1.0) {
			for (int i = 0; i < bounds.xpoints.length; i++) {
				if(bounds.xpoints[i] != 0) {
					bounds.xpoints[i] *= x;
				}
			}
		}
		
		if(y != 1.0) {
			for (int i = 0; i < bounds.ypoints.length; i++) {
				if(bounds.ypoints[i] != 0) {
					bounds.ypoints[i] *= y;
				}
			}
		}
		
//		int it = bounds.npoints;
//		for (int i = 0; i < it; i++) {
//			bounds.addPoint(bounds.xpoints[i], bounds.ypoints[i]);
//		}
		
		bounds.translate(orig_translation.width, orig_translation.height);
		
		bounds.invalidate();
	}
	
	/**
	 * Diese Methode nimmt an, dass die {@link #bounds} rechteckig gemacht werden sollen.
	 * @param x width
	 * @param y height
	 */
	void assumeRect(int x, int y) {
		if(x != 0) {
			bounds.xpoints = new int[POLYGON_BUFFER_CAPACITY];
			bounds.xpoints[0] = getAbsoluteX();
			bounds.xpoints[1] = getAbsoluteX() + x;
			bounds.xpoints[2] = getAbsoluteX() + x;
			bounds.xpoints[3] = getAbsoluteX();
		}
		
		if(y != 0) {
			bounds.ypoints = new int[POLYGON_BUFFER_CAPACITY];
			bounds.ypoints[0] = getAbsoluteY();
			bounds.ypoints[1] = getAbsoluteY();
			bounds.ypoints[2] = getAbsoluteY() + y;
			bounds.ypoints[3] = getAbsoluteY() + y;
		}
		bounds.invalidate();
	}

	/**
	 * @return the bounds
	 */
	public Polygon getBounds() {
		return bounds;
	}

//	/**
//	 * Setzt die Grenzen des Steuerelements neu. Methode sollte noch nicht verwendet werden, da sie
//	 * den Mausinput durcheinander bringen kann.
//	 * @param bounds
//	 */
//	@Deprecated
//	protected void setBounds(Polygon bounds) {
//		this.bounds = bounds; 
//	}

	/**
	 * Erstellt eine neue Instanz eines Rechtecks. In diesen werden Position,
	 * Breite und Höhe vereinfacht zusammengefasst. Mit jedem Aufruf dieser
	 * Methode wird eine neue Instanz eines Rechtecks erstellt.
	 * 
	 * @return Ein neues Rechteck mit der vereinfachten BoundingBox.
	 */
	public Rectangle getSimplifiedBounds() {
		return simplifiedBounds;
	}
	
	/**
	 * Aktualisiert die {@link #simplifiedBounds}.
	 */
	private void refreshSimplifiedBounds() {
		simplifiedBounds.x = getAbsoluteX();
		simplifiedBounds.y = getAbsoluteY();
		simplifiedBounds.width = getWidth();
		simplifiedBounds.height = getHeight();
	}

	/**
	 * Berechnet das umgebende Rechteck eines auf dem Display darstellbaren
	 * Textes in Pixeln.
	 * 
	 * @param text
	 *            Der Text, der benutzt werden soll.
	 * @param f
	 *            Die Schriftart.
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
	 * Veranlasst das Steuerelement, wieder Input zu akzeptieren.
	 * @see Component.declineInput
	 */
	public void acceptInput() {
		status = ComponentStatus.NO_MOUSE;
		acceptingInput = true;
	}
	
	/**
	 * Veranlasst das Steuerelement, keinen Input mehr zu akzeptieren.
	 * @see Component.acceptInput
	 */
	public void declineInput() {
		status = ComponentStatus.NOT_AVAILABLE;
		acceptingInput = false;
	}
	
	/**
	 * Funktioniert diese Funktion überhaupt?
	 */
	public void virtualClick() {
		for (MouseListener m : mouseListeners) {
			m.mouseReleased(new MouseEvent(Main.getGameWindow(), 
					MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 
					0, x + 1, y + y, x + 1, y + 1, 1, false, MouseEvent.BUTTON1));
		}
	}
	
	public void remove(Component c) {
		if(children.containsKey(c.getName()) && children.containsValue(c)) {
			children.remove(c.getName());
		}
	}
	
	/**
	 * Passt den Sichtbarkeitswert zurück.
	 * @return
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Setzt die Sichtbarkeit des Steuerelements. Wenn die neue Sichtbarkeit false ist, dann
	 * akzeptiert das Steuerelement keinen Input mehr. Wäre auch unlogisch, wenn ein unsichtbares
	 * Steuerelement Input akzeptieren würde.
	 * @param vvvvvv Der neue Sichtbarkeitswert.
	 */
	public void setVisible(boolean vvvvvv) {
		visible = vvvvvv;
		if(vvvvvv) {
			acceptInput();
		} else {
			declineInput();
		}
	}
	
	/**
	 *
	 * Zeigt an, ob die Koordinaten des Steuerelements verändert werden können.
	 * Kann mittels {@link #chain()} und {@link #unchain()} gesteuert werden.
	 *
	 * @return Ob die Koordinaten des Steuerelements verändert werden können.
	 */
	public boolean isChained() {
		return chained;
	}
	
	/**
	 * Sagt aus, ob die Component ein {@link #parent} besitzt.
	 * @return
	 */
	public boolean hasParent() {
		return parent != null;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Setzt den Namen sowohl in der Hashtable der {@link #parent}-Component (sofern vorhanden),
	 * als auch in diesem Objekt neu.
	 * @param name Der neue Name der Component.
	 */
	public void setName(String name) {
		Component ancestor = parent;
		makeChildrenOf(null);
		this.name = name;
		makeChildrenOf(ancestor);
	}
	
	/**
	 * Gibt den Wert zurück, ob das Steuerelement Input akzeptiert.
	 * @return
	 */
	public boolean isAcceptingInput() {
		return acceptingInput;
	}
	
	/**
	 * Gibt das übergeordnete Steuerelement zurück, sonst null.
	 * @return Das übergeordnete Steuerelement, oder null, sofern keines vorhanden.
	 */
	public Component getParent() {
		return parent;
	}
	
	/**
	 * Passt die absolute x Position der Component zurück.
	 * @return
	 */
	public int getAbsoluteX() {
		if(hasParent()) {
			return x + parent.getAbsoluteX();
		} else {
			return x;
		}
	}
	
	/**
	 * Passt die absolute y-Position der Component zurück.
	 * @return
	 */
	public int getAbsoluteY() {
		if(hasParent()) {
			return y + parent.getAbsoluteY();
		} else {
			return y;
		}
	}
	
	/**
	 * Setzt die absolute Position der Component. Macht genau das selbe wie {@link #setX(int)},
	 * wenn die Component kein übergeordnetes Steuerelement besitzt.
	 * @param x Die neue absolute x Koordinate der Component.
	 */
	public void setAbsoluteX(int x) {
		if(hasParent()) {
			setX(relativeCoordinates(x, 0).x);
		} else {
			setX(x);
		}
	}
	
	/**
	 * Setzt die absolute Position der Component. Macht genau das selbe wie {@link #setY(int)},
	 * wenn die Component kein übergeordnetes Steuerelement besitzt.
	 * @param y Die neue absolute y Koordinate der Component.
	 */
	public void setAbsoluteY(int y) {
		if(hasParent()) {
			setY(relativeCoordinates(0, y).y);
		} else {
			setY(y);
		}
	}
	
	/**
	 * Relativiert angegebene absolute Koordinaten zu der {@link #parent}-Component.
	 * Wenn {@link #hasParent()} <code>== false</code>, dann gibt es die angegebenen
	 * Koordinaten ohne Änderungen direkt zurück.
	 * @param abs_x
	 * @param abs_y
	 * @return
	 */
	public Point relativeCoordinates(int abs_x, int abs_y) {
		if(hasParent()) {
			return new Point(abs_x - parent.getAbsoluteX(), abs_y - parent.getAbsoluteY());
		} else {
			return new Point(abs_x, abs_y);
		}
	}
	
	/**
	 * Macht angegebene relative Koordinaten absolut.
	 * Wenn {@link #hasParent()} <code>== false</code>, dann gibt es die angegebenen
	 * Koordinaten ohne Änderungen direkt zurück.
	 * @return
	 */
	public Point absoluteCoordinates(int rel_x, int rel_y) {
		if(hasParent()) {
			return new Point(rel_x + parent.getAbsoluteX(), rel_y + parent.getAbsoluteY());
		} else {
			return new Point(rel_x, rel_y);
		}
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
     * @return <code>true</code> if, and only if, the mouse is in the components' bounds.
     */
    public boolean isMouseFocused() {
        return mouseFocused;
    }

    public void setMouseFocused(boolean mouseFocused) {
        this.mouseFocused = mouseFocused;
    }
}

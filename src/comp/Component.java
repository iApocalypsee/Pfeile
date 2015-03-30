package comp;

import general.Delegate;
import general.JavaInterop;
import general.Main;
import geom.Vector2;
import gui.screen.Screen;
import scala.Function1;
import scala.Unit;
import scala.runtime.BoxedUnit;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.LinkedList;

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
	private Shape bounds;

	private Shape srcShape;

	private boolean isTransformationChangedSince = false;

	private final Transformation2D transformation = new Transformation2D();

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
	 * Die Liste der MouseListener.
	 */
	private java.util.List<MouseListener> mouseListeners;

	/**
	 * Die Liste der MouseMotionListener.
	 */
	private java.util.List<MouseMotionListener> mouseMotionListeners;

	/**
	 * Die Liste der MouseWheelListener.
	 */
	private java.util.List<MouseWheelListener> mouseWheelListeners = new LinkedList<>();

	/**
	 * Die Steuerelemente, die von diesem hier abhängen. Die Koordinatenangaben der
	 * untergeordneten Elemente werden relativ zu diesem hier angegeben.
	 */
	private Hashtable<String, Component> children = new Hashtable<>();

	/**
	 * Die Farbgebung innen und außen.
	 */
	private Border border;

	/**
	 * The parent of the component, if any.
	 * Coordinates are given according to the parent. If the parent is null, the corodinates
	 * are absolute.
	 */
	private Component parent = null;

	/**
	 * Ability of the component to draw additional things that belong to the component
	 * but don't have to belong to the boundaries of the component.
	 */
	private Function1<Graphics2D, Unit> additionalDrawing = null;

	/**
	 * Called when the bounds have been moved.
	 */
	public final Delegate.Delegate<Vector2> onMoved = new Delegate.Delegate<>();

	/**
	 * Called when the component's dimensions have changed.
	 */
	public final Delegate.Delegate<Vector2> onResize = new Delegate.Delegate<>();

	/**
	 * Called when any transformation has been done to the component.
	 */
	public final Delegate.Function0Delegate onTransformed = new Delegate.Function0Delegate();

	/**
	 * Indicates whether the mouse is inside the components' bounds or not.
	 */
	private boolean mouseFocused = false;

	/** In order to increase the size of all Text-Elements for higher resolutions, this method returns the size of
	 * <code>STD_FONT</code> in size 13, if the window is smaller or equal to FullHD (1080) or in size 14, if the
	 * window is bigger than fullHD. This method is used to bypass the assignment of the final <code>STD_FONT</code>
	 * variable.
	 *
	 * @return STD_FONT
	 */
	private static Font getSTD_FONT() {
		if (Main.getWindowHeight() > 1080)
			return new Font("Consolas", Font.PLAIN, 14);
		else
			return new Font("Consolas", Font.PLAIN, 13);
	}

	/** The standard font, which is used if no special font has been set. Use it in Buttons, Lists,... */
	public static final Font STD_FONT = getSTD_FONT();

	public static final Insets STD_INSETS = new Insets(7, 7, 10, 7);

	/**
	 * Erstellt eine Component, deren Daten nicht bekannt sind.
	 */
	public Component() {
		mouseListeners = new LinkedList<>();
		mouseMotionListeners = new LinkedList<>();

		srcShape = new Rectangle();

		border = new Border();
		border.setComponent(this);

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				if (status != ComponentStatus.MOUSE) {
					status = ComponentStatus.MOUSE;
				}
			}
		});

		addMouseListener(new MouseAdapter() {

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

		transformation.onTranslated().register(JavaInterop.asScalaFunctionFun((TranslationChange t) -> {
            isTransformationChangedSince = true;
            onTransformed.apply();
            return BoxedUnit.UNIT;
        }));

		transformation.onScaled().register(JavaInterop.asScalaFunctionFun((ScaleChange t) -> {
            isTransformationChangedSince = true;
            onTransformed.apply();
            return BoxedUnit.UNIT;
        }));

		transformation.onRotated().register(JavaInterop.asScalaFunctionFun((RotationChange t) -> {
            isTransformationChangedSince = true;
            onTransformed.apply();
            return BoxedUnit.UNIT;
        }));
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

		// The component's model is now in its own model space.
		setSourceShape(new Rectangle(-width / 2, -height / 2, width, height));
		transformation.translate(x, y);

		setBackingScreen(backing);

	}

	public Component(Vector2 initialPosition, Shape srcShape, Screen backing) {
		super();
		setSourceShape(srcShape);
		transformation.translate(initialPosition.x(), initialPosition.y());
		setBackingScreen(backing);
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

	public java.util.List<MouseListener> getMouseListeners() {
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

	public java.util.List<MouseMotionListener> getMouseMotionListeners() {
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

	public java.util.List<MouseWheelListener> getMouseWheelListeners() {
		return mouseWheelListeners;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return (int) getPreciseRectangle().getX();
	}

	public int getRelativeX() {
		return (int) transformation.translation().x() - (parent != null ? parent.getX() : 0);
	}

	/**
	 * Setzt die x Position des Steuerelements.
	 *
	 * @param x Die neue x Position des Steuerelements.
	 */
	public void setX(int x) {
		final Vector2 oldPosition = transformation.translation();
		transformation.translate(x - oldPosition.x() + getPreciseRectangle().getWidth() / 2, 0);
	}

	public void setRelativeX(int x) {
		if(parent == null) throw new NullPointerException("Parent of component is null.");
		// Just translate the relative coordinate to the absolute coordinate.
		setX(x + parent.getX());
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return (int) getPreciseRectangle().getY();
	}

	public int getRelativeY() {
		return (int) transformation.translation().y() - (parent != null ? parent.getY() : 0);
	}

	/**
	 * Setzt die y Position des Steuerelements.
	 *
	 * @param y Die neue y Position des Steuerelements.
	 */
	public void setY(int y) {
		final Vector2 oldPosition = transformation.translation();
		transformation.translate(0, y - oldPosition.y() + getPreciseRectangle().getHeight() / 2);
	}

	public void setRelativeY(int y) {
		if(parent == null) throw new NullPointerException("Parent of component is null.");
		setY(y + parent.getY());
	}

	public void setLocation(int x, int y) {
		int xTranslation = x - getX(), yTranslation = y - getY();
		AffineTransform transform = AffineTransform.getTranslateInstance(xTranslation, yTranslation);
		bounds = transform.createTransformedShape(bounds);
		if (xTranslation != 0 || yTranslation != 0) {
			onMoved.apply(new Vector2(xTranslation, yTranslation));
		}
	}

	public void setRelativeLocation(int x, int y) {
		if(parent == null) throw new NullPointerException("Parent of component is null.");
		setLocation(x + parent.getSimplifiedBounds().x, y + parent.getSimplifiedBounds().y);
	}

	public void move(int x, int y) {
		setLocation(getX() + x, getY() + y);
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return (int) getBounds().getBounds2D().getWidth();
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
        if(width == 0) throw new IllegalArgumentException("Illegal width of 0: Shape implosion");
		double scaleFactor = width / getPreciseRectangle().getWidth();
		//int oldWidth = (int) (srcShape.getBounds().width * transformation.scale().x());

		final double oldX = getPreciseRectangle().getX();
		final double oldY = getPreciseRectangle().getY();

		transformation.scale(scaleFactor, 1);

		final double newX = getPreciseRectangle().getX();
		final double newY = getPreciseRectangle().getY();

		final Vector2 newPosition = Vector2.apply((float) newX, (float) newY);
		final Vector2 oldPosition = Vector2.apply((float) oldX, (float) oldY);
		final Vector2 delta = newPosition.sub(oldPosition).negated();

		// Truncate the result. Apparently, the JVM does rounding by it self.
		transformation.translate((int) delta.x(), (int) delta.y());

		onResize.apply(new Vector2(width, getHeight()));
	}

    void setWidthDirectly(int width) {

    }

	/**
	 * @return the height
	 */
	public int getHeight() {
		return (int) getPreciseRectangle().getHeight();
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
        if(height == 0) throw new IllegalArgumentException("Illegal height of 0: Shape implosion");
		double scaleFactor = height / getPreciseRectangle().getHeight();

		final double oldX = getPreciseRectangle().getX();
		final double oldY = getPreciseRectangle().getY();

		transformation.scale(1, scaleFactor);

		final double newX = getPreciseRectangle().getX();
		final double newY = getPreciseRectangle().getY();

		final Vector2 newPosition = Vector2.apply((float) newX, (float) newY);
		final Vector2 oldPosition = Vector2.apply((float) oldX, (float) oldY);
		final Vector2 delta = newPosition.sub(oldPosition).negated();

		transformation.translate((int) delta.x(), (int) delta.y());

		onResize.apply(new Vector2(getWidth(), height));
	}

	/**
	 * @return the bounds
	 */
	public Shape getBounds() {
		if(isTransformationChangedSince || bounds == null) {
			bounds = transformation.transformOriginal(srcShape);
		}
		return bounds;
	}

	public Shape getSourceShape() {
		return srcShape;
	}

	public void setSourceShape(Shape srcShape) {
		if(srcShape == null) throw new NullPointerException();
		this.srcShape = srcShape;
		transformation.resetTransformation();
		transformation.translate(srcShape.getBounds().width / 2, srcShape.getBounds().height / 2);
	}

	/**
	 * Erstellt eine neue Instanz eines Rechtecks. In diesen werden Position,
	 * Breite und Höhe vereinfacht zusammengefasst. Mit jedem Aufruf dieser
	 * Methode wird eine neue Instanz eines Rechtecks erstellt.
	 *
	 * @return Ein neues Rechteck mit der vereinfachten BoundingBox.
	 */
	@Deprecated
	public Rectangle getSimplifiedBounds() {
		return getBounds().getBounds();
	}

	public Rectangle2D getPreciseRectangle() {
		return getBounds().getBounds2D();
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
	 * Vergleicht, ob die gewählte Schriftart im System installiert ist.
     * Die Methode läuft sehr langsamm ab, also nur verwenden, wenn wirklich notwendig!
	 *
	 * @param fontName - Name der Schriftart
	 */
	public static boolean isFontInstalled(String fontName) {
		for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
			if (font.getFamily().equals(fontName))
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

	/**
	 * Returns <code>true</code> if, and only if, the mouse is in the components' bounds.
	 *
	 * @return <code>true</code> if, and only if, the mouse is in the components' bounds.
	 */
	public boolean isMouseFocused() {
		return mouseFocused;
	}

	public Component getParent() {
		return parent;
	}

	public void setParent(Component parent) {
		if(this.parent != null) {
			transformation.translate(-this.parent.getX(), -this.parent.getY());
		}
		this.parent = parent;
		if(this.parent != null) {
			transformation.translate(this.parent.getX(), this.parent.getY());
		}
	}

	public Transformation2D getTransformation() {
		return transformation;
	}

	public void resetPosition() {
		transformation.setTranslation(srcShape.getBounds().width / 2, srcShape.getBounds().height / 2);
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
	 * The point is rather the focus point of the component's bounds rather
	 * than its rectangular center.
	 *
	 * @return The center point of the component's simplified bounding box.
	 */
	public Point center() {
		java.util.List<Vector2> vectorList = new LinkedList<>();
		for(PathIterator pi = bounds.getPathIterator(null); !pi.isDone(); pi.next()) {
			final float[] coords = new float[6];
			pi.currentSegment(coords);
			vectorList.add(new Vector2(coords[0], coords[1]));
		}

		float sum_x = 0f;
		float sum_y = 0f;
		for(Vector2 vec : vectorList) {
			sum_x += vec.x();
			sum_y += vec.y();
		}

		float balancePointX = sum_x / vectorList.size();
		float balancePointY = sum_y / vectorList.size();

		return new Point((int) balancePointX, (int) balancePointY);
	}

	/**
	 * Returns the routine that draws additional things on to the screen that belong
	 * to the component. These additional drawn things do not have to be inside the
	 * boundaries of the component. <p>
	 * Subclasses do decide on their own, when and in which way additional drawings are used,
	 * so do not depend on the additional drawing routine to be drawn for every subclass
	 * of Component.
	 *
	 * @return The chunk of code representing the additional drawing commands for the component.
	 */
	public Function1<Graphics2D, Unit> getAdditionalDrawing() {
		return additionalDrawing;
	}

	public void setAdditionalDrawing(Function1<Graphics2D, Unit> additionalDrawing) {
		this.additionalDrawing = additionalDrawing;
	}

	@Override
	public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.remove(mouseWheelListener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.add(mouseWheelListener);
	}

	/**
	 * Applies the given transformation to the bounds of the component. <p>
	 * This call is essentially equivalent to the call
	 * {@code setBounds(transformation.createTransformedShape(getBounds()))}.
	 *
	 * @param transformation The transformation to apply to the bounds.
	 */
	@Deprecated
	public void applyTransformation(AffineTransform transformation) {
		bounds = transformation.createTransformedShape(bounds);
		onTransformed.apply();
	}
}

package comp;

import general.Delegate;
import general.GameWindow;
import geom.Vector;
import gui.screen.Screen;
import scala.Function1;
import scala.Unit;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A standard implementation of a component.
 */
public class Component {

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
	 * The current bounds of the component.
	 * Recomputed on demand. The recompute flag is set when any positional data changes.
	 */
	private Shape bounds;

	/**
	 * The shape from which the component derives its bounds.
	 */
	private Shape srcShape;

	/**
	 * The recompute flag for the bounds.
	 * @see Component#bounds
	 */
	private boolean transformationChangedSince = false;

	/**
	 * Determines whether this component's bounds should recalculate every
	 * time the component is transformed. However, this flag does not prevent
	 * the {@link Component#onTransformed} delegate to be called.
	 */
	private boolean boundsRecalculationIssued = true;

	/**
	 * Object taking care of aligning the bounds to any new positional data given to the
	 * component.
	 */
	private final Transformation2D transformation = new Transformation2D();

	/**
	 * The name of the component. Useful for debugging and necessary for the parent mechanism.
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
	 * Indicates whether this component is visible.
	 * If it is not, the component should not accept input.
	 */
	private boolean visible = true;

    /**
     * Determines whether the next underlying component's listeners should be triggered as well.
     */
    private boolean listenerTransparent = false;

	/**
	 * Self-explanatory.
	 */
	private java.util.List<MouseListener> mouseListeners;

	/**
	 * Self-explanatory.
	 */
	private java.util.List<MouseMotionListener> mouseMotionListeners;

	/**
	 * Self-explanatory.
	 */
	private java.util.List<MouseWheelListener> mouseWheelListeners = new LinkedList<>();

	/**
	 * Saves components that are children of <tt>this</tt>. Duplicate names
	 * are not allowed; if a component tries to hook into another component having another component
	 * with the same name, the new component overrides the old one.
	 */
	private Map<String, Component> children = new ConcurrentHashMap<>();

	/**
	 * Object for coloring borders (if you were to paint them).
	 */
	private Border border;

	/**
	 * The parent of the component, if any.
	 * Coordinates are specified according to the parent. If the parent is null, the coordinates
	 * are absolute.
	 */
	private Component parent = null;

	/**
	 * Ability of the component to draw additional things that should be displayed alongside the component
	 * but doesn't belong to the component itself.
	 */
	private Function1<Graphics2D, Unit> additionalDrawing = null;

    /**
	 * Called when the component's dimensions have changed.
	 */
	public final Delegate<Vector> onResize = new Delegate<>();

	/**
	 * Called when any transformation has been done to the component.
	 */
	public final Delegate<TransformationEvent> onTransformed = transformation.onTransformed();

    /**
     * Called when a child has been added to this component.
     */
    public final Delegate<Component> onChildAdded = new Delegate<>();

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
		if (GameWindow.HEIGHT > 1080)
			return new Font("Consolas", Font.PLAIN, 14);
		else
			return new Font("Consolas", Font.PLAIN, 13);
	}

	/** The standard font, which is used if no special font has been set. Use it in Buttons, Lists,... */
	public static final Font STD_FONT = getSTD_FONT();

	public static final Insets STD_INSETS = new Insets(7, 7, 10, 7);

	/**
	 * Creates an empty component with a shape resembling that of a 2px wide point as a source shape.
	 */
	public Component() {
        this(new Rectangle(-1, -1, 2, 2));
	}

    public Component(Shape sourceShape) {
        mouseListeners = new LinkedList<>();
        mouseMotionListeners = new LinkedList<>();

        setSourceShape(sourceShape);

        border = new Border();
        border.setComponent(this);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent arg0) {
                if (status != ComponentStatus.MOUSE) {
                    status = ComponentStatus.MOUSE;
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if(status != ComponentStatus.CLICK) {
                    status = ComponentStatus.CLICK;
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
        setName(Integer.toString(this.hashCode()));

        onTransformed.registerJava(event -> {
            children.values().forEach(component -> event.applyTransformation(component.transformation));
            if (boundsRecalculationIssued) {
                transformationChangedSince = true;
            }
        });
    }

    /** A component initialized with this constructor will have (width = 1) and (height = 1). Use with care. It is a point.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param backing the screen, in which the component is drawn
     */
    public Component(int x, int y, Screen backing) {
        this();
        transformation.translate(x, y);
        setBackingScreen(backing);
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
		this(new Rectangle(-width / 2, -height / 2, width, height));
		transformation.translate(x, y);
		setBackingScreen(backing);
	}

	public Component(Vector initialPosition, Shape srcShape, Screen backing) {
		this(srcShape);
		transformation.translate(initialPosition.getX(), initialPosition.getY());
		setBackingScreen(backing);
	}

    public void draw(Graphics2D g) {
    }

	private boolean isBoundsRecomputeNeeded() {
		return /*visible && */(transformationChangedSince || bounds == null) && boundsRecalculationIssued;
	}

    public final void drawChecked(Graphics2D g) {
        if(isVisible()) {
            draw(g);

	        children.forEach((key, component) -> component.drawChecked(g));

            if(additionalDrawing != null) {
                additionalDrawing.apply(g);
            }
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
		return getX() - (parent != null ? parent.getX() : 0);
	}

	/**
	 * Setzt die x Position des Steuerelements.
	 *
	 * @param x Die neue x Position des Steuerelements.
	 */
	public void setX(int x) {
        setLocation(x, getY());
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
		return getY() - (parent != null ? parent.getY() : 0);
	}

	/**
	 * Setzt die y Position des Steuerelements.
	 *
	 * @param y Die neue y Position des Steuerelements.
	 */
	public void setY(int y) {
        setLocation(getX(), y);
	}

	public void setRelativeY(int y) {
		if(parent == null) throw new NullPointerException("Parent of component is null.");
		setY(y + parent.getY());
	}

    public Vector getLocation() { return new Vector(getX(), getY());
    }

	public void setLocation(int x, int y) {
		int xTranslation = x - getX(), yTranslation = y - getY();

		if(xTranslation != 0 || yTranslation != 0) {
			transformation.translate(xTranslation, yTranslation);
		}
	}

	public void setRelativeLocation(int x, int y) {
		if(parent == null) throw new NullPointerException("Parent of component is null.");
		setLocation(x + ((int) parent.getPreciseRectangle().getX()), y + ((int) parent.getPreciseRectangle().getY()));
	}

    public void setCenteredLocation(int x, int y) {
        final Point center = center();
        move(x - center.x, y - center.y);
    }

	public void move(int dx, int dy) {
		setLocation(getX() + dx, getY() + dy);
	}

	public void move(Point dp) {
		move(dp.x, dp.y);
	}

    /**
     * Returns the rotation of this component in degrees.
     * Note that not every component tends to support rotation or scaling.
     */
    public double getRotation() {
        return transformation.rotation();
    }

    public void rotateDeg(double degAngle) {
        transformation.rotate(degAngle);
    }

    public void rotateRad(double radAngle) {
        transformation.rotate(Math.toDegrees(radAngle));
    }

    public void setRotationDeg(double degAngle) {
        transformation.setRotation(degAngle);
    }

    public void setRotationRad(double radAngle) {
        transformation.setRotation(Math.toDegrees(radAngle));
    }

	public int getWidth() {
		return (int) getBounds().getBounds2D().getWidth();
	}

	public void setWidth(int width) {
        if(width == 0)
            throw new IllegalArgumentException("Illegal width of 0: Shape implosion");

		double scaleFactor = width / getPreciseRectangle().getWidth();
		//int oldWidth = (int) (srcShape.getBounds().width * transformation.scale().x());

		final double oldX = getPreciseRectangle().getX();
		final double oldY = getPreciseRectangle().getY();

		transformation.scale(scaleFactor, 1);

		final double newX = getPreciseRectangle().getX();
		final double newY = getPreciseRectangle().getY();

		final Vector newPosition = new Vector(newX, newY);
		final Vector oldPosition = new Vector(oldX, oldY);
		final Vector delta = oldPosition.difference(newPosition);

		// Truncate the result. Apparently, the JVM does rounding by it self.
		transformation.translate((int) delta.getX(), (int) delta.getY());

		onResize.apply(new Vector(width, getHeight()));
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

		final Vector newPosition = new Vector(newX, newY);
		final Vector oldPosition = new Vector(oldX, oldY);
		final Vector delta = oldPosition.difference(newPosition);

		transformation.translate((int) delta.getX(), (int) delta.getY());

		onResize.apply(new Vector(getWidth(), height));
	}

	protected void forceBoundsRecalculation() {
		bounds = generatedBounds();
	}

	protected final Shape generatedBounds() {
		return transformation.transformOriginal(srcShape);
	}

	/**
	 * Returns the current bounds for the component.
	 * The bounds are derived from the component's source shape and its transformation.
	 * However, it is possible to set special bounds for the component, which overrides the
	 * source shape and transform automatism provided by the component, allowing for arbitrary
	 * optimizations.
	 * @return The current component's bounds.
	 */
	public Shape getBounds() {
		forceBoundsRecalculation();
		if(bounds == null) throw new IllegalStateException("Component with no bounds");
		return bounds;
	}

	/**
	 * Sets the bounds for this component.
	 * When new, custom bounds are set for this component, the component assumes that
	 * the caller does not want it to recalculate bounds upon any transformation done to it.
	 * However, if the new bounds are set to be <tt>null</tt>, the component restores its behavior
	 * of automatically calculating new bounds on demand.
	 * @param bounds The new bounds to set for the component.
	 */
	public void setBounds(Shape bounds) {
		this.bounds = bounds;
		boundsRecalculationIssued = bounds == null;
		transformationChangedSince = bounds == null;
	}

	public boolean isBoundsRecalculationIssued() {
		return boundsRecalculationIssued;
	}

	public void setBoundsRecalculationIssued(boolean boundsRecalculationIssued) {
		this.boundsRecalculationIssued = boundsRecalculationIssued;
	}

	public Shape getSourceShape() {
		return srcShape;
	}

	public void setSourceShape(Shape srcShapeParam) {
		if(srcShapeParam == null) throw new NullPointerException();

        final AffineTransform resetSrcShapeTransform = AffineTransform.getTranslateInstance(
                -srcShapeParam.getBounds2D().getX() - srcShapeParam.getBounds2D().getWidth() / 2,
                -srcShapeParam.getBounds2D().getY() - srcShapeParam.getBounds2D().getHeight() / 2);

        this.srcShape = resetSrcShapeTransform.createTransformedShape(srcShapeParam);

		// Invalidate old bounds, very likely to be wrong now.
		transformationChangedSince = true;

		transformation.resetTransformation();
		transformation.setTranslationWithoutSideEffect(srcShapeParam.getBounds2D().getWidth() / 2, srcShapeParam.getBounds2D().getHeight() / 2);
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
		FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
        Rectangle2D rect = f.getStringBounds(text, frc);
		return new Dimension((int) rect.getWidth(), (int) rect.getHeight());
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

    public static Rectangle originCenteredRectangle(int w, int h) {
        return new Rectangle(-w / 2, -h / 2, w, h);
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
		} else throw new NoSuchElementException("No such element " + c);
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
        children.values().forEach(component -> component.setVisible(vvvvvv));
		if (vvvvvv) {
			if(bounds == null) forceBoundsRecalculation();
			acceptInput();
		} else {
			declineInput();
		}
	}

	/**
	 * Gets an unmodifiable view of the component's current children map
	 * @return The children of this component.
	 */
    public Map<String, Component> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public String getName() {
		return name;
	}

	/**
	 * Setzt den Namen neu.
	 *
	 * @param nameParam Der neue Name der Component.
	 */
	public void setName(String nameParam) {
		this.name = getClass().getName() + ": \"" + nameParam + "\"";
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

    private void addChild(Component component) {
        children.put(component.name, component);
        onChildAdded.apply(component);
    }

	public Component getParent() {
		return parent;
	}

	public void setParent(Component parent) {
        if(this.parent == parent) return;

		if(children.containsValue(parent)) {
			throw new IllegalArgumentException("Circular component reference involving " + this + " and " + parent);
		}

		if(this.parent != null) {
			move(-this.parent.getX(), -this.parent.getY());
            this.parent.remove(this);
		}
		this.parent = parent;
		if(this.parent != null) {
			move(this.parent.getX(), this.parent.getY());
            this.parent.addChild(this);
		}
	}

    public boolean isListenerTransparent() {
        return listenerTransparent;
    }

    public void setListenerTransparent(boolean listenerTransparent) {
        this.listenerTransparent = listenerTransparent;
    }

    public Transformation2D getTransformation() {
		return transformation;
	}

    /**
     * Copies data of given transformation to the transformation of this component without triggering
     * any listeners or events.
     * @param transformation The data to be copied to this component's transformation.
     */
	protected void reproduceTransformation(Transformation2D transformation) {
		Transformation2D.applySilently(this, transformation);
		transformationChangedSince = true;
	}

	public void resetPosition() {
		transformation.setTranslation(srcShape.getBounds().width / 2, srcShape.getBounds().height / 2);
	}

    public void unparent() {
        if(parent == null) return;
        parent.children.remove(getName());
        this.parent = null;
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
     * Returns the center point of the bounds.
     * @return The bound's center.
     */
	public Point center() {
        final Rectangle2D preciseRectangle = getPreciseRectangle();
        return new Point((int) preciseRectangle.getCenterX(), (int) preciseRectangle.getCenterY());
	}

    public Point balanceCenter() {
        java.util.List<Vector> vectorList = new LinkedList<>();
        for(PathIterator pi = getBounds().getPathIterator(null); !pi.isDone(); pi.next()) {
            final float[] coords = new float[6];
            pi.currentSegment(coords);
            vectorList.add(new Vector(coords[0], coords[1]));
        }

        float sum_x = 0f;
        float sum_y = 0f;
        for(Vector vec : vectorList) {
            sum_x += vec.getX();
            sum_y += vec.getY();
        }

        final float balancePointX = sum_x / vectorList.size();
        final float balancePointY = sum_y / vectorList.size();

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
	 * @return The chunk of code represented the additional drawing commands for the component.
	 */
	public Function1<Graphics2D, Unit> getAdditionalDrawing() {
		return additionalDrawing;
	}

	public void setAdditionalDrawing(Function1<Graphics2D, Unit> additionalDrawing) {
		this.additionalDrawing = additionalDrawing;
	}

	public void removeMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.remove(mouseWheelListener);
	}

	public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
		mouseWheelListeners.add(mouseWheelListener);
	}

}

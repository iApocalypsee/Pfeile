package comp;

import gui.Drawable;
import gui.Screen;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.List;

/**
 * Public interface for all components.
 * Implementors of {@link comp.IComponent} can create an instance of
 * {@link comp.Component} internally. In the implemented methods, they just delegate
 * to the {@link comp.Component} methods.
 * @author Josip Palavra
 * @version 22.07.2014
 */
public interface IComponent extends Drawable {

	Component.ComponentStatus getStatus();
	void setStatus(Component.ComponentStatus status);

	Screen getBackingScreen();
	void setBackingScreen(Screen screen);

	List<MouseListener> getMouseListeners();
	void addMouseListener(MouseListener mouseListener);
	void removeMouseListener(MouseListener mouseListener);

	List<MouseMotionListener> getMouseMotionListeners();
	void addMouseMotionListener(MouseMotionListener mouseMotionListener);
	void removeMouseMotionListener(MouseMotionListener mouseMotionListener);

	List<MouseWheelListener> getMouseWheelListeners();
	void addMouseWheelListener(MouseWheelListener mouseWheelListener);
	void removeMouseWheelListener(MouseWheelListener mouseWheelListener);

	int getX();
	int getY();
	int getWidth();
	int getHeight();

	void setX(int x);
	void setY(int y);
	void setWidth(int width);
	void setHeight(int height);

	Shape getBounds();
	Rectangle getSimplifiedBounds();

	void acceptInput();
	void declineInput();
	boolean isAcceptingInput();

	boolean isVisible();
	void setVisible(boolean visible);

	Border getBorder();

	boolean isMouseFocused();
	void triggerListeners(MouseEvent event);

	String getName();

}


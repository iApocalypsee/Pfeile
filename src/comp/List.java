package comp;

import gui.Screen;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

/**
 * Eine Liste, die gezeichnet werden und mit Einträgen versehen werden kann.
 * 
 * <b>17.1.2014:</b> Die List ist jetzt funktionsfähig, die Elemente von Listen können jetzt
 * eigene Strings enthalten. Das Scrolling ist aber noch nicht implementiert, deswegen mit
 * Version 17.1.2014 keine langen Listen machen.
 * <p><b>Hello</b></p>
 * hello
 * 
 * @version 19.4.2014
 */
public class List extends Component {
	
	/**
	 * Die Auflistung aller Items in der Liste.
	 */
	private LinkedList<String> items = new LinkedList<String>();
	
	/**
	 * Die Auflistung der Labels zur leicheren Handhabung.
	 */
	private LinkedList<Label> listItems = new LinkedList<Label>();
	
	/**
	 * Der (nullbasierte!) Index des Items, der gerade ganz oben angezeigt wird. Beim Runter-/Raufscrollen kann sich dieser
	 * Top Index ändern. Standardmäßig wird der oberste Eintrag zuerst gezeigt.
	 */
	private int topIndex = 0;
	
	/**
	 * Der (nullbasierte!) Index des Items, das ausgewählt wurde.
	 */
	private int selectedIndex;
	
	static final Insets STD_INSETS = new Insets(20, 10, 20, 10);
	
	/**
	 * Creates a new list.
	 * @param x The x position.
	 * @param y The y position.
	 * @param width The width.
	 * @param height The height.
	 * @param backing The backing screen.
	 * @param items The items.
	 */
	public List(int x, int y, int height, Screen backing, LinkedList<String> items) {
		super();
		
		setBackingScreen(backing);
		this.items = items;

		Dimension dim = tfits();
		int widest = dim.width;
		int heighest = dim.height;

		setX(x);
		setY(y);
		setWidth(widest + 20);
		setHeight(height);

		for (int i = 0; i < items.size(); i++) {

			final Label l = new Label(0, 0, backing, items.get(i));
			// set the position up
			l.setX(x);
			l.setY(y + heighest * i + 10);
			// only set the label visible if their bounding boxes intersect each other
			l.setVisible(l.getBounds().intersects(getX(), getY(), getWidth(), getHeight()));
			// add the mouse listener
			l.addMouseListener(new ListElementMouseHandler(l));
			// add the label to the list
			listItems.add(l);

		}
		
		/*
		
		for (int i = 0; i < items.size(); i++) {
			final Label l = new Label(0, 0, items.get(i), this);
			l.setY(l.getHeight() * i);
			if(l.getAbsoluteY() >= this.getAbsoluteY()) {
				if(l.getAbsoluteY() + l.getHeight() > this.getAbsoluteY() + this.getHeight()) {
					l.setVisible(false);
				} else {
					l.setVisible(true);
				}
			} else {
				l.setVisible(false);
			}
			
			l.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent arg0) {
					if(l.getBounds().contains(arg0.getPoint())) {
						selectedIndex = listItems.indexOf(l);
					}
				}
				
				@Override
				public void mousePressed(MouseEvent arg0) {
				}
				
				@Override
				public void mouseExited(MouseEvent arg0) {
				}
				
				@Override
				public void mouseEntered(MouseEvent arg0) {
				}
				
				@Override
				public void mouseClicked(MouseEvent arg0) {
				}
			});
//			
//			l.addMouseMotionListener(new MouseMotionListener() {
//				
//				@Override
//				public void mouseMoved(MouseEvent e) {
//					if(l.getBounds().contains(e.getPoint())) {
//					}
//				}
//				
//				@Override
//				public void mouseDragged(MouseEvent e) {
//				}
//			});
			listItems.add(l);
		}
		
		*/
		
		
	}

	@Override
	public void draw(Graphics2D g) {

		getBorder().draw(g);

		for (int i = getFirstDisplayIndex(); i < getLastDisplayIndex(); i++) {
			listItems.get(i).draw(g);
		}

	}
	
	/**
	 * Berechnet die Bounding Box von jedem Text in der Liste und gibt die größte Bounding Box zurück.
	 * @return
	 */
	Dimension tfits() {
		int width = 0, height = 0;
		for (String s : items) {
			Dimension bounds = Component.getTextBounds(s, STD_FONT);
			if(bounds.width > width) {
				width = bounds.width;
			}
			if(bounds.height > height) {
				height = bounds.height;
			}
		}
		return new Dimension(width, height);
	}
	
	/**
	 * Gibt den Index zur�ck, der noch dargestellt werden kann.
	 * @return
	 */
	int getLastDisplayIndex() {
		
		int ldi = 0;
		
		for(; ldi < listItems.size(); ldi++) {
			if(listItems.get(ldi).getAbsoluteY() > this.getAbsoluteY() + this.getHeight()) {
				ldi--;
				break;
			}
		}
		
		return ldi;
		
	}
	
	/**
	 * Das Gegenteil von {@link List#getLastDisplayIndex()}
	 * @return
	 */
	int getFirstDisplayIndex() {
		int fdi = 0;
		
		for(; fdi < listItems.size(); fdi++) {
			if(listItems.get(fdi).getAbsoluteY() >= this.getAbsoluteY()) {
				break;
			}
		}
		
		return fdi;
	}
	
	/**
	 * Gibt den derzeit ausgew�hlten Listenindex zur�ck. Der ausgew�hlte Listenindex
	 * wird mit Mausklick bestimmt.
	 * @return Den Index des ausgew�hlten Listeneintrags.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	/**
	 * Gibt den Index zur�ck, der ganz oben in der Liste noch dargestellt werden kann.
	 * @return
	 */
	public int getTopIndex() {
		return topIndex;
	}
	
	/**
	 * Scrollt die Liste.
	 * @param delta Die Anzahl an Listeneintr�gen, die rauf- bzw. runtergescrollt werden sollen.
	 * Negative Werte m�glich.
	 * @deprecated Noch nicht benutzen, die Funktionalit�t ist noch nicht ordentlich implementiert, 
	 * Koordinatenverf�lschungen von Steuerelementen m�glich.
	 */
	public void scroll(int delta) {
		topIndex += delta;
	}

	private class ListElementMouseHandler implements MouseListener {

		private Label handling;

		private ListElementMouseHandler(Label label) {
			handling = label;
		}

		/**
		 * Invoked when the mouse button has been clicked (pressed
		 * and released) on a component.
		 *
		 * @param e
		 */
		@Override
		public void mouseClicked(MouseEvent e) {

		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 *
		 * @param e
		 */
		@Override
		public void mousePressed(MouseEvent e) {

		}

		/**
		 * Invoked when a mouse button has been released on a component.
		 *
		 * @param e
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			selectedIndex = listItems.indexOf(handling);
		}

		/**
		 * Invoked when the mouse enters a component.
		 *
		 * @param e
		 */
		@Override
		public void mouseEntered(MouseEvent e) {

		}

		/**
		 * Invoked when the mouse exits a component.
		 *
		 * @param e
		 */
		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

}

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
 * @version 17.1.2014
 * @tag.requires Component: 17.1.2014 oder höher; 
 */
public class List extends Component {
	
	/**
	 * Die Auflistung aller Items in der Liste.
	 */
	private java.util.List<String> items = new LinkedList<String>();
	
	/**
	 * Die Auflistung der Labels zur leicheren Handhabung.
	 */
	private java.util.List<Label> listItems = new LinkedList<Label>();
	
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

	public List() {
		super();
	}
	
	

	public List(int x, int y, int width, int height, Screen backing, java.util.List<String> items) {
		super(x, y, width, height, backing);
		
		this.items = items;
		
		tfits();
		
		for (int i = 0; i < items.size(); i++) {
			final Label l = new Label(0, 0, items.get(i), this);
			if (items.size() == 0) 
				l.setY(l.getHeight() * i);
			else 
				l.setY(l.getHeight() * i + 1);
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
	}

	@Override
	public void draw(Graphics2D g) {
		
		if(isVisible()) {
			
			getBorder().draw(g);
			
			for (int i = getFirstDisplayIndex(); i < getLastDisplayIndex(); i++) {
				listItems.get(i).draw(g);
			}
		}
	}
	
	/**
	 * Berechnet die Bounding Box von jedem Text in der Liste und gibt die größte Bounding Box zurück.
	 * @return
	 */
	Dimension tfits() {
		int width = 0, height = 0;
		for (String s : items) {
			Dimension bounds = new Dimension(getTextBounds(s, STD_FONT).width + 1, getTextBounds(s, STD_FONT).height + 1);
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
	
	/** setzt, ob das Rechteck rund ist oder nicht */
	public void setRoundBorder(boolean isRoundRect) {
		getBorder().setRoundedBorder(isRoundRect);
	}
	
	/** gibt zur�ck, ob das Rechteck (Border) das gezeichnet werden soll, rund ist oder nicht */
	public boolean isRoundBorder () {
		return getBorder().isRoundedBorder();
	}
	
	/**
	 * Veranlasst die komplette Liste, wieder Input zu akzeptieren.
	 * @see declineInput
	 */
	@Override
	public void acceptInput() {
		
		for (Label label : listItems) {
			label.acceptInput();
		}
		
		super.acceptInput(); 
	}
	
	/**
	 * Veranlasst das die Liste, keinen Input mehr zu akzeptieren.
	 * @see acceptInput
	 */
	@Override
	public void declineInput() {
		for (Label label : listItems) {
			label.declineInput();
		}
		
		super.declineInput(); 
	}
}

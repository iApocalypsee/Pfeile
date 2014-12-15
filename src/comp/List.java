package comp;

import gui.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * Eine Liste, die gezeichnet werden und mit Einträgen versehen werden kann.
 * 
 * <b>17.1.2014:</b> Die List ist jetzt funktionsfähig, die Elemente von Listen können jetzt
 * eigene Strings enthalten. Das Scrolling ist aber noch nicht implementiert, deswegen mit
 * Version 17.1.2014 keine langen Listen machen.
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
	 * Der (nullbasierte!) Index des Items, das ausgewählt wurde. Standardmäßig auf 0.
	 */
	private int selectedIndex = 0;

	static final Insets STD_INSETS = new Insets(5, 8, 6, 8);

	public List(int x, int y, int width, int height, Screen backing, java.util.List<String> items) {
		super(x, y, width, height, backing);
		
		this.items = items;

		for (int i = 0; i < items.size(); i++) {
			final Label build = new Label(x, y, backing, items.get(i));

			// I need to set the position in the correct order.
			build.setY(build.getY() + (build.getHeight() + 1) * i);
            // The loop wants that.

			// If the label is inside the boundaries of the list, then it should be visible...
			build.setVisible((build.getY() + build.getHeight()) < (this.getY() + this.getHeight()));

			// Every appended label should have a listener attached to it so that I know
			// when a list element has been pressed.
			build.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if(build.getSimplifiedBounds().contains(e.getPoint())) {
						selectedIndex = listItems.indexOf(build);
					}
				}
			});
			listItems.add(build);
		}
	}

    public List(int x, int y, Screen backing, java.util.List<String> items) {
        super(x, y, 200, 200, backing);

        this.items = items;

        setWidth(tfits().width + STD_INSETS.left + STD_INSETS.right);
        setHeight(tfits().height + STD_INSETS.bottom + STD_INSETS.top);

        y++;

        for (int i = 0; i < items.size(); i++) {
            final Label build = new Label(x, y, backing, items.get(i));

            // I need to set the position in the correct order.
            if (i != 0)
                build.setY(build.getY() + (build.getHeight() + 1) * i);
            // The loop wants that.

            // If the label is inside the boundaries of the list, then it should be visible...
            build.setVisible((build.getY() + build.getHeight()) < (this.getY() + this.getHeight()));

            // Every appended label should have a listener attached to it so that I know
            // when a list element has been pressed.
            build.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(build.getSimplifiedBounds().contains(e.getPoint())) {
                        selectedIndex = listItems.indexOf(build);
                    }
                }
            });
            listItems.add(build);
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
	 * Berechnet die Höhe und Breite der Liste.
	 */
    protected Dimension tfits() {
		int width = 0, height = 0;
		for (String s : items) {
			Dimension bounds = new Dimension(getTextBounds(s, STD_FONT).width + 1, getTextBounds(s, STD_FONT).height + 1);
			if (width < bounds.width)
                width = bounds.width;
            height = height + bounds.height;
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
			if(listItems.get(ldi).getY() > this.getY() + this.getHeight()) {
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
			if(listItems.get(fdi).getY() >= this.getY()) {
				break;
			}
		}
		
		return fdi;
	}
	
	/**
	 * Gibt den derzeit ausgew�hlten Listenindex zur�ck. Der ausgew�hlte Listenindex
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
	 * @see #declineInput
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
	 * @see #acceptInput
	 */
	@Override
	public void declineInput() {
		for (Label label : listItems) {
			label.declineInput();
		}
		
		super.declineInput(); 
	}

    /** setzt den ausgewählten Wert auf index */
    public void setSelectedIndex (int index) {
        selectedIndex = index;
    }

    @Override
    public void triggerListeners (MouseEvent e) {
        super.triggerListeners(e);
        for (Label label : listItems)
            label.triggerListeners(e);
    }
}

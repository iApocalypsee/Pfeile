package comp;

import general.Delegate;
import gui.screen.Screen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Eine Liste, die gezeichnet werden und mit Einträgen versehen werden kann.
 *
 * @version compare with GitHub for further details.
 */
public class List extends Component {
	
	/**
	 * Die Auflistung aller Items in der Liste.
	 */
	private java.util.List<String> items = new LinkedList<>();
	
	/**
	 * Die Auflistung der Labels zur leicheren Handhabung.
	 */
	private java.util.List<Label> listItems = new LinkedList<>();
	
	/**
	 * Der (nullbasierte!) Index des Items, der gerade ganz oben angezeigt wird. Beim Runter-/Raufscrollen kann sich dieser
	 * Top Index ändern. Standardmäßig wird der oberste Eintrag zuerst gezeigt.
	 */
	private int topIndex = 0;
	
	/**
	 * Der (nullbasierte!) Index des Items, das ausgewählt wurde. Standardmäßig auf 0.
	 */
	private int selectedIndex = 0;

	public final Delegate.Delegate<Integer> onItemSelected = new Delegate.Delegate<>();

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
					selectedIndex = listItems.indexOf(build);
					onItemSelected.apply(selectedIndex);
				}
			});
			listItems.add(build);
		}
	}

    public List(int x, int y, Screen backing, java.util.List<String> items) {
        this(x, y, tfits_static(items).width + STD_INSETS.left + STD_INSETS.right, tfits_static(items).height + STD_INSETS.bottom + STD_INSETS.top, backing, items);
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
	 * Returns a Dimension with the bounds of the list calculated from the labels with STD_INSETS.
     * The bounds returned by this list should always be equal or smaller to current bounds.
     *
     * @see comp.List#tfits_static(java.util.List)
	 */
    protected Dimension tfits() {
		int width = 0, height = 0;
		for (Label label : listItems) {
			Dimension bounds = new Dimension(label.getWidth(), label.getHeight());
			if (width < bounds.width)
                width = bounds.width;
            height = height + bounds.height + 1;
		}
        width = width + STD_INSETS.right + STD_INSETS.left;
        height = height + STD_INSETS.bottom + STD_INSETS.top;

		return new Dimension(width, height);
	}

    /**
     * Notice the difference to {@link List#tfits()}: No STD_INSETS are added, a list of strings is used instead of labels,
     * which only allows the {@link comp.Component#STD_FONT} to be used without BufferedImage.
     *
     * @param elems the list
     * @return a dimension, which would contain the list [as labels] without {@link comp.List#STD_INSETS}.
     */
	static Dimension tfits_static(java.util.List<String> elems) {
		int width = 0, height = 0;
		for (String s : elems) {
			Dimension bounds = new Dimension(getTextBounds(s, STD_FONT).width + 1, getTextBounds(s, STD_FONT).height + 1);
			if (width < bounds.width)
				width = bounds.width;
			height = height + bounds.height + 1;
		}
		return new Dimension(width, height);
	}
	
	/**
	 * @return the last index which can be displayed.
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
	 * @return the opposite of {@link List#getLastDisplayIndex()}
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

	public void appendListenerToLabels(MouseListener listener) {
		for(Label label : listItems) {
			label.addMouseListener(listener);
		}
	}

    /**
     * Sets <code>image</code> to the label with the index <code>index</code> and recalculates the bounds of the list.
     *
     * @param index the index of the entry. You may use {@link comp.List#getIndex(String)} to find the index.
     * @param image the image to iconify the label.
     */
    public void iconify (int index, BufferedImage image) {
        listItems.get(index).iconify(image);

        Dimension bounds = tfits();
        if (getWidth() < bounds.width)
            setWidth(bounds.width);
        if (getHeight() < bounds.height)
            setHeight(bounds.height);
    }

    /** Returns the size [= length] of the list. The last index is <code>getIndexSize() - 1</code> as the index is 0-based.*/
    public int getIndexSize () {
        return items.size();
    }

    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if this list
     * does not contain the element.
     *
     * @param element the name of label to search for
     * @return the index or -1
     * */
    public int getIndex (String element) {
        return items.indexOf(element);
    }

    /**
     * Returns true if this list contains the specified element.
     *
     * @param element the element to search for
     * @return <code>true</code> - if one of the labels' name is <code>element</code>
     */
    public boolean contains (String element) {
        return items.contains(element);
    }
}

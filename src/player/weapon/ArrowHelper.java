package player.weapon;

import general.Main;
import gui.ImageHelper;
import newent.InventoryLike;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Daniel
 * @version 11.05.2014
 */
public final class ArrowHelper {
	
	private static BufferedImage [] arrowImages;

    /**
     * the number of different types of arrows. It's equal 8.
     */
    public static final int NUMBER_OF_ARROW_TYPES = 8;

    /** Don't instance ArrowHelper, only if you want to initialize ArrowHelper at the beginning
     * (before using {@link player.weapon.ArrowHelper#getArrowImage(int)}
     */
    public ArrowHelper() {
		arrowImages = new BufferedImage[NUMBER_OF_ARROW_TYPES];
		arrowImages[FireArrow.INDEX] = new FireArrow().getImage();
		arrowImages[WaterArrow.INDEX] = new WaterArrow().getImage();
		arrowImages[StormArrow.INDEX] = new StormArrow().getImage();
		arrowImages[StoneArrow.INDEX] = new StoneArrow().getImage();
		arrowImages[IceArrow.INDEX] = new IceArrow().getImage();
		arrowImages[LightningArrow.INDEX] = new LightningArrow().getImage();
		arrowImages[LightArrow.INDEX] = new LightArrow().getImage();
		arrowImages[ShadowArrow.INDEX] = new ShadowArrow().getImage();
	}
	
	/**
	 * Compares the given string with all known arrow classes and passes corresponding
	 * class object back.
	 * @param selectedArrow The arrow string.
	 * @return A class object, or null, if no string matches.
	 */
	public static Class<? extends AbstractArrow> reformArrow(String selectedArrow) {
        switch (selectedArrow) {
            case FireArrow.NAME:     return FireArrow.class;
            case WaterArrow.NAME:    return WaterArrow.class;
            case StormArrow.NAME:    return StormArrow.class;
            case StoneArrow.NAME:    return StoneArrow.class;
            case IceArrow.NAME:      return IceArrow.class;
            case LightningArrow.NAME:return LightningArrow.class;
            case LightArrow.NAME:    return LightArrow.class;
            case ShadowArrow.NAME:   return ShadowArrow.class;
            default:
                return null;
        }
	}

	/** Methode vergleicht den �bergebenen int - Wert (der PfeilIndex)
	 *   '...Arrow.INDEX' und gibt das jeweilige class-Object zur�ck '...Arrow.class' *
	 *  @see <code> reformArrow(String selectedArrow) </code>*/
	public static Class<? extends AbstractArrow> reformArrow(int selectedArrowIndex) {

		switch (selectedArrowIndex) {
		case FireArrow.INDEX:
			return FireArrow.class;
		case WaterArrow.INDEX:
			return WaterArrow.class;
		case StormArrow.INDEX:
			return StormArrow.class;
		case StoneArrow.INDEX:
			return StoneArrow.class;
		case IceArrow.INDEX:
			return IceArrow.class;
		case LightningArrow.INDEX:
			return LightningArrow.class;
		case LightArrow.INDEX:
			return LightArrow.class;
		case ShadowArrow.INDEX:
			return ShadowArrow.class;
		default:
			return null;
		}
	}

	/** Methode vergleicht den �bergebenen int - Wert (der PfeilIndex)
	 *   '...Arrow.INDEX' und gibt den jeweiligen Namen des Pfeils: '...Arrow.NAME' zur�ck
	 *  @see <code> reformArrow(String selectedArrow) </code>*/
	public static String arrowIndexToName(int selectedArrowIndex) {
		switch (selectedArrowIndex) {
		case FireArrow.INDEX:
			return FireArrow.NAME;
		case WaterArrow.INDEX:
			return WaterArrow.NAME;
		case StormArrow.INDEX:
			return StormArrow.NAME;
		case StoneArrow.INDEX:
			return StoneArrow.NAME;
		case IceArrow.INDEX:
			return IceArrow.NAME;
		case LightningArrow.INDEX:
			return LightningArrow.NAME;
		case LightArrow.INDEX:
			return LightArrow.NAME;
		case ShadowArrow.INDEX:
			return ShadowArrow.NAME;
		default:
			return null;
		}
	}

    /** Methode vergleicht den übergebenen int - Wert (der PfeilIndex)
     *   '...Arrow.INDEX' und erzeugt eine neue Instanz der Klasse
     *   Bei keiner Übereinstimmung wird <code> null </code> zurückgeben.
     *
     *  @see <code> reformArrow(String selectedArrow).newInstance() </code>
     *  @see <code> instanceArrow(String selectedArrow) </code>*/
    public static AbstractArrow instanceArrow (int selectedArrowIndex) {

        switch (selectedArrowIndex) {
            case FireArrow.INDEX:
                return new FireArrow();
            case WaterArrow.INDEX:
                return new WaterArrow();
            case StormArrow.INDEX:
                return new StormArrow();
            case StoneArrow.INDEX:
                return new StoneArrow();
            case IceArrow.INDEX:
                return new IceArrow();
            case LightningArrow.INDEX:
                return new LightningArrow();
            case LightArrow.INDEX:
                return new LightArrow();
            case ShadowArrow.INDEX:
                return new ShadowArrow();
            default:
                return null;
        }
    }

    /** Methode vergleicht den übergebenen String - Wert (der Pfeilname: Arrow.NAME)
     *   und erzeugt eine neue Instanz der Klasse dieser Klasse.
     *   Bei keiner Übereinstimmung wird <code> null </code> zurückgeben.
     *
     *  @see <code> reformArrow(String selectedArrow).newInstance() </code>
     *  @see <code> instanceArrow(String selectedArrow) </code>*/
    public static AbstractArrow instanceArrow (String selectedArrowName) {
        switch (selectedArrowName) {
            case FireArrow.NAME:     return new FireArrow();
            case WaterArrow.NAME:    return new WaterArrow();
            case StormArrow.NAME:    return new StormArrow();
            case StoneArrow.NAME:    return new StoneArrow();
            case IceArrow.NAME:      return new IceArrow();
            case LightningArrow.NAME:return new LightningArrow();
            case LightArrow.NAME:    return new LightArrow();
            case ShadowArrow.NAME:   return new ShadowArrow();
            default:
                return null;
        }
    }

    /** Gleicht <code>arrow.newInstance()</code> hat aber den Exeption Block hier drin, sodass es bei Bedingungen verwendet
     * werden kann.
     * @param arrow the class of the Arrow, which is extends AbstractArrow
     * @return the instanced Arrow
     */
    public static AbstractArrow instanceArrow (Class<? extends AbstractArrow> arrow) {
        try {
            return arrow.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

	/** Methode vergleicht den �bergebenen Pfeilnamen
	 *   '...Arrow.NAME' und gibt den jeweiligen Index des Pfeils: '...Arrow.INDEX' zur�ck;
	 *   wenn der Pfeilname nicht existiert: -1
	 *  @see <code> arrowIndexToName (String selectedArrowIndex) </code>*/
	public static int arrowNameToIndex(String selectedArrow) {
        switch (selectedArrow) {
            case FireArrow.NAME:     return FireArrow.INDEX;
            case WaterArrow.NAME:    return WaterArrow.INDEX;
            case StormArrow.NAME:    return StormArrow.INDEX;
            case StoneArrow.NAME:    return StoneArrow.INDEX;
            case IceArrow.NAME:      return IceArrow.INDEX;
            case LightningArrow.NAME:return LightningArrow.INDEX;
            case LightArrow.NAME:    return LightArrow.INDEX;
            case ShadowArrow.NAME:   return ShadowArrow.INDEX;
            default:
                return -1;
        }
	}

    /** If you want to know how much arrows per category are in the inventory of the ActivePlayer, use this method.
     * use it like <code>arrowCountInventory()[FireArrow.INDEX]</code> or any other Arrow.INDEX.
     * <p>
     * If you're thinking of using this method frequently without chaning the number of arrows, you should save the
     * value in an tempory <code>final int[] temp = arrowCountInventory();</code> (for example if you want to use it for
     * each arrow once).
     *
     * @return int[] - an array of the size <code>ArrowHelper.NUMBER_OF_ARROW_TYPES</code>
     * (it's 8 like the number of kinds of arrows).
     */
    public static int[] arrowCountInventory () {
        final InventoryLike inventory = Main.getContext().getActivePlayer().inventory();

        int [] arrowsCount = new int[NUMBER_OF_ARROW_TYPES];
        for (Item item : inventory.javaItems()) {
            if (item instanceof FireArrow)
                arrowsCount[FireArrow.INDEX]++;
            else if (item instanceof WaterArrow)
                arrowsCount[WaterArrow.INDEX]++;
            else if (item instanceof StormArrow)
                arrowsCount[StormArrow.INDEX]++;
            else if (item instanceof StoneArrow)
                arrowsCount[StoneArrow.INDEX]++;
            else if (item instanceof IceArrow)
                arrowsCount[IceArrow.INDEX]++;
            else if (item instanceof LightningArrow)
                arrowsCount[LightningArrow.INDEX]++;
            else if (item instanceof LightArrow)
                arrowsCount[LightArrow.INDEX]++;
            else if (item instanceof ShadowArrow)
                arrowsCount[ShadowArrow.INDEX]++;
        }
        return arrowsCount;
    }

    /** returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     * @param arrowIndex the index of the arrow like anyArrow.ARROW_INDEX
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor (int arrowIndex) {
        switch (arrowIndex) {
            case FireArrow.INDEX:  return FireArrow.UNIFIED_COLOR;
            case WaterArrow.INDEX: return WaterArrow.UNIFIED_COLOR;
            case StoneArrow.INDEX: return StoneArrow.UNIFIED_COLOR;
            case StormArrow.INDEX: return StormArrow.UNIFIED_COLOR;
            case LightArrow.INDEX: return LightArrow.UNIFIED_COLOR;
            case ShadowArrow.INDEX:return ShadowArrow.UNIFIED_COLOR;
            case IceArrow.INDEX:   return IceArrow.UNIFIED_COLOR;
            case LightningArrow.INDEX: return LightningArrow.UNIFIED_COLOR;
            default:
                return null;
        }
    }

    /** returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     * @param arrowClass Any class extends AbstractArrow
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor (Class <? extends AbstractArrow> arrowClass) {
        if (arrowClass.equals(FireArrow.class))
            return FireArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(WaterArrow.class))
            return WaterArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(StoneArrow.class))
            return StoneArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(StormArrow.class))
            return StormArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(IceArrow.class))
            return IceArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(LightningArrow.class))
            return LightningArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(LightArrow.class))
            return LightArrow.UNIFIED_COLOR;
        else if (arrowClass.equals(ShadowArrow.class))
            return ShadowArrow.UNIFIED_COLOR;
        else
            return null;
    }

    /** returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     * @param arrowName the name of the arrow like anyArrow.ARROW_NAME
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor (String arrowName) {
        return getUnifiedColor(ArrowHelper.arrowNameToIndex(arrowName));
    }

	/** gibt ein Bild des Pfeils des Indexes <code> selectedArrow </code> zur�ck;
     * @see player.weapon.ArrowHelper#getArrowImage(int, float) */
	public static BufferedImage getArrowImage (int selectedArrow) {
		return arrowImages[selectedArrow];
	}

    /** Returns the scaled version of a BufferedImage from the selected arrow. Compare with the methods in {@link gui.ImageHelper}, too.
     * If you want to use this method regulary, save the returned BufferedImage somewhere, because scaling an image is no fast Operation or use
     * {@link gui.ImageHelper#scaleBufferedImage(java.awt.image.BufferedImage, float, float, int)} with {@link java.awt.Image#SCALE_FAST}.
     *
     * @param selectedArrow the index of the selected arrow arrow.INDEX
     * @param scaleFactor the scale Factor in x and in y direction
     * @return the scaled image of the arrow.INDEX or use {@link player.weapon.ArrowHelper#arrowNameToIndex(String)} (ArrowHelper.arrowNameToIndex(arrow.getName()))
     * @see player.weapon.ArrowHelper#getArrowImage(int)
     */
    public static BufferedImage getArrowImage (int selectedArrow, float scaleFactor) {
        return ImageHelper.scaleBufferedImage(getArrowImage(selectedArrow), scaleFactor);
    }
}

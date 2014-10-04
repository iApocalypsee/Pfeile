package player.weapon;

import general.Main;
import gui.GameScreen;
import newent.InventoryLike;
import sun.security.provider.SHA;

import java.awt.image.BufferedImage;

/**
 * @author Daniel
 * @version 11.05.2014
 */
public final class ArrowHelper {
	
	private static BufferedImage [] arrowImages;

    /**
     * the number of diffrent typs of arrows. It's equal 8.
     */
    public static final int NUMBER_OF_ARROW_TYPES = 8;

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
		if (selectedArrow.equals(FireArrow.NAME)) {
			return FireArrow.class;
		} else if (selectedArrow.equals(WaterArrow.NAME)) {
			return WaterArrow.class;
		} else if (selectedArrow.equals(StormArrow.NAME)) {
			return StormArrow.class;
		} else if (selectedArrow.equals(StoneArrow.NAME)) {
			return StoneArrow.class;
		} else if (selectedArrow.equals(IceArrow.NAME)) {
			return IceArrow.class;
		} else if (selectedArrow.equals(LightningArrow.NAME)) {
			return LightningArrow.class;
		} else if (selectedArrow.equals(LightArrow.NAME)) {
			return LightArrow.class;
		} else if (selectedArrow.equals(ShadowArrow.NAME)) {
			return ShadowArrow.class;
		} else {
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
        if (selectedArrowName.equals(FireArrow.NAME)) {
            return new FireArrow();
        } else if (selectedArrowName.equals(WaterArrow.NAME)) {
            return new WaterArrow();
        } else if (selectedArrowName.equals(StormArrow.NAME)) {
            return new StormArrow();
        } else if (selectedArrowName.equals(StoneArrow.NAME)) {
            return new StoneArrow();
        } else if (selectedArrowName.equals(IceArrow.NAME)) {
            return new IceArrow();
        } else if (selectedArrowName.equals(LightningArrow.NAME)) {
            return new LightningArrow();
        } else if (selectedArrowName.equals(LightArrow.NAME)) {
            return new LightArrow();
        } else if (selectedArrowName.equals(ShadowArrow.NAME)) {
            return new ShadowArrow();
        } else {
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
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

	/** Methode vergleicht den �bergebenen Pfeilnamen
	 *   '...Arrow.NAME' und gibt den jeweiligen Index des Pfeils: '...Arrow.INDEX' zur�ck;
	 *   wenn der Pfeilname nicht existiert: -1
	 *  @see <code> arrowIndexToName (String selectedArrowIndex) </code>*/
	public static int arrowNameToIndex(String selectedArrow) {
		if (selectedArrow.equals(FireArrow.NAME)) {
			return FireArrow.INDEX;
		} else if (selectedArrow.equals(WaterArrow.NAME)) {
			return WaterArrow.INDEX;
		} else if (selectedArrow.equals(StormArrow.NAME)) {
			return StormArrow.INDEX;
		} else if (selectedArrow.equals(StoneArrow.NAME)) {
			return StoneArrow.INDEX;
		} else if (selectedArrow.equals(IceArrow.NAME)) {
			return IceArrow.INDEX;
		} else if (selectedArrow.equals(LightningArrow.NAME)) {
			return LightningArrow.INDEX;
		} else if (selectedArrow.equals(LightArrow.NAME)) {
			return LightArrow.INDEX;
		} else if (selectedArrow.equals(ShadowArrow.NAME)) {
			return ShadowArrow.INDEX;
		} else {
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
	
	/** gibt ein Bild des Pfeils des Indexes <code> selectedArrow </code> zur�ck; */
	public static BufferedImage getArrowImage (int selectedArrow) {
		return arrowImages[selectedArrow];
	}
}

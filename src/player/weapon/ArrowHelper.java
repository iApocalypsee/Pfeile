package player.weapon;

/**
 * @author Josip
 * @version 11.05.2014
 */
public final class ArrowHelper {
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

	/** Methode vergleicht den Übergebenen int - Wert (der PfeilIndex)
	 *   '...Arrow.INDEX' und gibt das jeweilige class-Object zurück '...Arrow.class' *
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

	/** Methode vergleicht den Übergebenen int - Wert (der PfeilIndex)
	 *   '...Arrow.INDEX' und gibt den jeweiligen Namen des Pfeils: '...Arrow.NAME' zurück
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

	/** Methode vergleicht den Übergebenen Pfeilnamen
	 *   '...Arrow.NAME' und gibt den jeweiligen Index des Pfeils: '...Arrow.INDEX' zurück;
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
}

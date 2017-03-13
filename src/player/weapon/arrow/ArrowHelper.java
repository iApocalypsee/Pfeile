package player.weapon.arrow;

import general.LogFacility;
import misc.ImageHelper;
import newent.InventoryLike;
import player.item.Item;

import java.awt.*;
import java.awt.image.BufferedImage;
/**
 * <code>ArrowHelper</code> provides useful methods for the arrow classes. It contains a lot of different classes for
 * comparisons (e.g. for getting indexes, classes and names from each other), provides the arrow-images and lot's of
 * other helpful stuff.
 */
public final class ArrowHelper {

    private static AbstractArrow[] arrows;

    /**
     * the number of different types of arrows. It's equal 8.
     */
    public static final int NUMBER_OF_ARROW_TYPES = 8;

    /**
     * Don't instance ArrowHelper, only if you want to initialize ArrowHelper at the beginning
     * (before using {@link ArrowHelper#getArrowImage(int)}
     */
    public ArrowHelper() {
        arrows = new AbstractArrow[NUMBER_OF_ARROW_TYPES];
        arrows[FireArrow.INDEX] = new FireArrow();
        arrows[WaterArrow.INDEX] = new WaterArrow();
        arrows[StormArrow.INDEX] = new StormArrow();
        arrows[StoneArrow.INDEX] = new StoneArrow();
        arrows[IceArrow.INDEX] = new IceArrow();
        arrows[LightningArrow.INDEX] = new LightningArrow();
        arrows[LightArrow.INDEX] = new LightArrow();
        arrows[ShadowArrow.INDEX] = new ShadowArrow();

        LogFacility.log("Arrow images loaded.", "Info", "init process");
    }

    /**
     * Compares the given string with all known arrow classes and passes corresponding
     * class object back. Not only the coding name of the arrow (getName()), but also the displayed name
     * is tested.
     *
     * @param selectedArrow The name of the arrow (getName() and getNameDisplayed() both are possible)
     * @return A class object, or null, if no string matches.
     */
    public static Class<? extends AbstractArrow> reformArrow (String selectedArrow) {
        if (selectedArrow.equals(arrows[FireArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[FireArrow.INDEX].getNameDisplayed()))
            return FireArrow.class;

        else if (selectedArrow.equals(arrows[WaterArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[WaterArrow.INDEX].getNameDisplayed()))
            return WaterArrow.class;

        else if (selectedArrow.equals(arrows[StoneArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[StoneArrow.INDEX].getNameDisplayed()))
            return StoneArrow.class;

        else if (selectedArrow.equals(arrows[StormArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[StormArrow.INDEX].getNameDisplayed()))
            return StormArrow.class;

        else if (selectedArrow.equals(arrows[IceArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[IceArrow.INDEX].getNameDisplayed()))
            return IceArrow.class;

        else if (selectedArrow.equals(arrows[LightningArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[LightningArrow.INDEX].getNameDisplayed()))
            return LightningArrow.class;

        else if (selectedArrow.equals(arrows[LightArrow.INDEX].getName()) ||
                 selectedArrow.equals(arrows[LightArrow.INDEX].getNameDisplayed()))
            return LightArrow.class;

        else
            return ShadowArrow.class;
    }

    /** This method returns the name of arrow with the <code>selectedArrowIndex</code> (e.g. FireArrow.INDEX).
     * If <code>isCodingName</code> is set to be <code>true</code>, the unique coding name of the arrow (defined in
     * item as <code>getName()</code>) is returned. If <code>false</code> is choosen, the returned name
     * is the name displayed for the user (defined as <code>getNameDisplayed()</code> in <code>Item</code>.
     *
     * @param selectedArrowIndex the index of arrow (e.g. StoneArrow.INDEX)
     * @param isCodingName true, if the returned value should be the unique coding name (getName()); false
     *                     for the displayed name
     * @return the name of arrow
     */
    public static String arrowIndexToName (int selectedArrowIndex, boolean isCodingName) {
        switch (selectedArrowIndex) {
            case FireArrow.INDEX:
                if (isCodingName)
                    return arrows[FireArrow.INDEX].getName();
                else
                    return arrows[FireArrow.INDEX].getNameDisplayed();

            case WaterArrow.INDEX:
                if (isCodingName)
                    return arrows[WaterArrow.INDEX].getName();
                else
                    return arrows[WaterArrow.INDEX].getNameDisplayed();

            case StormArrow.INDEX:
                if (isCodingName)
                    return arrows[StormArrow.INDEX].getName();
                else
                    return arrows[StormArrow.INDEX].getNameDisplayed();

            case StoneArrow.INDEX:
                if (isCodingName)
                    return arrows[StoneArrow.INDEX].getName();
                else
                    return arrows[StoneArrow.INDEX].getNameDisplayed();

            case IceArrow.INDEX:
                if (isCodingName)
                    return arrows[IceArrow.INDEX].getName();
                else
                    return arrows[IceArrow.INDEX].getNameDisplayed();

            case LightningArrow.INDEX:
                if (isCodingName)
                    return arrows[LightningArrow.INDEX].getName();
                else
                    return arrows[LightningArrow.INDEX].getNameDisplayed();

            case LightArrow.INDEX:
                if (isCodingName)
                    return arrows[LightArrow.INDEX].getName();
                else
                    return arrows[LightArrow.INDEX].getNameDisplayed();

            default:
                if (isCodingName)
                    return arrows[ShadowArrow.INDEX].getName();
                else
                    return arrows[ShadowArrow.INDEX].getNameDisplayed();
        }
    }

    /**
     * Methode vergleicht den übergebenen int - Wert (der PfeilIndex)
     * '...Arrow.INDEX' und erzeugt eine neue Instanz der Klasse
     * Bei keiner Übereinstimmung wird <code> null </code> zurückgeben.
     *
     * @see <code> reformArrow(String selectedArrow).newInstance() </code>
     * @see <code> instanceArrow(String selectedArrow) </code>
     */
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
            default:
                return new ShadowArrow();
        }
    }

    /**
     * Gleicht <code>arrow.newInstance()</code> hat aber den Exeption Block hier drin, sodass es bei Bedingungen verwendet
     * werden kann.
     *
     * @param arrow the class of the Arrow, which is extends AbstractArrow
     * @return the instanced Arrow, or null, if there has been an Exception thrown from JAVA intern classes.
     */
    public static AbstractArrow instanceArrow(Class<? extends AbstractArrow> arrow) {
        try {
            return arrow.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** The name of arrows (both the unique coding name and the displayed user name are possible parameters) is used to
     * determine the index of that error. For example, if <code>arrowNameToIndex("FireArrow")</code> or
     * <code>arrowNameToIndex("Feuerpfeil")</code> (if language is set to German), the method will return
     * <code>FireArrow.INDEX</code>
     *
     * @param selectedArrow the name of the arrow (both getName() and getNameDisplayed() are possible)
     * @return the index of that arrow
     */
    public static int arrowNameToIndex (String selectedArrow) {
        if (selectedArrow.equals(arrows[FireArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[FireArrow.INDEX].getNameDisplayed()))
            return FireArrow.INDEX;

        else if (selectedArrow.equals(arrows[WaterArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[WaterArrow.INDEX].getNameDisplayed()))
            return WaterArrow.INDEX;

        else if (selectedArrow.equals(arrows[StoneArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[StoneArrow.INDEX].getNameDisplayed()))
            return StoneArrow.INDEX;

        else if (selectedArrow.equals(arrows[StormArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[StormArrow.INDEX].getNameDisplayed()))
            return StormArrow.INDEX;

        else if (selectedArrow.equals(arrows[IceArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[IceArrow.INDEX].getNameDisplayed()))
            return IceArrow.INDEX;

        else if (selectedArrow.equals(arrows[LightningArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[LightningArrow.INDEX].getNameDisplayed()))
            return LightningArrow.INDEX;

        else if (selectedArrow.equals(arrows[LightArrow.INDEX].getName()) ||
                selectedArrow.equals(arrows[LightArrow.INDEX].getNameDisplayed()))
            return LightArrow.INDEX;

        else
            return ShadowArrow.INDEX;
    }

    /**
     * If you want to know how much arrows per category are in the inventory of the ActivePlayer, use this method.
     * use it like <code>arrowCountInventory()[FireArrow.INDEX]</code> or any other Arrow.INDEX.
     * <p>
     * If you're thinking of using this method frequently without chaning the number of arrows, you should save the
     * value in an tempory <code>final int[] temp = arrowCountInventory();</code> (for example if you want to use it for
     * each arrow once).
     *
     * @return int[] - an array of the size <code>ArrowHelper.NUMBER_OF_ARROW_TYPES</code>
     * (it's 8 like the number of kinds of arrows).
     */
    public static int[] arrowCountInventory(final InventoryLike inventory) {
        int[] arrowsCount = new int[NUMBER_OF_ARROW_TYPES];

        for (Item item : inventory.getItems()) {
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

    /** returns an empty int array of length <code>ArrowHelper.NUMBER_OF_ARROW_TYPES</code>. */
    public static int[] emptyArrowCount () {
        /*
        final int[] array = new int[NUMBER_OF_ARROW_TYPES];
        for(int i = 0; i < array.length; i++) {
            array[i] = 0;
        }
        return array;
        */

        // the array is automatically filled with 0 after the initialization.
        return new int[NUMBER_OF_ARROW_TYPES];
    }

    /**
     * returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     *
     * @param arrowIndex the index of the arrow like anyArrow.ARROW_INDEX
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor (int arrowIndex) {
        switch (arrowIndex) {
            case FireArrow.INDEX:
                return FireArrow.UNIFIED_COLOR;
            case WaterArrow.INDEX:
                return WaterArrow.UNIFIED_COLOR;
            case StoneArrow.INDEX:
                return StoneArrow.UNIFIED_COLOR;
            case StormArrow.INDEX:
                return StormArrow.UNIFIED_COLOR;
            case LightArrow.INDEX:
                return LightArrow.UNIFIED_COLOR;
            case ShadowArrow.INDEX:
                return ShadowArrow.UNIFIED_COLOR;
            case IceArrow.INDEX:
                return IceArrow.UNIFIED_COLOR;
            default:
                return LightningArrow.UNIFIED_COLOR;
        }
    }

    /**
     * returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     *
     * @param arrowClass Any class extends AbstractArrow
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor (Class<? extends AbstractArrow> arrowClass) {
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
        else
            return ShadowArrow.UNIFIED_COLOR;
    }

    /**
     * returns the unifiedColor of the arrow. That is similar to:
     * ...Arrow.UNIFIED_COLOR
     *
     * @param arrowName the name of the arrow like anyArrow.ARROW_NAME
     * @return UNIFIED_COLOR - the standard Color of an arrow
     */
    public static Color getUnifiedColor(String arrowName) {
        return getUnifiedColor(ArrowHelper.arrowNameToIndex(arrowName));
    }

    /** Returns the image of the arrow. If you want to draw that image lot's of times, it's recommended to save it
     * locally to save computing time.
     *
     * @see ArrowHelper#getArrowImage(int, float)
     */
    public static BufferedImage getArrowImage(int selectedArrow) {
        return arrows[selectedArrow].getImage();
    }

    /**
     * Returns the scaled version of a BufferedImage from the selected arrow. Compare with the methods in {@link misc.ImageHelper}, too.
     * If you want to use this method frequently, save the returned BufferedImage somewhere, because scaling an image is no fast Operation or use
     * {@link misc.ImageHelper#scaleBufferedImage(java.awt.image.BufferedImage, float, float, int)} with {@link java.awt.Image#SCALE_FAST}.
     *
     * @param selectedArrow the index of the selected arrow arrow.INDEX
     * @param scaleFactor   the scale Factor in x and in y direction
     * @return the scaled image of the arrow.INDEX or use {@link ArrowHelper#arrowNameToIndex(String)} (ArrowHelper.arrowNameToIndex(arrow.getName()))
     * @see ArrowHelper#getArrowImage(int)
     */
    public static BufferedImage getArrowImage(int selectedArrow, float scaleFactor) {
        return ImageHelper.scaleBufferedImage(getArrowImage(selectedArrow), scaleFactor, scaleFactor, Image.SCALE_SMOOTH);
    }
}

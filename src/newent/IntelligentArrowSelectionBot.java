package newent;

import general.LogFacility;
import general.PfeileContext;
import player.weapon.arrow.ArrowHelper;

import java.util.Random;

/** this an interface for all Bots which allows them to select all arrow related actions more or less intelligible
 * <b> this interface is only for Bots! So do not use it with any Entity. </b> */
public interface IntelligentArrowSelectionBot {

    /** This method is adding all arrowsPreSet to the inventory of the Bot. Right now every arrow is selected randomly.
     * Later, in this method an intelligent algorithm can be found. */
    public default void selectArrowsPreSet () {
        Random randomGen = new Random();

        Bot bot;
        try {
            bot = (Bot) this;
        } catch(ClassCastException e) {
            LogFacility.log(this + " is no subclass of Bot => illegal implementation of IntelligentArrowSelectionBot!");
            throw e;
        }

        switch (BotStrength.Strength) {
            case MISERABLE: {
                for (int i = 0; i < PfeileContext.arrowNumberPreSet().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            }
            case WEAK: {
                for (int i = 0; i < PfeileContext.arrowNumberPreSet().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case NORMAL: {
                for (int i = 0; i < PfeileContext.arrowNumberPreSet().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case STRONG: {
                for (int i = 0; i < PfeileContext.arrowNumberPreSet().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case BRUTAL: {
                for (int i = 0; i < PfeileContext.arrowNumberPreSet().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            }
        }
    }
}

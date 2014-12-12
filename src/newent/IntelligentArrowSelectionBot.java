package newent;

import general.PfeileContext;
import player.weapon.ArrowHelper;

import java.util.Random;

/** this an interface for all Bots which allows them to select all arrow related actions more or less intelligible
 * <b> this interface is only for Bots! So do not use it with any Entity. </b> */
public interface IntelligentArrowSelectionBot {

    /** This method is adding all arrowsPreSet to the inventory of the Bot. Right now every arrow is selected randomly.
     * Later, in this method an intelligent algorithm can be found. */
    public default void selectArrowsPreSet () {
        Random randomGen = new Random();
        // This have to be possible, because every Entity, which calls this method IS a Bot.
        Bot bot = (Bot) this;

        switch (BotStrength.Strength) {
            case MISERABLE: {
                for (int i = 0; i < PfeileContext.ARROW_NUMBER_PRE_SET().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            }
            case WEAK: {
                for (int i = 0; i < PfeileContext.ARROW_NUMBER_PRE_SET().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case NORMAL: {
                for (int i = 0; i < PfeileContext.ARROW_NUMBER_PRE_SET().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case STRONG: {
                for (int i = 0; i < PfeileContext.ARROW_NUMBER_PRE_SET().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            } case BRUTAL: {
                for (int i = 0; i < PfeileContext.ARROW_NUMBER_PRE_SET().get(); i++) {
                    bot.inventory().put(ArrowHelper.instanceArrow(randomGen.nextInt(ArrowHelper.NUMBER_OF_ARROW_TYPES)));
                }
                break;
            }
        }
    }
}

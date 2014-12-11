package newent;

/** these are all possible Strength values of a Bot. Compare it with {@link newent.Bot#Strength()} */
public enum BotStrength implements Comparable<BotStrength> {
    /** All Bots, which doesn't have any kind of intelligence */
    MISERABLE,

    /** All Bots, which do at least something more or less intelligent */
    WEAK,

    /** All Bots, which doesn't behave perfect at least */
    NORMAL,

    /** All Bots, which are able to do something (giving the feeling of a intelligent Bot) */
    STRONG,

    /** All Bots, which can do anything. They are not only intelligent, but also preferred from the computer (without the player noticing it) */
    BRUTAL;

    /** this is the default Strength of a bot. It can be changed during PreWindowScreen. */
    public static BotStrength Strength = BotStrength.NORMAL;

    /** <b>it compares the strength of this and strength.</b> Basically, it does the same like <code>compareTo(...)</code>.
     * <p>
     * if both Bots are equally strong, the return value will be 0. <p>
     * if this bot is weaker then the strength of the other bot, it returns a negative value. <p>
     * if this bot is stronger then the strength of the other bot, it returns a positive value.
     *
     * @param strength the Strength of the other bot
     * @return a positive, negative or 0 value
     */
    public byte compare (BotStrength strength) {
        return (byte) (this.ordinal() - strength.ordinal());
    }
}

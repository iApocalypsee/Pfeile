package newent;

import general.Main;

/**
 * If every living entity can be poised. This class handles the effects. Every LivingEntity has one Object of Poison.
 */
public class Poison {

    /** This private instance is used to access for example the life and team membership. Do not change other properties
     * except the life from this class.
     */
    private final LivingEntity entity;

    /** The amount the entity is poisoned. */
    private int poisonStat = 0;

    /** The number of turns, after the Entity has been poisoned. */
    private int turnsAfterPoisoning = 0;

    /** A value, which reduces the impact the poison does. */
    private float poisonResistance;

    /** Every living entity must have a poison stat. The poison resistance not only decreases the amount of poison take
     * (and therefore decreases the damage), but also accelerate the amount of poison a little bit. Only values between
     * 0 and 1 are reasonable.
     *
     * @param entity the living entity --> therefore it's <code>this</code>
     * @param poisonResistance a value between 0 and 1, the higher the value, the less impact the poison will have.
     */
    Poison (LivingEntity entity, float poisonResistance) {
        this.poisonResistance = poisonResistance;
        this.entity = entity;

        // At the beginning of each turn, all the members of each team need to calculate the poison effect
        Main.getContext().getTurnSystem().onTurnGet().registerJava(jf -> {
            if (poisonStat > 0) {
                if (jf.isInTeam((CanHoldTeamContract) this.entity))
                    calculatePoisonEffects();
            }
        });
    }

    /** The living entity is poisoned.
     *
     * @param amount The higher the value the stronger the poison. I wouldn't recommend to put vales over about 70 to
     *               this method, because the damage would lead to death instantly.
     */
    public void poisoned (int amount) {
        poisonStat += amount * (1 - poisonResistance);
        turnsAfterPoisoning = 0;
    }

    /** Reduces the life of the person. The poison shows it's effects at the beginning of the living entities turn. */
    private void calculatePoisonEffects () {
        // Change the multiplier of 0.5 here to increase or decrease poison damage.
        entity.getLife().changeLife(-poisonStat * 0.5);
        reducePoison();
    }

    /** The metabolism is slowly decreasing the amount of poison in one's body. */
    private void reducePoison () {
        // TODO introduce a more realistic calculation
        int change = - (int) (poisonStat * 0.1 + 10 * poisonResistance + 2 - turnCurve());

        if (poisonStat - change <= 0)
            poisonStat = 0;
        else if (poisonStat - change < poisonStat)
            poisonStat = poisonStat - change;
        // the poisonStat can't increase except with poisoned(int amount)
    }

    /** Slightly increases the poison effect. Well, actually it is only slowing down the decrease. This effect should
     * resemble the effect, that most poisons need to time to achieve their maximum strength. */
    private float turnCurve () {
        float increase = - (turnsAfterPoisoning - 2.5f) * (turnsAfterPoisoning - 2.5f) + 6f;
        if (increase > 0)
            return increase;
        else
            return 0;
    }
}

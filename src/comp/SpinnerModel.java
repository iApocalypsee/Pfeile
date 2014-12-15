package comp;

/**
 * This is a Spinner model for just for numbers (int values).
 * an implementation with double is not needed right now, however it would be easy to do it as every method stays the same.
 * */
public class SpinnerModel {
    private int value;
    private int minimum;
    private int maximum;
    private int stepSize;

    /**
     * Constructs a <code>SpinnerModel</code> with the specified
     * <code>currentValue</code>, <code>minimum</code>/<code>maximum</code> bounds,
     * and <code>stepSize</code>.
     *
     * @param currentValue the current value of the model
     * @param minimum the first number in the sequence
     * @param maximum the last number in the sequence
     * @param stepSize the difference between elements of the sequence
     * @throws IllegalArgumentException   if the following expression is false:
     *     <code>minimum &lt;= value &lt;= maximum</code>
     */
    public SpinnerModel (int currentValue, int minimum, int maximum, int stepSize) {
        if (currentValue < minimum || currentValue > maximum) {
            throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
        }
        this.value = currentValue;
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepSize = stepSize;
    }


    /**
     * Returns the value of the current element of the sequence.
     *
     * @return the value property
     * @see #setValue
     */
    public int getValue () {
        return value;
    }

    /** sets the current value of the spinner model.
     * <code>if (currentValue < minimum) { currentValue = minimum }</code>
     * and
     * <code>if (currentValue > maximum) { currentValue = maximum }</code>
     * so there won't be an Exception.
     */
    public void setValue (int currentValue) {
        if (currentValue < minimum)
            currentValue = minimum;
        if (currentValue > maximum)
            currentValue = maximum;
        value = currentValue;
    }

    /**
     * Returns the first number in this sequence.
     *
     * @return the value of the <code>minimum</code> property
     * @see #setMinimum
     */
    public int getMinimum () {
        return minimum;
    }

    /** sets the <code>minimum</code> of this SpinnerModel. The new Minimum has to be smaller than <code>maximum</code>.
     * If <code>value</code> is smaller than <code>minimum</code> --> <code>value = minimum</code>.
     * @param minimum the new minimum value the spinner can have
     * @throws java.lang.IllegalArgumentException   if (minimum < maximum) is false
     */
    public void setMinimum (int minimum) {
        if (minimum < maximum)
            throw new IllegalArgumentException("(minimum < maximum) is false");
        if (value < minimum)
            value = minimum;
        this.minimum = minimum;
    }

    /** the last number of the sequence
     *
     * @return the maximum value
     * @see #getMinimum()
     * @see #setMaximum(int)
     */
    public int getMaximum () {
        return maximum;
    }
    /** sets the <code>maximum</code> of this SpinnerModel. The new Maximum has to be smaller than <code>minimum</code>.
     * If <code>value</code> is bigger than <code>maximum</code> --> <code>value = maximum</code>.
     * @param maximum the new minimum value the spinner can have
     * @throws java.lang.IllegalArgumentException   if (maximum > minimum) is false
     */
    public void setMaximum (int maximum) {
        if (maximum < minimum)
            throw new IllegalArgumentException("(maximum > minimum) is false");
        if (value > maximum)
            value = maximum;
        this.maximum = maximum;
    }

    /**
     * Returns the size of the value change computed by the
     * <code>getNextValue</code>
     * and <code>getPreviousValue</code> methods.
     *
     * @return the value of the <code>stepSize</code> property
     * @see #setStepSize
     */
    public int getStepSize () {
        return stepSize;
    }
    public void setStepSize (int stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * Returns the previous number in the sequence.
     *
     * @return <code>value - stepSize</code> or <code>minimum</code>
     *
     * @see #getNextValue()
     * @see #setStepSize
     * @see #getMinimum()
     */
    public int getPreviousValue () {
        if (value - stepSize < minimum)
            return minimum;
        else return (value - stepSize);
    }

    /**
     * Returns the next number in the sequence.
     *
     * @return <code>value + stepSize</code> or <code>maximum</code>
     *
     * @see #getPreviousValue()
     * @see #setStepSize
     * @see #getMaximum()
     */
    public int getNextValue () {
        if (value + stepSize >= maximum)
            return maximum;
        else return (value + stepSize);
    }
}

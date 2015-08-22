package comp;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Josip Palavra
 */
public class RangeSpinnerModel implements ISpinnerModel<Integer> {

	private int minimum;
	private int maximum;
	private int stepSize;
	private int current;

    public RangeSpinnerModel (int currentValue, int minimum, int maximum, int stepSize) {
        this.current = currentValue;
        this.minimum = minimum;
        this.maximum = maximum;
        this.stepSize = stepSize;
    }

    /** The current value is the average value of minimum and maximum <code>(minimum + maximum) / 2.0</code> */
	public RangeSpinnerModel(int minimum, int maximum, int stepSize) {
        this.current = (int) ((minimum + maximum) / 2.0);
		this.minimum = minimum;
		this.maximum = maximum;
		this.stepSize = stepSize;
	}

	public RangeSpinnerModel(SpinnerModel oldModel) {
		minimum = oldModel.getMinimum();
		maximum = oldModel.getMaximum();
		stepSize = oldModel.getStepSize();
		current = oldModel.getValue();
	}

    @Override
	public Integer getMinimum() {
		return minimum;
	}

    @Override
	public Integer getMaximum() {
		return maximum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getStepSize() {
		return stepSize;
	}

	public void setStepSize(int stepSize) {
		this.stepSize = stepSize;
	}

	@Override
	public Integer next() {
		current = peekNext();
		return current;
	}

	@Override
	public Integer previous() {
		current = peekPrevious();
		return current;
	}

	@Override
	public Integer peekNext() {
		int ret;
		if(current + stepSize > maximum) {
            //the diff value leads to strange changes, e.g.:
            // minimum = 0; maximum = 50; stepSize = 1; current = 50;
            // --> ret = 1 [instead of 0]

			//int diff = (current + stepSize) - maximum;
			//ret = minimum + diff;
            ret = minimum;
		} else ret = current + stepSize;
		return ret;
	}

	@Override
	public Integer peekPrevious() {
		int ret;
		if(current - stepSize < minimum) {
            // no diff. Compare with peekNext()

			//int diff = minimum - (current - stepSize);
			//ret = maximum - diff;
            ret = maximum;
		} else ret = current - stepSize;
		return ret;
	}

	@Override
	public Integer getCurrent() {
		return current;
	}

	@Override
	public List<Integer> getValues() {
		List<Integer> ret = new LinkedList<>();
		for(int value = minimum; value <= maximum; value += stepSize) {
			ret.add(value);
		}
		return ret;
	}
}

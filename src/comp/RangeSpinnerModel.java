package comp;

import java.util.*;
import java.util.List;

/**
 * @author Josip Palavra
 */
public class RangeSpinnerModel implements ISpinnerModel<Integer> {

	private int minimum;
	private int maximum;
	private int stepSize;
	private int current;

	public RangeSpinnerModel(int minimum, int maximum, int stepSize) {
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

	public int getMinimum() {
		return minimum;
	}

	public int getMaximum() {
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
			int diff = (current + stepSize) - maximum;
			ret = minimum + diff;
		} else ret = current + stepSize;
		return ret;
	}

	@Override
	public Integer peekPrevious() {
		int ret;
		if(current - stepSize < minimum) {
			int diff = minimum - (current - stepSize);
			ret = maximum - diff;
		} else ret = current - stepSize;
		return ret;
	}

	@Override
	public Integer getCurrent() {
		return current;
	}

	@Override
	public List<Integer> getValues() {
		List<Integer> ret = new LinkedList<Integer>();
		for(int value = minimum; value < maximum; value += stepSize) {
			ret.add(value);
		}
		return ret;
	}
}

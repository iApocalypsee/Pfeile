package comp;

import java.util.List;

/**
 * @author Josip Palavra
 */
public class GenericReferenceSpinnerModel<A> implements ISpinnerModel<A> {

	private java.util.List<A> values;
	private A current;

	public GenericReferenceSpinnerModel(java.util.List<A> values) {
		this.values = values;
	}

	public List<A> getValues() {
		return values;
	}

	public void setValues(List<A> values) {
		if(values == null) throw new NullPointerException();
		this.values = values;
		current = values.get(0);
	}

	public A getCurrent() {
		return current;
	}

	public A peekNext() {
		A ret;
		int currentIndex = indexOfCurrent();
		if(currentIndex + 1 >= values.size()) {
			ret = values.get(0);
		} else {
			ret = values.get(currentIndex + 1);
		}
		return ret;
	}

	public A next() {
		current = peekNext();
		return current;
	}

	public A peekPrevious() {
		A ret;
		int currentIndex = indexOfCurrent();
		if(currentIndex - 1 < 0) {
			ret = values.get(values.size() - 1);
		} else {
			ret = values.get(currentIndex - 1);
		}
		return ret;
	}

	public A previous() {
		current = peekPrevious();
		return current;
	}

}

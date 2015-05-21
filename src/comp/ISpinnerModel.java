package comp;

/**
 * @author Josip Palavra
 */
public interface ISpinnerModel<A> {

	A next();
	A previous();

	A peekNext();
	A peekPrevious();

	A getCurrent();

    default A getMinimum() {
        return getValues().get(0);
    }

    default A getMaximum() {
        final java.util.List<A> values = getValues();
        return values.get(values.size() - 1);
    }

    default int indexOf(A x) {
        return getValues().indexOf(x);
    }

    default int indexOfCurrent() {
        return indexOf(getCurrent());
    }

    default String currentAsString() {
        return getCurrent().toString();
    }

	java.util.List<A> getValues();

}

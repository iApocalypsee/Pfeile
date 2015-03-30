package comp;

/**
 * @author Josip Palavra
 */
public interface ISpinnerModel<A> {

	public A next();
	public A previous();

	public A peekNext();
	public A peekPrevious();

	public A getCurrent();

    default public A getMinimum() {
        return getValues().get(0);
    }

    default public A getMaximum() {
        final java.util.List<A> values = getValues();
        return values.get(values.size() - 1);
    }

	public java.util.List<A> getValues();

}

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

	public java.util.List<A> getValues();

}

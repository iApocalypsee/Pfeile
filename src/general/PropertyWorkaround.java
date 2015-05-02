package general;

/**
 * Created by jolecaric on 30/04/15.
 */
public class PropertyWorkaround {

    private static Property$ propModule = Property$.MODULE$;

    private PropertyWorkaround() {
    }

    public static <A> Property<A> apply() {
        return propModule.apply();
    }

    public static <A> Property<A> apply(A initialValue) {
        return propModule.apply(initialValue);
    }

    public static <A> Property<A> withValidation() {
        return propModule.withValidation();
    }

    public static <A> Property<A> withValidation(A x) {
        return propModule.withValidation(x);
    }
}

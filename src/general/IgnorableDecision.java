package general;

/**
 * Annotation for any decision fields in a subclass of {@link general.Choice}
 * when these decisions should not be included in the {@link general.Choice#decisions()}
 * list.
 */
public @interface IgnorableDecision {
}

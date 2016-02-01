package comp;

import scala.Function0;

import static general.JavaInterop.func;

public interface TextContainerComponent {

    String getText();

    default void setText(String text) {
        setText(func(() -> text));
    }

    void setText(Function0<String> provider);

}

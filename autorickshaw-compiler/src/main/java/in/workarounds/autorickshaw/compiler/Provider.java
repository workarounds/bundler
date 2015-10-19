package in.workarounds.autorickshaw.compiler;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by madki on 17/10/15.
 */
public interface Provider {
    Types typeUtils();
    Elements elementUtils();
    Filer filer();
    Messager messager();

    void error(Element element, String msg, Object... args);
    void message(Element element, String msg, Object... args);
    void warn(Element element, String msg, Object... args);

    void reportError();
    boolean hasErrorOccurred();
}

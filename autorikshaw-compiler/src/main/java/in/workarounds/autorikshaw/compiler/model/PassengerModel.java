package in.workarounds.autorikshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import in.workarounds.autorikshaw.compiler.support.TypeMatcher;

/**
 * Created by madki on 16/10/15.
 */
public class PassengerModel {
    private TypeMirror type;
    private String label;

    public PassengerModel(Element element, Types typeUtils) throws IllegalArgumentException {
        label = element.getSimpleName().toString();
        type = element.asType();
        isValid();
    }

    private void isValid() throws IllegalArgumentException {
        if(!TypeMatcher.isTypeSupported(type)) {
            throw new IllegalArgumentException(String.format("Unsupported type for passenger %s", label));
        }
    }

    public TypeMirror getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}

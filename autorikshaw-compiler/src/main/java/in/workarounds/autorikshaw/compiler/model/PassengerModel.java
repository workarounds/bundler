package in.workarounds.autorikshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import in.workarounds.autorikshaw.compiler.support.ParsedType;

/**
 * Created by madki on 16/10/15.
 */
public class PassengerModel {
    private TypeMirror type;
    private String label;
    public ParsedType parsedType;


    public PassengerModel(Element element) throws IllegalArgumentException {
        label = element.getSimpleName().toString();
        type = element.asType();
        parsedType = new ParsedType();
    }

    public TypeMirror getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}

package in.workarounds.autorickshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import in.workarounds.autorickshaw.annotations.Passenger;
import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.model.type.RootType;
import in.workarounds.autorickshaw.compiler.support.ParserTypeVisitor;
import in.workarounds.autorickshaw.compiler.support.TypeMatcher;

/**
 * Created by madki on 16/10/15.
 */
public class PassengerModel {
    private String label;
    private RootType type;

    public PassengerModel(Element element, Provider provider) {
        label = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();
        type = typeMirror.accept(ParserTypeVisitor.getInstance(), null);

        checkValidity(element, provider);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public RootType getType() {
        return type;
    }

    public void setType(RootType type) {
        this.type = type;
    }

    private void checkValidity(Element element, Provider provider) {
        if(!TypeMatcher.isSupported(type)) {
            provider.error(element,
                    "Field %s annotated with @%s has an unsupported type",
                    label,
                    Passenger.class.getSimpleName()
            );
            provider.reportError();
        }
    }

}

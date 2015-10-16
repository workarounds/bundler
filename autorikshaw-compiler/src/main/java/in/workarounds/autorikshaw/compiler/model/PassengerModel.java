package in.workarounds.autorikshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import in.workarounds.autorikshaw.annotations.Passenger;
import in.workarounds.autorikshaw.compiler.RikshawProcessor;
import in.workarounds.autorikshaw.compiler.model.type.RootType;
import in.workarounds.autorikshaw.compiler.support.ParserTypeVisitor;
import in.workarounds.autorikshaw.compiler.support.TypeMatcher;

/**
 * Created by madki on 16/10/15.
 */
public class PassengerModel {
    private String label;
    private RootType type;

    public PassengerModel(Element element, RikshawProcessor processor) {
        label = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();
        type = typeMirror.accept(ParserTypeVisitor.getInstance(), null);

        checkValidity(element, processor);
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

    private void checkValidity(Element element, RikshawProcessor processor) {
        if(!TypeMatcher.isSupported(type)) {
            processor.error(element,
                    "Field %s annotated with @%s has an unsupported type",
                    label,
                    Passenger.class.getSimpleName()
            );
            processor.errorStatus = true;
        }
    }

}

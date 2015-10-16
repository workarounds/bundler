package in.workarounds.autorickshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import in.workarounds.autorickshaw.compiler.RickshawProcessor;

/**
 * Created by madki on 16/10/15.
 */
public class PassengerModel {
    private String label;
    private in.workarounds.autorickshaw.compiler.model.type.RootType type;

    public PassengerModel(Element element, RickshawProcessor processor) {
        label = element.getSimpleName().toString();
        TypeMirror typeMirror = element.asType();
        type = typeMirror.accept(in.workarounds.autorickshaw.compiler.support.ParserTypeVisitor.getInstance(), null);

        checkValidity(element, processor);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public in.workarounds.autorickshaw.compiler.model.type.RootType getType() {
        return type;
    }

    public void setType(in.workarounds.autorickshaw.compiler.model.type.RootType type) {
        this.type = type;
    }

    private void checkValidity(Element element, RickshawProcessor processor) {
        if(!in.workarounds.autorickshaw.compiler.support.TypeMatcher.isSupported(type)) {
            processor.error(element,
                    "Field %s annotated with @%s has an unsupported type",
                    label,
                    in.workarounds.autorickshaw.annotations.Passenger.class.getSimpleName()
            );
            processor.errorStatus = true;
        }
    }

}

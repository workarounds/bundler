package in.workarounds.autorickshaw.compiler.model;

import com.squareup.javapoet.TypeName;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import in.workarounds.autorickshaw.annotations.Cargo;
import in.workarounds.autorickshaw.compiler.Provider;
import in.workarounds.autorickshaw.compiler.support.SupportResolver;

/**
 * Created by madki on 21/10/15.
 */
public class CargoModel {
    private Provider provider;

    private String label;
    private TypeName typeName;

    public CargoModel(Element element, Provider provider) {
        this.provider = provider;

        label = element.getSimpleName().toString();
        typeName = TypeName.get(element.asType());
        checkModifiers(element);
        checkIfValidType(element);
    }

    private void checkModifiers(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.FINAL)
                || modifiers.contains(Modifier.PROTECTED)
                || modifiers.contains(Modifier.PRIVATE)
                ) {
            provider.error(element, "Error at: %s, Fields annotated with @%s should not be final and should be public.", label, Cargo.class.getSimpleName());
            provider.reportError();
        }
    }

    private void checkIfValidType(Element element) {
        if(!SupportResolver.isSupportedType(this, provider.elementUtils())) {
            provider.error(element, "Error at: %s, Unsupported type %s", label, typeName);
            provider.reportError();
        }
    }

    public String getLabel() {
        return label;
    }

    public TypeName getTypeName() {
        return typeName;
    }
}

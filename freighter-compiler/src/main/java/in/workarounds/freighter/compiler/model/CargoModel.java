package in.workarounds.freighter.compiler.model;

import com.squareup.javapoet.TypeName;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import in.workarounds.freighter.annotations.Cargo;
import in.workarounds.freighter.compiler.Provider;
import in.workarounds.freighter.compiler.support.SupportResolver;
import in.workarounds.freighter.compiler.support.helper.TypeHelper;
import in.workarounds.freighter.compiler.util.StringUtils;

/**
 * Created by madki on 21/10/15.
 */
public class CargoModel {
    private Provider provider;

    private String label;
    private TypeName typeName;
    private TypeHelper helper;

    public CargoModel(Element element, Provider provider) {
        this.provider = provider;

        label = element.getSimpleName().toString();
        typeName = TypeName.get(element.asType());
        helper = SupportResolver.getHelper(typeName, provider.elementUtils());
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
        if(helper == null) {
            provider.error(element, "Error at: %s, Unsupported type %s", label, typeName);
            provider.reportError();
        }
    }

    public String getKeyConstant() {
        return StringUtils.getConstantName(label);
    }

    public String getBundleMethodSuffix() {
        return helper.getBundleMethodSuffix();
    }

    public boolean requiresCasting() {
        return helper.requiresCasting();
    }

    public String getLabel() {
        return label;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public TypeHelper getHelper() {
        return helper;
    }
}

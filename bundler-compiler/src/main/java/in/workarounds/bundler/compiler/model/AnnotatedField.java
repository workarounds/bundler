package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.support.TypeHelperFactory;
import in.workarounds.bundler.compiler.support.helper.TypeHelper;
import in.workarounds.bundler.compiler.util.StringUtils;
import in.workarounds.bundler.compiler.util.names.VarName;

/**
 * Created by madki on 21/10/15.
 */
public class AnnotatedField {
    private Provider provider;

    private String label;
    private TypeName typeName;
    private TypeHelper helper;
    private Class<?> annotation;

    public AnnotatedField(Element element, Provider provider, Class<?> annotation) {
        this.provider = provider;

        this.annotation = annotation;
        label = element.getSimpleName().toString();
        typeName = TypeName.get(element.asType());
        helper = TypeHelperFactory.getHelper(typeName, provider.elementUtils());
        checkModifiers(element);
        checkIfValidType(element);
    }

    private void checkModifiers(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(Modifier.FINAL)
                || modifiers.contains(Modifier.PROTECTED)
                || modifiers.contains(Modifier.PRIVATE)
                ) {
            provider.error(element, "Error at: %s, Fields annotated with @%s should not be final and should be public.", label, annotation.getSimpleName());
            provider.reportError();
        }
    }

    private void checkIfValidType(Element element) {
        if(helper == null) {
            provider.error(element, "Error at: %s, Unsupported type %s annotated with @%s", label, typeName, annotation.getSimpleName());
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

    public ParameterSpec getAsParameter(Modifier... modifiers) {
        return ParameterSpec.builder(typeName, VarName.from(this))
                .addModifiers(modifiers)
                .build();
    }

    public FieldSpec getAsField(Modifier... modifiers) {
        return FieldSpec.builder(typeName, VarName.from(this))
                .addModifiers(modifiers)
                .build();
    }
}

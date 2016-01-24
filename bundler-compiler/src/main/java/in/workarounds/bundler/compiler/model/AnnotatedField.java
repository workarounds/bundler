package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.DefaultSerializer;
import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.helper.TypeHelper;
import in.workarounds.bundler.compiler.helper.TypeHelperFactory;
import in.workarounds.bundler.compiler.util.StringUtils;
import in.workarounds.bundler.compiler.util.Utils;
import in.workarounds.bundler.compiler.util.names.ClassProvider;
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
    private TypeName serializer;

    public AnnotatedField(Element element, Provider provider, Class<?> annotation, TypeName serializer) {
        this.provider = provider;

        this.annotation = annotation;
        this.serializer = serializer;

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
        TypeName fieldType = typeName.isPrimitive() ? typeName.box() : typeName;
        return FieldSpec.builder(fieldType, VarName.from(this))
                .addModifiers(modifiers)
                .build();
    }

    /**
     * Helper class to return the serializer of a @Arg as TypeName
     * @param arg the @Arg annotation whose serializer is to be retrieved
     * @return serializer of @Arg as TypeName if it's valid else null
     */
    public static TypeName serializer(Arg arg) {
        TypeName serializer;
        boolean isValid;
        try {
            Class<?> clazz = arg.serializer();
            serializer = TypeName.get(clazz);
            isValid = isValidSerializer(clazz);
        } catch (MirroredTypeException mte) {
            TypeMirror typeMirror = mte.getTypeMirror();
            serializer = TypeName.get(typeMirror);
            isValid = isValidSerializer(typeMirror);
        }

        if (isValid) {
            return serializer;
        }

        return null;
    }

    /**
     * Helper class to return the serializer of a @State as TypeName
     * @param state the @State annotation whose serializer is to be retrieved
     * @return serializer of @State as TypeName if it's valid else null
     */
    public static TypeName serializer(State state) {
        TypeName serializer;
        try {
            Class<?> clazz = state.serializer();
            serializer = TypeName.get(clazz);
        } catch (MirroredTypeException mte) {
            serializer = TypeName.get(mte.getTypeMirror());
        }
        return serializer;
    }

    /**
     * @param serializer the serializer Class to be validated
     * @return true if the given serializer is DefaultSerializer or implements Serializer
     */
    private static boolean isValidSerializer(Class<?> serializer) {
        return serializer.equals(DefaultSerializer.class) ||
                Utils.implementsInterface(serializer, ClassProvider.serializer.toString());
    }

    /**
     * @param serializer the typeMirror of the serializer to be validated
     * @return true if the given serializer is DefaultSerializer or implements Serializer
     */
    private static boolean isValidSerializer(TypeMirror serializer) {
        return serializer.toString().equals(DefaultSerializer.class.getName()) ||
                Utils.implementsInterface(serializer, ClassProvider.serializer.toString());
    }
}

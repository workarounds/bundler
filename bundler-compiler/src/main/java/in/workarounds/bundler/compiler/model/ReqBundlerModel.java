package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import in.workarounds.bundler.annotations.Arg;
import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.annotations.State;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.util.names.ClassProvider;

/**
 * Created by madki on 16/10/15.
 */
public class ReqBundlerModel {
    private static final String ACTIVITY = "android.app.Activity";
    private static final String FRAGMENT = "android.app.Fragment";
    private static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    private static final String SERVICE = "android.app.Service";

    private VARIETY variety;
    private ClassName className;
    private Element element;
    private String bundlerMethodName;
    private boolean requireAll;

    private List<StateModel> states;
    private List<ArgModel> args;

    public ReqBundlerModel(Element element, Provider provider) {
        init(element, provider);

        args = retrieveArgs(element, provider);
        states = retrieveStates(element, provider);
   }

    public ReqBundlerModel(Element element, ReqBundlerModel superClass, Provider provider) {
        init(element, provider);

        RequireBundler annotation = element.getAnnotation(RequireBundler.class);
        if(annotation.inheritState()) {
            states = getInheritedFields(retrieveStates(element, provider), superClass.getStates());
        } else {
            states = retrieveStates(element, provider);
        }

        if(annotation.inheritArgs()) {
            args = getInheritedFields(retrieveArgs(element, provider), superClass.getArgs());
        } else {
            args = retrieveArgs(element, provider);
        }
    }

    private void init(Element element,Provider provider) {
        if (element.getKind() != ElementKind.CLASS) {
            provider.error(element, "@%s annotation used on a non-class element %s",
                    RequireBundler.class.getSimpleName(),
                    element.getSimpleName());
            provider.reportError();
            return;
        }

        this.element = element;
        RequireBundler annotation = element.getAnnotation(RequireBundler.class);
        this.bundlerMethodName = annotation.bundlerMethod();
        this.requireAll = annotation.requireAll();

        variety = getVariety((TypeElement) element, provider.typeUtils());
        String qualifiedName = ((TypeElement) element).getQualifiedName().toString();
        className = ClassName.bestGuess(qualifiedName);

    }

    private <T extends AnnotatedField> List<T> getInheritedFields(List<T> currentFields, List<T> superFields) {
        List<T> tempFields = new ArrayList<>();
        tempFields.addAll(superFields);

        for (AnnotatedField field: currentFields) {
            removeIfLabelPresent(field.getLabel(), tempFields);
        }

        tempFields.addAll(currentFields);

        return tempFields;
    }

    private void removeIfLabelPresent(String label, List<? extends AnnotatedField> fields) {
        AnnotatedField foundField = null;
        for(AnnotatedField field: fields) {
            if(field.getLabel().equals(label)) {
                foundField = field;
            }
        }
        if (foundField != null) {
            fields.remove(foundField);
        }
    }

    private List<StateModel> retrieveStates(Element element, Provider provider) {
        List<StateModel> tempStates = new ArrayList<>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            State instanceState = enclosedElement.getAnnotation(State.class);

            if (instanceState != null) {
                TypeName serializer = AnnotatedField.serializer(instanceState);
                if(serializer == null) reportInvalidSerializer(enclosedElement, State.class, provider);
                StateModel state = new StateModel(enclosedElement, provider, serializer);
                tempStates.add(state);
            }
        }
        return tempStates;
    }

    private List<ArgModel> retrieveArgs(Element element, Provider provider) {
        List<ArgModel> tempArgs = new ArrayList<>();
        for (Element enclosedElement : element.getEnclosedElements()) {
            Arg arg = enclosedElement.getAnnotation(Arg.class);

            if (arg != null) {
                TypeName serializer = AnnotatedField.serializer(arg);
                if(serializer == null) reportInvalidSerializer(enclosedElement, Arg.class, provider);
                ArgModel argModel = new ArgModel(enclosedElement, provider, requireAll(), serializer);
                tempArgs.add(argModel);
            }
        }
        return tempArgs;
    }

    private void reportInvalidSerializer(Element element, Class annotation, Provider provider) {
        provider.error(element,
                "The serializer provided with @%s annotation does not implement %s. Please provide a valid serializer",
                annotation.getSimpleName(), ClassProvider.serializer.simpleName());
    }

    private VARIETY getVariety(TypeElement element, Types typeUtils) {
        // Check subclassing
        TypeElement currentClass = element;
        while (true) {
            TypeMirror superClassType = currentClass.getSuperclass();

            if (superClassType.getKind() == TypeKind.NONE) {
                // Basis class (java.lang.Object) reached, so exit
                return VARIETY.OTHER;
            }

            if (getVariety(superClassType.toString()) != VARIETY.OTHER) {
                // Required super class found
                return getVariety(superClassType.toString());
            }

            // Moving up in inheritance tree
            currentClass = (TypeElement) typeUtils.asElement(superClassType);
        }
    }

    private VARIETY getVariety(String className) {
        switch (className) {
            case ACTIVITY:
                return VARIETY.ACTIVITY;
            case FRAGMENT:
                return VARIETY.FRAGMENT;
            case FRAGMENT_V4:
                return VARIETY.FRAGMENT_V4;
            case SERVICE:
                return VARIETY.SERVICE;
            default:
                return VARIETY.OTHER;
        }
    }

    public List<ArgModel> getRequiredArgs() {
        List<ArgModel> requiredArgs = new ArrayList<>();

        for (ArgModel arg : getArgs()) {
            if (arg.isRequired()) {
                requiredArgs.add(arg);
            }
        }
        return requiredArgs;
    }

    public boolean requireAll() {
        return this.requireAll;
    }

    public String getBundlerMethodName() {
        return this.bundlerMethodName;
    }

    public Element getElement() {
        return element;
    }

    public VARIETY getVariety() {
        return variety;
    }

    public String getSimpleName() {
        return className.simpleName();
    }

    public String getPackageName() {
        return className.packageName();
    }

    public ClassName getClassName() {
        return className;
    }

    public enum VARIETY {
        ACTIVITY,
        FRAGMENT,
        FRAGMENT_V4,
        SERVICE,
        OTHER
    }

    public List<ArgModel> getArgs() {
        return args;
    }

    public List<StateModel> getStates() {
        return states;
    }
}

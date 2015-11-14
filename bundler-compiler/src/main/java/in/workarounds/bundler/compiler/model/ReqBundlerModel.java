package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.ClassName;

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

        args = new ArrayList<>();
        states = new ArrayList<>();

        for (Element enclosedElement : element.getEnclosedElements()) {
            Arg arg = enclosedElement.getAnnotation(Arg.class);
            State instanceState = enclosedElement.getAnnotation(State.class);

            if (arg != null) {
                ArgModel argModel = new ArgModel(enclosedElement, provider);
                args.add(argModel);
            }

            if (instanceState != null) {
                StateModel state = new StateModel(enclosedElement, provider);
                states.add(state);
            }
        }
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

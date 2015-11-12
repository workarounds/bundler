package in.workarounds.bundler.compiler.model;

import com.squareup.javapoet.ClassName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import in.workarounds.bundler.annotations.RequireBundler;
import in.workarounds.bundler.compiler.Provider;
import in.workarounds.bundler.compiler.util.StringUtils;

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

    private Methods methods;
    private Classes classes;
    private Vars vars;

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

        this.methods = new Methods();
        this.classes = new Classes(provider);
        this.vars = new Vars();
    }

    public class Vars {

        public String target() {
            return StringUtils.getVariableName(getSimpleName());
        }

        public String bundle() {
            return "bundle";
        }

        public String context() {
            return "context";
        }

        public String intent() {
            return "intent";
        }

        public String defaultVal() {
            return "defaultVal";
        }

        public String parser() {
            return "parser";
        }
    }

    public class Methods {

        public Methods() {
        }

        public String build() {
            return StringUtils.getVariableName(getSimpleName());
        }

        public String parse() {
            return "parse";
        }

        public String into() {
            return "into";
        }

        public String bundle() {
            return "bundle";
        }

        public String intent() {
            return "intent";
        }

        public String start() {
            return "start";
        }

        public String create() {
            return "create";
        }

        public String inject() {
            return "inject";
        }

        public String saveState() {
            return "saveState";
        }

        public String restoreState() {
            return "restoreState";
        }

        public String isNull() {
            return "isNull";
        }
    }

    public class Classes {
        private Provider provider;

        public Classes(Provider provider) {
            this.provider = provider;
        }

        public ClassName bundler() {
            return provider.bundlerClass();
        }

        public ClassName helper() {
            return ClassName.bestGuess(getPackageName() + "." + getSimpleName() + "Bundler");
        }

        public ClassName parser() {
            return innerClass(helper(), "Parser");
        }

        public ClassName builder() {
            return innerClass(helper(), "Builder");
        }

        public ClassName keys() {
            return innerClass(helper(), "Keys");
        }

        private ClassName innerClass(ClassName superClass, String innerClass) {
            return ClassName.bestGuess(superClass.toString() + "." + innerClass);
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

    public Methods methods() {
        return this.methods;
    }

    public Classes classes() {
        return this.classes;
    }

    public Vars vars() {
        return this.vars;
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
}

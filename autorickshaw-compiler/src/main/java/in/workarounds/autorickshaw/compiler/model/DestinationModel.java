package in.workarounds.autorickshaw.compiler.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import in.workarounds.autorickshaw.compiler.RickshawProcessor;

/**
 * Created by madki on 16/10/15.
 */
public class DestinationModel {
    private static final String ACTIVITY    = "android.app.Activity";
    private static final String FRAGMENT    = "android.app.Fragment";
    private static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    private static final String SERVICE     = "android.app.Service";

    private VARIETY variety;
    private String className;

    public DestinationModel(Element element, RickshawProcessor processor) {
        if(element.getKind() != ElementKind.CLASS) {
            processor.error(element, "@%s annotation used on a non-class element %s",
                    in.workarounds.autorickshaw.annotations.Destination.class.getSimpleName(),
                    element.getSimpleName());
            processor.errorStatus = true;
            return;
        }
        variety = getVariety((TypeElement) element, processor.typeUtils);
        className = ((TypeElement) element).getQualifiedName().toString();

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

    public VARIETY getVariety() {
        return variety;
    }

    public String getClassName() {
        return className;
    }

    enum VARIETY {
        ACTIVITY,
        FRAGMENT,
        FRAGMENT_V4,
        SERVICE,
        OTHER
    }
}

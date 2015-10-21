package in.workarounds.autorickshaw.compiler.util;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Created by madki on 17/10/15.
 */
public class Utils {

    /**
     * @param typeMirror    of the element
     * @param interfaceName fully qualified name of the interface
     * @return true is the type defined by typeMirror implements the given interface
     */
    public static boolean implementsInterface(TypeMirror typeMirror, String interfaceName) {
        TypeElement type = getAsElement(typeMirror);
        if (type != null) {
            List<? extends TypeMirror> interfaces = type.getInterfaces();
            for (TypeMirror t : interfaces) {
                if (isSubInterface(t, interfaceName)) {
                    return true;
                }
            }
            return implementsInterface(type.getSuperclass(), interfaceName);
        }
        return false;
    }

    /**
     * @param t             typeMirror of an interface
     * @param qualifiedName fully qualified name of the possible super Interface
     * @return true if the interface defined by typeMirror extends the interface defined by qualifiedName
     */
    public static boolean isSubInterface(TypeMirror t, String qualifiedName) {
        TypeElement typeElement = getAsElement(t);
        if (typeElement != null) {
            if (typeElement.getQualifiedName().contentEquals(qualifiedName)) {
                return true;
            } else {
                return isSubInterface(typeElement.getSuperclass(), qualifiedName);
            }
        }
        return false;
    }

    /**
     * @param typeMirror of the element
     * @return typeElement representation of typeMirror if it's a declaredType null otherwise
     */
    public static TypeElement getAsElement(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            return (TypeElement) ((DeclaredType) typeMirror).asElement();
        }
        return null;
    }

    /**
     * @param typeMirror of the element whose class name is needed
     * @return qualified name if the typeMirror is of declared type else null
     */
    public static String getQualifiedName(TypeMirror typeMirror) {
        TypeElement element = getAsElement(typeMirror);
        if(element != null) {
            return element.getQualifiedName().toString();
        }
        return null;
    }

    /**
     * @param typeMirror of the element whose class name is needed
     * @return simple name if the typeMirror is of declared type else null
     */
    public static String getSimpleName(TypeMirror typeMirror) {
        TypeElement element = getAsElement(typeMirror);
        if(element != null) {
            return element.getSimpleName().toString();
        }
        return null;
    }

    /**
     * @param typeMirror of the element
     * @return ClassName of the typemirror if its a declared type else null
     */
    public static ClassName getClassName(TypeMirror typeMirror) {
        String qualifiedName = getQualifiedName(typeMirror);
        if(qualifiedName != null) {
            return ClassName.bestGuess(qualifiedName);
        }
        return null;
    }

    public static boolean isPrimitiveArray(TypeName typeName) {
        if(typeName instanceof ArrayTypeName) {
            return ((ArrayTypeName) typeName).componentType.isPrimitive();
        }
        return false;
    }

}
